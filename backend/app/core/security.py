from abc import ABC, abstractmethod
from datetime import datetime, timedelta, timezone

import bcrypt
import jwt


class PasswordHasher(ABC):
    """Contrato para hashing y verificación de contraseñas (DIP / SRP)."""

    @abstractmethod
    def hash(self, plain_password: str) -> str:
        raise NotImplementedError

    @abstractmethod
    def verify(self, plain_password: str, password_hash: str) -> bool:
        raise NotImplementedError


class BcryptPasswordHasher(PasswordHasher):
    """Implementación con bcrypt compatible con Python 3.11–3.14 sin passlib."""

    def hash(self, plain_password: str) -> str:
        salt = bcrypt.gensalt(rounds=12)
        hashed = bcrypt.hashpw(plain_password.encode("utf-8"), salt)
        return hashed.decode("utf-8")

    def verify(self, plain_password: str, password_hash: str) -> bool:
        try:
            return bcrypt.checkpw(
                plain_password.encode("utf-8"),
                password_hash.encode("utf-8"),
            )
        except ValueError:
            return False


class TokenService(ABC):
    """Contrato para creación y validación de tokens de acceso."""

    @abstractmethod
    def create_access_token(self, subject: str, email: str) -> str:
        raise NotImplementedError

    @abstractmethod
    def decode_subject(self, token: str) -> str | None:
        raise NotImplementedError


class JwtTokenService(TokenService):
    """Servicio JWT usando PyJWT."""

    def __init__(self, secret_key: str, algorithm: str, expire_minutes: int) -> None:
        self._secret_key = secret_key
        self._algorithm = algorithm
        self._expire_minutes = expire_minutes

    def create_access_token(self, subject: str, email: str) -> str:
        now = datetime.now(timezone.utc)
        expires_at = now + timedelta(minutes=self._expire_minutes)
        payload = {
            "sub": subject,
            "email": email,
            "iat": int(now.timestamp()),
            "exp": int(expires_at.timestamp()),
        }
        return jwt.encode(payload, self._secret_key, algorithm=self._algorithm)

    def decode_subject(self, token: str) -> str | None:
        try:
            payload = jwt.decode(
                token,
                self._secret_key,
                algorithms=[self._algorithm],
            )
            subject = payload.get("sub")
            return str(subject) if subject else None
        except jwt.PyJWTError:
            return None
