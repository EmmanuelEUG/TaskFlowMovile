from fastapi import FastAPI, Request, status
from fastapi.responses import JSONResponse

from app.domain.exceptions import (
    EmailAlreadyRegisteredError,
    InvalidCredentialsError,
    TaskNotFoundError,
    UnauthorizedError,
    ValidationDomainError,
)


def register_exception_handlers(app: FastAPI) -> None:
    @app.exception_handler(EmailAlreadyRegisteredError)
    async def _email_exists(_req: Request, exc: EmailAlreadyRegisteredError) -> JSONResponse:
        return JSONResponse(status_code=status.HTTP_409_CONFLICT, content={"detail": exc.message})

    @app.exception_handler(InvalidCredentialsError)
    async def _invalid_creds(_req: Request, exc: InvalidCredentialsError) -> JSONResponse:
        return JSONResponse(status_code=status.HTTP_401_UNAUTHORIZED, content={"detail": exc.message})

    @app.exception_handler(UnauthorizedError)
    async def _unauthorized(_req: Request, exc: UnauthorizedError) -> JSONResponse:
        return JSONResponse(status_code=status.HTTP_401_UNAUTHORIZED, content={"detail": exc.message})

    @app.exception_handler(TaskNotFoundError)
    async def _not_found(_req: Request, exc: TaskNotFoundError) -> JSONResponse:
        return JSONResponse(status_code=status.HTTP_404_NOT_FOUND, content={"detail": exc.message})

    @app.exception_handler(ValidationDomainError)
    async def _validation_err(_req: Request, exc: ValidationDomainError) -> JSONResponse:
        return JSONResponse(status_code=status.HTTP_400_BAD_REQUEST, content={"detail": exc.message})
