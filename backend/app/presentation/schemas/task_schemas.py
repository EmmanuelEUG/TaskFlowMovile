from datetime import datetime, timezone
from pydantic import BaseModel, Field

from app.application.services.task_service import TaskUpsertData
from app.domain.entities import Importance, RecurrenceUnit, TaskEntity


class TaskCreateRequest(BaseModel):
    title: str = Field(min_length=2, max_length=200)
    description: str = Field(default="", max_length=2000)
    area: str = Field(default="General", min_length=1, max_length=80)
    importance: Importance = Importance.MEDIUM
    priority_level: int = Field(default=3, ge=1, le=5)
    due_date_formatted: str | None = Field(default=None, max_length=120)
    due_date_millis: int | None = None
    is_completed: bool = False
    is_recurring: bool = False
    recurrence_interval: int = Field(default=1, ge=1, le=3650)
    recurrence_unit: RecurrenceUnit = RecurrenceUnit.DAYS
    recurrence_days_of_week: list[str] = Field(default_factory=list)
    recurrence_days_of_month: list[int] = Field(default_factory=list)
    created_date_formatted: str | None = Field(default=None, max_length=80)
    completed_at_millis: int | None = None

    def to_upsert_data(self) -> TaskUpsertData:
        return TaskUpsertData(
            title=self.title,
            description=self.description,
            area=self.area,
            importance=self.importance,
            priority_level=self.priority_level,
            due_date_formatted=self.due_date_formatted,
            due_date_millis=self.due_date_millis,
            is_completed=self.is_completed,
            is_recurring=self.is_recurring,
            recurrence_interval=self.recurrence_interval,
            recurrence_unit=self.recurrence_unit,
            recurrence_days_of_week=self.recurrence_days_of_week,
            recurrence_days_of_month=self.recurrence_days_of_month,
            created_date_formatted=self.created_date_formatted,
            completed_at_millis=self.completed_at_millis,
        )


class TaskUpdateRequest(TaskCreateRequest):
    """Petición completa de reemplazo para PUT /tasks/{id}."""


class TaskPatchRequest(BaseModel):
    """Petición parcial para PATCH /tasks/{id}."""

    is_completed: bool | None = None
    title: str | None = Field(default=None, min_length=2, max_length=200)
    priority_level: int | None = Field(default=None, ge=1, le=5)


class TaskResponse(BaseModel):
    id: str
    title: str
    description: str
    area: str
    importance: Importance
    priority_level: int
    due_date_formatted: str | None = None
    due_date_millis: int | None = None
    is_overdue: bool
    is_completed: bool
    is_recurring: bool
    recurrence_interval: int
    recurrence_unit: RecurrenceUnit
    created_date_formatted: str
    completed_at_millis: int | None = None
    recurrence_days_of_week: list[str] = Field(default_factory=list)
    recurrence_days_of_month: list[int] = Field(default_factory=list)

    @classmethod
    def from_entity(cls, task: TaskEntity) -> "TaskResponse":
        now_ms = int(datetime.now(timezone.utc).timestamp() * 1000)
        return cls(
            id=task.id,
            title=task.title,
            description=task.description,
            area=task.area,
            importance=task.importance,
            priority_level=task.priority_level,
            due_date_formatted=task.due_date_formatted,
            due_date_millis=task.due_date_millis,
            is_overdue=task.is_overdue_at(now_ms),
            is_completed=task.is_completed,
            is_recurring=task.is_recurring,
            recurrence_interval=task.recurrence_interval,
            recurrence_unit=task.recurrence_unit,
            created_date_formatted=task.created_date_formatted,
            completed_at_millis=task.completed_at_millis,
            recurrence_days_of_week=task.recurrence_days_of_week,
            recurrence_days_of_month=task.recurrence_days_of_month,
        )


class DeleteTaskResponse(BaseModel):
    deleted: bool = True
    id: str
