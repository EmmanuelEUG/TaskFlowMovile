# TaskFlow Backend (FastAPI + SOLID + SQLite Async)

API REST para TaskFlow construida siguiendo principios SOLID y Clean Architecture.

## Estructura del proyecto

- `app/core/`: Configuración (`pydantic-settings`) y seguridad (`PasswordHasher`, `TokenService` con `bcrypt` + `PyJWT`).
- `app/domain/`: Entidades puras (`UserEntity`, `TaskEntity`), excepciones de dominio e interfaces abstractas de repositorio (`UserRepository`, `TaskRepository`).
- `app/application/services/`: Casos de uso (`AuthService`, `TaskService`) inyectados por constructor contra interfaces de dominio.
- `app/infrastructure/`: Base de datos SQLite async con SQLAlchemy 2.0, modelos ORM, mappers dedicados e implementaciones concretas de repositorio.
- `app/presentation/`: Esquemas Pydantic, inyección de dependencias FastAPI, manejadores globales de excepciones y routers REST.

## Endpoints principales

| Método | Ruta | Uso |
| :--- | :--- | :--- |
| `GET` | `/health` | Estado del servicio |
| `POST` | `/register` | Registro de cuenta (`name`, `email`, `password`) |
| `POST` | `/login` | Inicio de sesión (`email`, `password`) |
| `GET` | `/me` | Datos del usuario autenticado (requiere Bearer token) |
| `GET` | `/tasks` | Listar tareas del usuario autenticado |
| `GET` | `/tasks/{id}` | Detalle de una tarea |
| `POST` | `/tasks` | Crear tarea |
| `PUT` | `/tasks/{id}` | Actualizar tarea completa |
| `PATCH` | `/tasks/{id}` | Actualización parcial (ej. `is_completed`) |
| `DELETE` | `/tasks/{id}` | Eliminar tarea |
| `POST` | `/tasks/seed` | Cargar las 9 tareas de ejemplo iniciales |

## Ejecución local rápida

```bash
cd backend
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
cp .env.example .env
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Con el emulador Android apunta a `http://10.0.2.2:8000/`. Documentación interactiva en `http://127.0.0.1:8000/docs`.

## Pruebas automatizadas

```bash
.venv/bin/pytest -v
```

## Despliegue en Oracle Cloud (OCI)

Consulta la guía paso a paso en [ORACLE_CLOUD_DEPLOY.md](ORACLE_CLOUD_DEPLOY.md).
