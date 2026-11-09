from dataclasses import dataclass
from datetime import datetime, timezone
import uuid

from app.application.services.seed_factory import build_seed_tasks_for_user
from app.core.security import PasswordHasher, TokenService
from app.domain.entities import UserEntity
from app.domain.exceptions import (
    EmailAlreadyRegisteredError,
    InvalidCredentialsError,
    UnauthorizedError,
    ValidationDomainError,
)
from app.domain.repositories import TaskRepository, UserRepository


@dataclass(slots=True)
class AuthResult:
    access_token: str
    user: UserEntity


class AuthService:
    """Casos de uso de registro, login y resolución de usuario autenticado (SRP + DIP)."""

    def __init__(
        self,
        user_repository: UserRepository,
        task_repository: TaskRepository,
        password_hasher: PasswordHasher,
        token_service: TokenService,
        seed_on_register: bool = False,
    ) -> None:
        self._user_repository = user_repository
        self._task_repository = task_repository
        self._password_hasher = password_hasher
        self._token_service = token_service
        self._seed_on_register = seed_on_register

    async def register(self, name: str, email: str, password: str) -> AuthResult:
        clean_name = name.strip()
        normalized_email = email.strip().lower()

        if len(clean_name) < 2:
            raise ValidationDomainError("El nombre debe tener al menos 2 caracteres")
        if len(password) < 8:
            raise ValidationDomainError("La contraseña debe tener al menos 8 caracteres")

        existing = await self._user_repository.get_by_email(normalized_email)
        if existing is not None:
            raise EmailAlreadyRegisteredError("Este correo ya está registrado")

        now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)
        new_user = UserEntity(
            id=str(uuid.uuid4()),
            name=clean_name,
            email=normalized_email,
            password_hash=self._password_hasher.hash(password),
            created_at_millis=now_ms,
        )
        created_user = await self._user_repository.create(new_user)

        if self._seed_on_register:
            await self._task_repository.replace_all_for_user(
                user_id=created_user.id,
                tasks=build_seed_tasks_for_user(created_user.id, now_ms),
            )

        token = self._token_service.create_access_token(
            subject=created_user.id,
            email=created_user.email,
        )
        return AuthResult(access_token=token, user=created_user)

    async def login(self, email: str, password: str) -> AuthResult:
        normalized_email = email.strip().lower()
        user = await self._user_repository.get_by_email(normalized_email)
        if user is None or not self._password_hasher.verify(password, user.password_hash):
            raise InvalidCredentialsError("Correo o contraseña incorrectos")

        token = self._token_service.create_access_token(
            subject=user.id,
            email=user.email,
        )
        return AuthResult(access_token=token, user=user)

    async def get_user_from_token(self, token: str) -> UserEntity:
        subject = self._token_service.decode_subject(token)
        if not subject:
            raise UnauthorizedError("Sesión expirada o token inválido")
        user = await self._user_repository.get_by_id(subject)
        if user is None:
            raise UnauthorizedError("Usuario no encontrado para esta sesión")
        return user
