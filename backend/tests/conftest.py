import pytest_asyncio
from httpx import ASGITransport, AsyncClient

from app.core.config import Settings
from app.infrastructure.db.database import Base
from app.main import create_app


@pytest_asyncio.fixture
async def client(tmp_path):
    db_file = tmp_path / "test_taskflow.db"
    settings = Settings(
        database_url=f"sqlite+aiosqlite:///{db_file}",
        secret_key="test-secret-key-for-taskflow-unit-tests-only-1234",
        seed_on_register=False,
    )
    app = create_app(settings_override=settings)
    async with app.state.engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)

    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://testserver") as async_client:
        yield async_client

    await app.state.engine.dispose()
