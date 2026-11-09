from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.core.config import Settings, get_settings
from app.infrastructure.db.database import Base, create_engine_and_sessionmaker
from app.presentation.exception_handlers import register_exception_handlers
from app.presentation.routers.auth_router import router as auth_router
from app.presentation.routers.health_router import router as health_router
from app.presentation.routers.task_router import router as task_router


def create_app(settings_override: Settings | None = None) -> FastAPI:
    settings = settings_override or get_settings()
    engine, session_factory = create_engine_and_sessionmaker(settings.database_url)

    @asynccontextmanager
    async def lifespan(application: FastAPI):
        async with engine.begin() as conn:
            await conn.run_sync(Base.metadata.create_all)
        yield
        await engine.dispose()

    application = FastAPI(
        title=settings.app_name,
        version=settings.app_version,
        description="API REST para TaskFlow construida con FastAPI y principios SOLID.",
        lifespan=lifespan,
    )
    application.state.engine = engine
    application.state.session_factory = session_factory

    application.add_middleware(
        CORSMiddleware,
        allow_origins=settings.cors_origins_list,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    register_exception_handlers(application)
    application.include_router(health_router)
    application.include_router(auth_router)
    application.include_router(task_router)
    return application


app = create_app()
