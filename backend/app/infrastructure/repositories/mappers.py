import json

from app.domain.entities import Importance, RecurrenceUnit, TaskEntity, UserEntity
from app.infrastructure.db.models import TaskModel, UserModel


class UserMapper:
    """Conversor dedicado entre UserModel (ORM) y UserEntity (Dominio)."""

    @staticmethod
    def to_domain(model: UserModel) -> UserEntity:
        return UserEntity(
            id=model.id,
            name=model.name,
            email=model.email,
            password_hash=model.password_hash,
            created_at_millis=model.created_at_millis,
        )

    @staticmethod
    def to_model(entity: UserEntity) -> UserModel:
        return UserModel(
            id=entity.id,
            name=entity.name,
            email=entity.email,
            password_hash=entity.password_hash,
            created_at_millis=entity.created_at_millis,
        )


class TaskMapper:
    """Conversor dedicado entre TaskModel (ORM) y TaskEntity (Dominio)."""

    @staticmethod
    def to_domain(model: TaskModel) -> TaskEntity:
        days_of_week = json.loads(model.recurrence_days_of_week_json or "[]")
        days_of_month = json.loads(model.recurrence_days_of_month_json or "[]")
        return TaskEntity(
            id=model.id,
            user_id=model.user_id,
            title=model.title,
            description=model.description,
            area=model.area,
            importance=Importance(model.importance),
            priority_level=model.priority_level,
            due_date_formatted=model.due_date_formatted,
            due_date_millis=model.due_date_millis,
            is_completed=model.is_completed,
            is_recurring=model.is_recurring,
            recurrence_interval=model.recurrence_interval,
            recurrence_unit=RecurrenceUnit(model.recurrence_unit),
            recurrence_days_of_week=[str(d) for d in days_of_week],
            recurrence_days_of_month=[int(d) for d in days_of_month],
            created_date_formatted=model.created_date_formatted,
            completed_at_millis=model.completed_at_millis,
            created_at_millis=model.created_at_millis,
            updated_at_millis=model.updated_at_millis,
        )

    @staticmethod
    def to_model(entity: TaskEntity) -> TaskModel:
        return TaskModel(
            id=entity.id,
            user_id=entity.user_id,
            title=entity.title,
            description=entity.description,
            area=entity.area,
            importance=entity.importance.value,
            priority_level=entity.priority_level,
            due_date_formatted=entity.due_date_formatted,
            due_date_millis=entity.due_date_millis,
            is_completed=entity.is_completed,
            is_recurring=entity.is_recurring,
            recurrence_interval=entity.recurrence_interval,
            recurrence_unit=entity.recurrence_unit.value,
            recurrence_days_of_week_json=json.dumps(entity.recurrence_days_of_week),
            recurrence_days_of_month_json=json.dumps(entity.recurrence_days_of_month),
            created_date_formatted=entity.created_date_formatted,
            completed_at_millis=entity.completed_at_millis,
            created_at_millis=entity.created_at_millis,
            updated_at_millis=entity.updated_at_millis,
        )

    @staticmethod
    def apply_to_model(model: TaskModel, entity: TaskEntity) -> TaskModel:
        model.title = entity.title
        model.description = entity.description
        model.area = entity.area
        model.importance = entity.importance.value
        model.priority_level = entity.priority_level
        model.due_date_formatted = entity.due_date_formatted
        model.due_date_millis = entity.due_date_millis
        model.is_completed = entity.is_completed
        model.is_recurring = entity.is_recurring
        model.recurrence_interval = entity.recurrence_interval
        model.recurrence_unit = entity.recurrence_unit.value
        model.recurrence_days_of_week_json = json.dumps(entity.recurrence_days_of_week)
        model.recurrence_days_of_month_json = json.dumps(entity.recurrence_days_of_month)
        model.created_date_formatted = entity.created_date_formatted
        model.completed_at_millis = entity.completed_at_millis
        model.updated_at_millis = entity.updated_at_millis
        return model
