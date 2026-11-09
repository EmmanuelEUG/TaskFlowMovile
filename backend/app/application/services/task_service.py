from dataclasses import dataclass
from datetime import datetime, timezone
import uuid

from app.application.services.seed_factory import (
    build_seed_tasks_for_user,
    format_spanish_datetime,
)
from app.domain.entities import Importance, RecurrenceUnit, TaskEntity
from app.domain.exceptions import TaskNotFoundError, ValidationDomainError
from app.domain.repositories import TaskRepository


@dataclass(slots=True)
class TaskUpsertData:
    title: str
    description: str
    area: str
    importance: Importance
    priority_level: int
    due_date_formatted: str | None
    due_date_millis: int | None
    is_completed: bool
    is_recurring: bool
    recurrence_interval: int
    recurrence_unit: RecurrenceUnit
    recurrence_days_of_week: list[str]
    recurrence_days_of_month: list[int]
    created_date_formatted: str | None = None
    completed_at_millis: int | None = None


class TaskService:
    """Casos de uso CRUD de tareas con validación de dominio y pertenencia de usuario."""

    def __init__(self, task_repository: TaskRepository) -> None:
        self._task_repository = task_repository

    async def list_tasks(
        self,
        user_id: str,
        search: str | None = None,
        area: str | None = None,
        importance: Importance | None = None,
    ) -> list[TaskEntity]:
        tasks = await self._task_repository.list_by_user(user_id)
        if search:
            q = search.strip().lower()
            tasks = [t for t in tasks if q in t.title.lower() or q in t.description.lower()]
        if area and area.lower() != "todas":
            tasks = [t for t in tasks if t.area.lower() == area.strip().lower()]
        if importance is not None:
            tasks = [t for t in tasks if t.importance == importance]
        return tasks

    async def get_task(self, user_id: str, task_id: str) -> TaskEntity:
        task = await self._task_repository.get_by_id_for_user(task_id=task_id, user_id=user_id)
        if task is None:
            raise TaskNotFoundError("Tarea no encontrada")
        return task

    async def create_task(self, user_id: str, data: TaskUpsertData) -> TaskEntity:
        self._validate_task_fields(data)
        now_dt = datetime.now(timezone.utc)
        now_ms = int(now_dt.timestamp() * 1000)

        completed_at = (
            data.completed_at_millis if data.completed_at_millis is not None else (now_ms if data.is_completed else None)
        )
        created_fmt = (data.created_date_formatted or "").strip() or format_spanish_datetime(
            now_dt, include_time=False
        )

        entity = TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title=data.title.strip(),
            description=data.description.strip(),
            area=data.area.strip(),
            importance=data.importance,
            priority_level=data.priority_level,
            due_date_formatted=data.due_date_formatted,
            due_date_millis=data.due_date_millis,
            is_completed=data.is_completed,
            is_recurring=data.is_recurring,
            recurrence_interval=max(1, data.recurrence_interval),
            recurrence_unit=data.recurrence_unit,
            recurrence_days_of_week=list(data.recurrence_days_of_week),
            recurrence_days_of_month=sorted(set(data.recurrence_days_of_month)),
            created_date_formatted=created_fmt,
            completed_at_millis=completed_at,
            created_at_millis=now_ms,
            updated_at_millis=now_ms,
        )
        return await self._task_repository.create(entity)

    async def update_task(self, user_id: str, task_id: str, data: TaskUpsertData) -> TaskEntity:
        existing = await self.get_task(user_id=user_id, task_id=task_id)
        self._validate_task_fields(data)
        now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)

        completed_at: int | None
        if data.is_completed:
            completed_at = data.completed_at_millis or existing.completed_at_millis or now_ms
        else:
            completed_at = None

        updated = TaskEntity(
            id=existing.id,
            user_id=existing.user_id,
            title=data.title.strip(),
            description=data.description.strip(),
            area=data.area.strip(),
            importance=data.importance,
            priority_level=data.priority_level,
            due_date_formatted=data.due_date_formatted,
            due_date_millis=data.due_date_millis,
            is_completed=data.is_completed,
            is_recurring=data.is_recurring,
            recurrence_interval=max(1, data.recurrence_interval),
            recurrence_unit=data.recurrence_unit,
            recurrence_days_of_week=list(data.recurrence_days_of_week),
            recurrence_days_of_month=sorted(set(data.recurrence_days_of_month)),
            created_date_formatted=existing.created_date_formatted,
            completed_at_millis=completed_at,
            created_at_millis=existing.created_at_millis,
            updated_at_millis=now_ms,
        )
        return await self._task_repository.update(updated)

    async def patch_task(
        self,
        user_id: str,
        task_id: str,
        *,
        is_completed: bool | None = None,
        title: str | None = None,
        priority_level: int | None = None,
    ) -> TaskEntity:
        existing = await self.get_task(user_id=user_id, task_id=task_id)
        now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)

        new_title = title.strip() if title is not None else existing.title
        new_priority = priority_level if priority_level is not None else existing.priority_level
        new_completed = is_completed if is_completed is not None else existing.is_completed

        if len(new_title) < 2:
            raise ValidationDomainError("El título debe tener al menos 2 caracteres")
        if not 1 <= new_priority <= 5:
            raise ValidationDomainError("La prioridad debe estar entre 1 y 5")

        completed_at = existing.completed_at_millis
        if is_completed is True:
            completed_at = existing.completed_at_millis or now_ms
        elif is_completed is False:
            completed_at = None

        updated = TaskEntity(
            id=existing.id,
            user_id=existing.user_id,
            title=new_title,
            description=existing.description,
            area=existing.area,
            importance=existing.importance,
            priority_level=new_priority,
            due_date_formatted=existing.due_date_formatted,
            due_date_millis=existing.due_date_millis,
            is_completed=new_completed,
            is_recurring=existing.is_recurring,
            recurrence_interval=existing.recurrence_interval,
            recurrence_unit=existing.recurrence_unit,
            recurrence_days_of_week=existing.recurrence_days_of_week,
            recurrence_days_of_month=existing.recurrence_days_of_month,
            created_date_formatted=existing.created_date_formatted,
            completed_at_millis=completed_at,
            created_at_millis=existing.created_at_millis,
            updated_at_millis=now_ms,
        )
        return await self._task_repository.update(updated)

    async def delete_task(self, user_id: str, task_id: str) -> None:
        deleted = await self._task_repository.delete(task_id=task_id, user_id=user_id)
        if not deleted:
            raise TaskNotFoundError("Tarea no encontrada")

    async def restore_seed_tasks(self, user_id: str) -> list[TaskEntity]:
        now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)
        seed_tasks = build_seed_tasks_for_user(user_id=user_id, now_ms=now_ms)
        return await self._task_repository.replace_all_for_user(user_id=user_id, tasks=seed_tasks)

    @staticmethod
    def _validate_task_fields(data: TaskUpsertData) -> None:
        if len(data.title.strip()) < 2:
            raise ValidationDomainError("El título es obligatorio y debe tener al menos 2 caracteres")
        if len(data.area.strip()) < 1:
            raise ValidationDomainError("El área es obligatoria")
        if not 1 <= data.priority_level <= 5:
            raise ValidationDomainError("La prioridad debe estar entre 1 y 5")
        if data.is_recurring and data.recurrence_interval < 1:
            raise ValidationDomainError("El intervalo de recurrencia debe ser mayor o igual a 1")
        for day in data.recurrence_days_of_month:
            if not 1 <= day <= 31:
                raise ValidationDomainError("Los días del mes deben estar entre 1 y 31")
