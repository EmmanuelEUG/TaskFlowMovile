from dataclasses import dataclass, field
from enum import StrEnum


class Importance(StrEnum):
    HIGH = "HIGH"
    MEDIUM = "MEDIUM"
    LOW = "LOW"


class RecurrenceUnit(StrEnum):
    HOURS = "HOURS"
    DAYS = "DAYS"
    WEEKS = "WEEKS"
    MONTHS = "MONTHS"
    YEARS = "YEARS"


@dataclass(slots=True)
class UserEntity:
    """Entidad de dominio pura para usuario autenticado."""

    id: str
    name: str
    email: str
    password_hash: str
    created_at_millis: int


@dataclass(slots=True)
class TaskEntity:
    """Entidad de dominio pura del recurso principal Task."""

    id: str
    user_id: str
    title: str
    description: str
    area: str
    importance: Importance
    priority_level: int
    due_date_formatted: str | None = None
    due_date_millis: int | None = None
    is_completed: bool = False
    is_recurring: bool = False
    recurrence_interval: int = 1
    recurrence_unit: RecurrenceUnit = RecurrenceUnit.DAYS
    recurrence_days_of_week: list[str] = field(default_factory=list)
    recurrence_days_of_month: list[int] = field(default_factory=list)
    created_date_formatted: str = ""
    completed_at_millis: int | None = None
    created_at_millis: int = 0
    updated_at_millis: int = 0

    def is_overdue_at(self, now_millis: int) -> bool:
        return (
            not self.is_completed
            and self.due_date_millis is not None
            and self.due_date_millis < now_millis
        )
