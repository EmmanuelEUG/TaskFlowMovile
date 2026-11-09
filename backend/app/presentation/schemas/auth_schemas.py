from pydantic import BaseModel, EmailStr, Field

from app.domain.entities import UserEntity


class RegisterRequest(BaseModel):
    name: str = Field(min_length=2, max_length=120)
    email: EmailStr
    password: str = Field(min_length=8, max_length=128)


class LoginRequest(BaseModel):
    email: EmailStr
    password: str = Field(min_length=1, max_length=128)


class UserResponse(BaseModel):
    id: str
    name: str
    email: str

    @classmethod
    def from_entity(cls, user: UserEntity) -> "UserResponse":
        return cls(id=user.id, name=user.name, email=user.email)


class AuthResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user: UserResponse
