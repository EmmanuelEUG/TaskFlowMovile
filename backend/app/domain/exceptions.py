class DomainException(Exception):
    """Excepción base de la capa de dominio."""

    def __init__(self, message: str) -> None:
        self.message = message
        super().__init__(message)


class EmailAlreadyRegisteredError(DomainException):
    pass


class InvalidCredentialsError(DomainException):
    pass


class UnauthorizedError(DomainException):
    pass


class TaskNotFoundError(DomainException):
    pass


class ValidationDomainError(DomainException):
    pass
