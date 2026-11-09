from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.domain.entities import UserEntity
from app.domain.repositories import UserRepository
from app.infrastructure.db.models import UserModel
from app.infrastructure.repositories.mappers import UserMapper


class SqlAlchemyUserRepository(UserRepository):
    """Implementación SQLAlchemy de UserRepository."""

    def __init__(self, session: AsyncSession) -> None:
        self._session = session

    async def get_by_email(self, email: str) -> UserEntity | None:
        stmt = select(UserModel).where(UserModel.email == email)
        result = await self._session.execute(stmt)
        model = result.scalar_one_or_none()
        return UserMapper.to_domain(model) if model is not None else None

    async def get_by_id(self, user_id: str) -> UserEntity | None:
        stmt = select(UserModel).where(UserModel.id == user_id)
        result = await self._session.execute(stmt)
        model = result.scalar_one_or_none()
        return UserMapper.to_domain(model) if model is not None else None

    async def create(self, user: UserEntity) -> UserEntity:
        model = UserMapper.to_model(user)
        self._session.add(model)
        await self._session.commit()
        await self._session.refresh(model)
        return UserMapper.to_domain(model)
