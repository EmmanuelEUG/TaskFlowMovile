from sqlalchemy import delete, select
from sqlalchemy.ext.asyncio import AsyncSession

from app.domain.entities import TaskEntity
from app.domain.repositories import TaskRepository
from app.infrastructure.db.models import TaskModel
from app.infrastructure.repositories.mappers import TaskMapper


class SqlAlchemyTaskRepository(TaskRepository):
    """Implementación SQLAlchemy de TaskRepository."""

    def __init__(self, session: AsyncSession) -> None:
        self._session = session

    async def list_by_user(self, user_id: str) -> list[TaskEntity]:
        stmt = (
            select(TaskModel)
            .where(TaskModel.user_id == user_id)
            .order_by(TaskModel.created_at_millis.desc())
        )
        result = await self._session.execute(stmt)
        models = result.scalars().all()
        return [TaskMapper.to_domain(model) for model in models]

    async def get_by_id_for_user(self, task_id: str, user_id: str) -> TaskEntity | None:
        stmt = select(TaskModel).where(
            TaskModel.id == task_id,
            TaskModel.user_id == user_id,
        )
        result = await self._session.execute(stmt)
        model = result.scalar_one_or_none()
        return TaskMapper.to_domain(model) if model is not None else None

    async def create(self, task: TaskEntity) -> TaskEntity:
        model = TaskMapper.to_model(task)
        self._session.add(model)
        await self._session.commit()
        await self._session.refresh(model)
        return TaskMapper.to_domain(model)

    async def update(self, task: TaskEntity) -> TaskEntity:
        stmt = select(TaskModel).where(
            TaskModel.id == task.id,
            TaskModel.user_id == task.user_id,
        )
        result = await self._session.execute(stmt)
        model = result.scalar_one()
        TaskMapper.apply_to_model(model, task)
        await self._session.commit()
        await self._session.refresh(model)
        return TaskMapper.to_domain(model)

    async def delete(self, task_id: str, user_id: str) -> bool:
        stmt = delete(TaskModel).where(
            TaskModel.id == task_id,
            TaskModel.user_id == user_id,
        )
        result = await self._session.execute(stmt)
        await self._session.commit()
        return (result.rowcount or 0) > 0

    async def replace_all_for_user(
        self,
        user_id: str,
        tasks: list[TaskEntity],
    ) -> list[TaskEntity]:
        await self._session.execute(delete(TaskModel).where(TaskModel.user_id == user_id))
        models = [TaskMapper.to_model(task) for task in tasks]
        self._session.add_all(models)
        await self._session.commit()
        return await self.list_by_user(user_id)
