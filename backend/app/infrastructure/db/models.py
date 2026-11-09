from sqlalchemy import BigInteger, Boolean, ForeignKey, Integer, String, Text
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.infrastructure.db.database import Base


class UserModel(Base):
    __tablename__ = "users"

    id: Mapped[str] = mapped_column(String(36), primary_key=True)
    name: Mapped[str] = mapped_column(String(120), nullable=False)
    email: Mapped[str] = mapped_column(String(255), nullable=False, unique=True, index=True)
    password_hash: Mapped[str] = mapped_column(String(255), nullable=False)
    created_at_millis: Mapped[int] = mapped_column(BigInteger, nullable=False)

    tasks: Mapped[list["TaskModel"]] = relationship(
        back_populates="owner",
        cascade="all, delete-orphan",
    )


class TaskModel(Base):
    __tablename__ = "tasks"

    id: Mapped[str] = mapped_column(String(36), primary_key=True)
    user_id: Mapped[str] = mapped_column(
        String(36),
        ForeignKey("users.id", ondelete="CASCADE"),
        nullable=False,
        index=True,
    )
    title: Mapped[str] = mapped_column(String(200), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=False, default="")
    area: Mapped[str] = mapped_column(String(80), nullable=False, default="General")
    importance: Mapped[str] = mapped_column(String(16), nullable=False, default="MEDIUM")
    priority_level: Mapped[int] = mapped_column(Integer, nullable=False, default=3)
    due_date_formatted: Mapped[str | None] = mapped_column(String(120), nullable=True)
    due_date_millis: Mapped[int | None] = mapped_column(BigInteger, nullable=True)
    is_completed: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    is_recurring: Mapped[bool] = mapped_column(Boolean, nullable=False, default=False)
    recurrence_interval: Mapped[int] = mapped_column(Integer, nullable=False, default=1)
    recurrence_unit: Mapped[str] = mapped_column(String(16), nullable=False, default="DAYS")
    recurrence_days_of_week_json: Mapped[str] = mapped_column(Text, nullable=False, default="[]")
    recurrence_days_of_month_json: Mapped[str] = mapped_column(Text, nullable=False, default="[]")
    created_date_formatted: Mapped[str] = mapped_column(String(80), nullable=False, default="")
    completed_at_millis: Mapped[int | None] = mapped_column(BigInteger, nullable=True)
    created_at_millis: Mapped[int] = mapped_column(BigInteger, nullable=False)
    updated_at_millis: Mapped[int] = mapped_column(BigInteger, nullable=False)

    owner: Mapped[UserModel] = relationship(back_populates="tasks")
