import pytest


@pytest.mark.asyncio
async def test_health_endpoint(client):
    response = await client.get("/health")
    assert response.status_code == 200
    body = response.json()
    assert body["status"] == "ok"


@pytest.mark.asyncio
async def test_register_login_and_me_flow(client):
    register_payload = {
        "name": "Emmanuel Urbina",
        "email": "Emmanuel@Upchiapas.edu.mx",
        "password": "Password123",
    }
    res = await client.post("/register", json=register_payload)
    assert res.status_code == 201
    data = res.json()
    assert data["token_type"] == "bearer"
    assert data["user"]["name"] == "Emmanuel Urbina"
    assert data["user"]["email"] == "emmanuel@upchiapas.edu.mx"
    token = data["access_token"]
    assert token

    me_res = await client.get("/me", headers={"Authorization": f"Bearer {token}"})
    assert me_res.status_code == 200
    assert me_res.json()["email"] == "emmanuel@upchiapas.edu.mx"

    login_res = await client.post(
        "/login",
        json={"email": "emmanuel@upchiapas.edu.mx", "password": "Password123"},
    )
    assert login_res.status_code == 200
    assert login_res.json()["access_token"]


@pytest.mark.asyncio
async def test_duplicate_email_and_wrong_password(client):
    payload = {
        "name": "Ana",
        "email": "ana@example.com",
        "password": "Password123",
    }
    first = await client.post("/register", json=payload)
    assert first.status_code == 201

    dup = await client.post("/register", json=payload)
    assert dup.status_code == 409

    bad_login = await client.post(
        "/login",
        json={"email": "ana@example.com", "password": "WrongPassword"},
    )
    assert bad_login.status_code == 401


@pytest.mark.asyncio
async def test_unauthenticated_access_returns_401(client):
    res = await client.get("/tasks")
    assert res.status_code == 401
