import pytest


async def _register_and_headers(client, email: str = "user1@example.com") -> dict[str, str]:
    res = await client.post(
        "/register",
        json={"name": "Usuario Prueba", "email": email, "password": "Password123"},
    )
    token = res.json()["access_token"]
    return {"Authorization": f"Bearer {token}"}


@pytest.mark.asyncio
async def test_full_task_crud_and_user_isolation(client):
    headers_user1 = await _register_and_headers(client, "user1@example.com")
    headers_user2 = await _register_and_headers(client, "user2@example.com")

    empty_list = await client.get("/tasks", headers=headers_user1)
    assert empty_list.status_code == 200
    assert empty_list.json() == []

    create_payload = {
        "title": "Entregar proyecto Unidad 3",
        "description": "Conectar Retrofit 3 y FastAPI en OCI",
        "area": "Escuela",
        "importance": "HIGH",
        "priority_level": 5,
        "due_date_formatted": "15 de noviembre, 18:00",
        "due_date_millis": 1893456000000,
        "is_completed": False,
        "is_recurring": True,
        "recurrence_interval": 2,
        "recurrence_unit": "WEEKS",
        "recurrence_days_of_week": ["MONDAY", "THURSDAY"],
        "recurrence_days_of_month": [],
    }
    created = await client.post("/tasks", json=create_payload, headers=headers_user1)
    assert created.status_code == 201
    task = created.json()
    task_id = task["id"]
    assert task["title"] == "Entregar proyecto Unidad 3"
    assert task["priority_level"] == 5
    assert task["recurrence_days_of_week"] == ["MONDAY", "THURSDAY"]

    detail = await client.get(f"/tasks/{task_id}", headers=headers_user1)
    assert detail.status_code == 200
    assert detail.json()["id"] == task_id

    # Usuario 2 no debe poder ver la tarea del Usuario 1
    forbidden_detail = await client.get(f"/tasks/{task_id}", headers=headers_user2)
    assert forbidden_detail.status_code == 404

    patched = await client.patch(
        f"/tasks/{task_id}",
        json={"is_completed": True},
        headers=headers_user1,
    )
    assert patched.status_code == 200
    assert patched.json()["is_completed"] is True
    assert patched.json()["completed_at_millis"] is not None

    update_payload = {**create_payload, "title": "Proyecto actualizado", "priority_level": 4, "is_completed": False}
    updated = await client.put(f"/tasks/{task_id}", json=update_payload, headers=headers_user1)
    assert updated.status_code == 200
    assert updated.json()["title"] == "Proyecto actualizado"
    assert updated.json()["priority_level"] == 4
    assert updated.json()["completed_at_millis"] is None

    deleted = await client.delete(f"/tasks/{task_id}", headers=headers_user1)
    assert deleted.status_code == 200
    assert deleted.json()["deleted"] is True

    after_delete = await client.get("/tasks", headers=headers_user1)
    assert after_delete.json() == []


@pytest.mark.asyncio
async def test_restore_seed_tasks(client):
    headers = await _register_and_headers(client, "seed@example.com")
    res = await client.post("/tasks/seed", headers=headers)
    assert res.status_code == 200
    tasks = res.json()
    assert len(tasks) == 9
