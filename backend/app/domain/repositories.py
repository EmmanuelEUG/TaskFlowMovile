from abc import ABC, abstractmethod

from app.domain.entities import TaskEntity, UserEntity


class UserRepository(ABC):
    """Contrato de persistencia de usuarios (DIP)."""

    @abstractmethod
    async def get_by_email(self, email: str) -> UserEntity | None:
        raise NotImplementedError

    @abstractmethod
    async def get_by_id(self, user_id: str) -> UserEntity | None:
        raise NotImplementedError

    @abstractmethod
    async def create(self, user: UserEntity) -> UserEntity:
        raise NotImplementedError


class TaskRepository(ABC):
    """Contrato de persistencia del recurso principal Task (DIP)."""

    @abstractmethod
    async def list_by_user(self, user_id: str) -> list[TaskEntity]:
        raise NotImplementedError

    @abstractmethod
    async def get_by_id_for_user(self, task_id: str, user_id: str) -> TaskEntity | None:
        raise NotImplementedError

    @abstractmethod
    async def create(self, task: TaskEntity) -> TaskEntity:
        raise NotImplementedError

    @abstractmethod
    async def update(self, task: TaskEntity) -> TaskEntity:
        raise NotImplementedError

    @abstractmethod
    async def delete(self, task_id: str, user_id: str) -> bool:
        raise NotImplementedError

    @abstractmethod
    async def replace_all_for_user(self, user_id: str, tasks: list[TaskEntity]) -> list[TaskEntity]:
        raise NotImplementedError
