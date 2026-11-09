from typing import Annotated, AsyncIterator

from fastapi import Depends, Request
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from sqlalchemy.ext.asyncio import AsyncSession

from app.application.services.auth_service import AuthService
from app.application.services.task_service import TaskService
from app.core.config import Settings, get_settings
from app.core.security import (
    BcryptPasswordHasher,
    JwtTokenService,
    PasswordHasher,
    TokenService,
)
from app.domain.entities import UserEntity
from app.domain.exceptions import UnauthorizedError
from app.domain.repositories import TaskRepository, UserRepository
from app.infrastructure.repositories.sqlalchemy_task_repository import SqlAlchemyTaskRepository
from app.infrastructure.repositories.sqlalchemy_user_repository import SqlAlchemyUserRepository

_bearer_scheme = HTTPBearer(auto_error=False)


async def get_db_session(request: Request) -> AsyncIterator[AsyncSession]:
    session_factory = request.app.state.session_factory
    async with session_factory() as session:
        yield session


def get_password_hasher() -> PasswordHasher:
    return BcryptPasswordHasher()


def get_token_service(
    settings: Annotated[Settings, Depends(get_settings)],
) -> TokenService:
    return JwtTokenService(
        secret_key=settings.secret_key,
        algorithm=settings.jwt_algorithm,
        expire_minutes=settings.access_token_expire_minutes,
    )


def get_user_repository(
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> UserRepository:
    return SqlAlchemyUserRepository(session=session)


def get_task_repository(
    session: Annotated[AsyncSession, Depends(get_db_session)],
) -> TaskRepository:
    return SqlAlchemyTaskRepository(session=session)


def get_auth_service(
    user_repo: Annotated[UserRepository, Depends(get_user_repository)],
    task_repo: Annotated[TaskRepository, Depends(get_task_repository)],
    hasher: Annotated[PasswordHasher, Depends(get_password_hasher)],
    token_service: Annotated[TokenService, Depends(get_token_service)],
    settings: Annotated[Settings, Depends(get_settings)],
) -> AuthService:
    return AuthService(
        user_repository=user_repo,
        task_repository=task_repo,
        password_hasher=hasher,
        token_service=token_service,
        seed_on_register=settings.seed_on_register,
    )


def get_task_service(
    task_repo: Annotated[TaskRepository, Depends(get_task_repository)],
) -> TaskService:
    return TaskService(task_repository=task_repo)


async def get_current_user(
    credentials: Annotated[HTTPAuthorizationCredentials | None, Depends(_bearer_scheme)],
    auth_service: Annotated[AuthService, Depends(get_auth_service)],
) -> UserEntity:
    if credentials is None or credentials.scheme.lower() != "bearer":
        raise UnauthorizedError("Se requiere token Bearer de autenticación")
    return await auth_service.get_user_from_token(credentials.credentials)
