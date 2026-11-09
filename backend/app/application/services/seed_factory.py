from datetime import datetime, timedelta, timezone
import uuid

from app.domain.entities import Importance, RecurrenceUnit, TaskEntity

_MONTHS_ES = {
    1: "enero",
    2: "febrero",
    3: "marzo",
    4: "abril",
    5: "mayo",
    6: "junio",
    7: "julio",
    8: "agosto",
    9: "septiembre",
    10: "octubre",
    11: "noviembre",
    12: "diciembre",
}


def format_spanish_datetime(dt: datetime, include_time: bool = True) -> str:
    month_name = _MONTHS_ES[dt.month]
    if include_time:
        return f"{dt.day} de {month_name}, {dt.hour:02d}:{dt.minute:02d}"
    return f"{dt.day} de {month_name}"


def build_seed_tasks_for_user(user_id: str, now_ms: int | None = None) -> list[TaskEntity]:
    now_dt = datetime.now(timezone.utc) if now_ms is None else datetime.fromtimestamp(now_ms / 1000, timezone.utc)
    now_epoch_ms = int(now_dt.timestamp() * 1000)

    def offset_ms(days: int, hour: int, minute: int) -> tuple[int, str]:
        target = (now_dt + timedelta(days=days)).replace(hour=hour, minute=minute, second=0, microsecond=0)
        return int(target.timestamp() * 1000), format_spanish_datetime(target, include_time=True)

    t1_ms, t1_fmt = offset_ms(-1, 18, 0)
    t2_ms, t2_fmt = offset_ms(0, 20, 0)
    t3_ms, t3_fmt = offset_ms(1, 20, 0)
    t4_ms, t4_fmt = offset_ms(7, 17, 0)
    t5_ms, t5_fmt = offset_ms(16, 10, 0)
    t7_ms, t7_fmt = offset_ms(-2, 14, 0)
    t8_ms, t8_fmt = offset_ms(-3, 21, 0)
    t9_ms, t9_fmt = offset_ms(-1, 22, 0)

    return [
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Pagar el internet",
            description="Pagar antes del corte para evitar la suspensión del servicio.",
            area="Casa",
            importance=Importance.HIGH,
            priority_level=4,
            due_date_formatted=t1_fmt,
            due_date_millis=t1_ms,
            is_completed=False,
            is_recurring=False,
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=5), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Hacer ejercicio",
            description="30 minutos de cardio en casa.",
            area="Salud",
            importance=Importance.MEDIUM,
            priority_level=3,
            due_date_formatted=t2_fmt,
            due_date_millis=t2_ms,
            is_completed=False,
            is_recurring=True,
            recurrence_interval=1,
            recurrence_unit=RecurrenceUnit.DAYS,
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=4), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Repasar Probabilidad y Estadística",
            description="Repasar distribuciones y ejercicios para el examen.",
            area="Escuela",
            importance=Importance.MEDIUM,
            priority_level=3,
            due_date_formatted=t3_fmt,
            due_date_millis=t3_ms,
            is_completed=False,
            is_recurring=False,
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=3), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Comprar mandado",
            description="Frutas, verduras, leche y avena para la semana.",
            area="Casa",
            importance=Importance.LOW,
            priority_level=2,
            due_date_formatted=t4_fmt,
            due_date_millis=t4_ms,
            is_completed=False,
            is_recurring=False,
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=2), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Entregar avance del proyecto",
            description="Mostrar arquitectura MVVM, StateFlow y pantallas en Compose.",
            area="Escuela",
            importance=Importance.HIGH,
            priority_level=5,
            due_date_formatted=t5_fmt,
            due_date_millis=t5_ms,
            is_completed=False,
            is_recurring=False,
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=1), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Revisar Classroom",
            description="Verificar anuncios y rúbrica de evaluación de la unidad.",
            area="Escuela",
            importance=Importance.LOW,
            priority_level=2,
            due_date_formatted=None,
            due_date_millis=None,
            is_completed=False,
            is_recurring=False,
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=1), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Crear repositorio en GitHub",
            description="Inicializar repositorio con commits incrementales.",
            area="Proyecto",
            importance=Importance.HIGH,
            priority_level=4,
            due_date_formatted=t7_fmt,
            due_date_millis=t7_ms,
            is_completed=True,
            completed_at_millis=int((now_dt - timedelta(days=2)).timestamp() * 1000),
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=6), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Respaldar archivos",
            description="Subir copia de seguridad de los proyectos del cuatrimestre.",
            area="Casa",
            importance=Importance.MEDIUM,
            priority_level=3,
            due_date_formatted=t8_fmt,
            due_date_millis=t8_ms,
            is_completed=True,
            completed_at_millis=int((now_dt - timedelta(days=1)).timestamp() * 1000),
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=7), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
        TaskEntity(
            id=str(uuid.uuid4()),
            user_id=user_id,
            title="Preparar mockups en Figma",
            description="Diseñar las vistas de TaskFlow con tokens de color.",
            area="Proyecto",
            importance=Importance.HIGH,
            priority_level=5,
            due_date_formatted=t9_fmt,
            due_date_millis=t9_ms,
            is_completed=True,
            completed_at_millis=now_epoch_ms,
            created_date_formatted=format_spanish_datetime(now_dt - timedelta(days=5), include_time=False),
            created_at_millis=now_epoch_ms,
            updated_at_millis=now_epoch_ms,
        ),
    ]
