from typing import Annotated

from fastapi import APIRouter, Depends, Query, status

from app.application.services.task_service import TaskService
from app.domain.entities import Importance, UserEntity
from app.presentation.dependencies import get_current_user, get_task_service
from app.presentation.schemas.task_schemas import (
    DeleteTaskResponse,
    TaskCreateRequest,
    TaskPatchRequest,
    TaskResponse,
    TaskUpdateRequest,
)

router = APIRouter(prefix="/tasks", tags=["Tareas"])


@router.get(
    "",
    response_model=list[TaskResponse],
    status_code=status.HTTP_200_OK,
    summary="Listar tareas del usuario autenticado",
)
async def list_tasks(
    current_user: Annotated[UserEntity, Depends(get_current_user)],
    task_service: Annotated[TaskService, Depends(get_task_service)],
    search: str | None = Query(default=None),
    area: str | None = Query(default=None),
    importance: Importance | None = Query(default=None),
) -> list[TaskResponse]:
    tasks = await task_service.list_tasks(
        user_id=current_user.id,
        search=search,
        area=area,
        importance=importance,
    )
    return [TaskResponse.from_entity(task) for task in tasks]


@router.post(
    "/seed",
    response_model=list[TaskResponse],
    status_code=status.HTTP_200_OK,
    summary="Restaurar las 9 tareas de ejemplo para el usuario autenticado",
)
async def restore_seed_tasks(
    current_user: Annotated[UserEntity, Depends(get_current_user)],
    task_service: Annotated[TaskService, Depends(get_task_service)],
) -> list[TaskResponse]:
    tasks = await task_service.restore_seed_tasks(user_id=current_user.id)
    return [TaskResponse.from_entity(task) for task in tasks]


@router.get(
    "/{task_id}",
    response_model=TaskResponse,
    status_code=status.HTTP_200_OK,
    summary="Obtener detalle de una tarea por ID",
)
async def get_task(
    task_id: str,
    current_user: Annotated[UserEntity, Depends(get_current_user)],
    task_service: Annotated[TaskService, Depends(get_task_service)],
) -> TaskResponse:
    task = await task_service.get_task(user_id=current_user.id, task_id=task_id)
    return TaskResponse.from_entity(task)


@router.post(
    "",
    response_model=TaskResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Crear una nueva tarea",
)
async def create_task(
    payload: TaskCreateRequest,
    current_user: Annotated[UserEntity, Depends(get_current_user)],
    task_service: Annotated[TaskService, Depends(get_task_service)],
) -> TaskResponse:
    created = await task_service.create_task(
        user_id=current_user.id,
        data=payload.to_upsert_data(),
    )
    return TaskResponse.from_entity(created)


@router.put(
    "/{task_id}",
    response_model=TaskResponse,
    status_code=status.HTTP_200_OK,
    summary="Actualizar completamente una tarea existente",
)
async def update_task(
    task_id: str,
    payload: TaskUpdateRequest,
    current_user: Annotated[UserEntity, Depends(get_current_user)],
    task_service: Annotated[TaskService, Depends(get_task_service)],
) -> TaskResponse:
    updated = await task_service.update_task(
        user_id=current_user.id,
        task_id=task_id,
        data=payload.to_upsert_data(),
    )
    return TaskResponse.from_entity(updated)


@router.patch(
    "/{task_id}",
    response_model=TaskResponse,
    status_code=status.HTTP_200_OK,
    summary="Actualizar parcialmente una tarea (ej. completar/desmarcar)",
)
async def patch_task(
    task_id: str,
    payload: TaskPatchRequest,
    current_user: Annotated[UserEntity, Depends(get_current_user)],
    task_service: Annotated[TaskService, Depends(get_task_service)],
) -> TaskResponse:
    updated = await task_service.patch_task(
        user_id=current_user.id,
        task_id=task_id,
        is_completed=payload.is_completed,
        title=payload.title,
        priority_level=payload.priority_level,
    )
    return TaskResponse.from_entity(updated)


@router.delete(
    "/{task_id}",
    response_model=DeleteTaskResponse,
    status_code=status.HTTP_200_OK,
    summary="Eliminar una tarea por ID",
)
async def delete_task(
    task_id: str,
    current_user: Annotated[UserEntity, Depends(get_current_user)],
    task_service: Annotated[TaskService, Depends(get_task_service)],
) -> DeleteTaskResponse:
    await task_service.delete_task(user_id=current_user.id, task_id=task_id)
    return DeleteTaskResponse(deleted=True, id=task_id)
