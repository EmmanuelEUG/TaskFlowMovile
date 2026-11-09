from typing import Annotated

from fastapi import APIRouter, Depends, status

from app.application.services.auth_service import AuthService
from app.domain.entities import UserEntity
from app.presentation.dependencies import get_auth_service, get_current_user
from app.presentation.schemas.auth_schemas import (
    AuthResponse,
    LoginRequest,
    RegisterRequest,
    UserResponse,
)

router = APIRouter(tags=["Autenticación"])


@router.post(
    "/register",
    response_model=AuthResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Registrar una nueva cuenta de usuario",
)
async def register(
    payload: RegisterRequest,
    auth_service: Annotated[AuthService, Depends(get_auth_service)],
) -> AuthResponse:
    result = await auth_service.register(
        name=payload.name,
        email=payload.email,
        password=payload.password,
    )
    return AuthResponse(
        access_token=result.access_token,
        user=UserResponse.from_entity(result.user),
    )


@router.post(
    "/login",
    response_model=AuthResponse,
    status_code=status.HTTP_200_OK,
    summary="Iniciar sesión con correo y contraseña",
)
async def login(
    payload: LoginRequest,
    auth_service: Annotated[AuthService, Depends(get_auth_service)],
) -> AuthResponse:
    result = await auth_service.login(
        email=payload.email,
        password=payload.password,
    )
    return AuthResponse(
        access_token=result.access_token,
        user=UserResponse.from_entity(result.user),
    )


@router.get(
    "/me",
    response_model=UserResponse,
    status_code=status.HTTP_200_OK,
    summary="Obtener el perfil del usuario autenticado",
)
async def me(
    current_user: Annotated[UserEntity, Depends(get_current_user)],
) -> UserResponse:
    return UserResponse.from_entity(current_user)
