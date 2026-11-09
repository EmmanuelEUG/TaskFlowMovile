# Guía paso a paso: Subir TaskFlow API a Oracle Cloud (OCI)

Si ya sabes usar **AWS**, Oracle Cloud Infrastructure (OCI) es muy parecido, solo cambian los nombres de algunos servicios y hay **un detalle importante en el firewall de Ubuntu** que siempre confunde la primera vez.

## Equivalencias rápidas AWS → Oracle Cloud

| Concepto en AWS | Equivalente en Oracle Cloud (OCI) |
| :--- | :--- |
| EC2 Instance | **Compute → Instances** |
| VPC | **VCN (Virtual Cloud Network)** |
| Security Group (Inbound Rules) | **Subnet → Security List → Ingress Rules** |
| Elastic IP | **Reserved Public IP** (o IP pública efímera al crear la VM) |
| Key Pair (`.pem`) | **SSH Key (`ssh-key-...key` / `.pub`)** |
| Ubuntu 24.04 `ubuntu` user | Usuario `ubuntu` igual que en EC2 |

---

## Paso 1: Crear cuenta y entrar a Oracle Cloud

1. Crea tu cuenta en [cloud.oracle.com](https://www.oracle.com/cloud/free/) (Always Free Tier; pide tarjeta para validar identidad pero **no cobra** si usas recursos *Always Free*).
2. En el menú superior izquierdo (**☰**), entra a **Compute → Instances → Create Instance**.

---

## Paso 2: Configurar la instancia (Always Free)

1. **Name**: `taskflow-api`
2. **Placement**: deja el Availability Domain por defecto.
3. **Image and Shape**:
   - **Image**: haz clic en *Edit* → *Change image* → **Canonical Ubuntu 24.04** (o *Minimal*).
   - **Shape**: haz clic en *Edit* → *Change shape* → **Ampere (`VM.Standard.A1.Flex`)** con `1 OCPU` y `6 GB RAM` (es Always Free y muy potente; si en tu región no hay capacidad ARM en ese momento, usa **AMD `VM.Standard.E2.1.Micro`**, también *Always Free*, 1 GB RAM, suficiente para FastAPI + SQLite).
4. **Networking**:
   - Selecciona **Create new virtual cloud network** (y **Create new public subnet**).
   - Marca **Assign a public IPv4 address** (*sí*).
5. **Add SSH keys**:
   - Elige **Generate a key pair for me** y **descarga la Private Key (`.key`)** (o sube tu `~/.ssh/id_ed25519.pub` si ya tienes llave local).
6. **Boot Volume**: deja el default (`47 GB`, *Always Free*).
7. Pulsa **Create** y espera 1–2 minutos hasta que el estado sea **RUNNING**. Anota tu **Public IPv4 address**.

---

## Paso 3: Abrir el puerto `8000` en la red de Oracle (Security List)

En la página de tu instancia `taskflow-api`:
1. En **Instance Details → Primary VNIC**, haz clic en tu **Subnet** (`subnet-...`).
2. Entra a **Security Lists → Default Security List for vcn-...**.
3. En **Ingress Rules**, haz clic en **Add Ingress Rules**:
   - **Source CIDR**: `0.0.0.0/0`
   - **IP Protocol**: `TCP`
   - **Source Port Range**: *(dejar vacío / All)*
   - **Destination Port Range**: `8000`
   - **Description**: `TaskFlow FastAPI HTTP`
4. Haz clic en **Add Ingress Rules**. *(Si más adelante agregas dominio + HTTPS con Nginx/Caddy, abre también `80` y `443`).*

---

## Paso 4: Conectarte por SSH y subir el backend

En tu computadora (Linux):

```bash
chmod 400 ~/Downloads/ssh-key-*.key
ssh -i ~/Downloads/ssh-key-*.key ubuntu@TU_IP_PUBLICA
```

Una vez dentro de la instancia, sube tu carpeta `backend/` (o clona tu repositorio de GitHub):

**Opción A — Si subiste tu repo a GitHub (recomendado):**
```bash
git clone https://github.com/TU_USUARIO/TaskFlow.git
cd TaskFlow/backend
```

**Opción B — Copiar directo desde tu PC con `scp`:**
*(Ejecuta esto en una terminal de tu PC, fuera del SSH)*
```bash
scp -i ~/Downloads/ssh-key-*.key -r /home/emma/AndroidStudioProjects/TaskFlow/backend ubuntu@TU_IP_PUBLICA:~/TaskFlow-backend
```
Y luego en el servidor SSH: `cd ~/TaskFlow-backend`.

---

## Paso 5: Ejecutar el script automático en OCI

Dentro del servidor (en `backend/`):

```bash
chmod +x deploy/setup_oci.sh
./deploy/setup_oci.sh
```

¿Qué hace este script por ti?
1. Instala **Docker** y **Docker Compose**.
2. Abre los puertos `8000`, `80` y `443` en las reglas **`iptables` internas de Ubuntu OCI** (en Oracle Cloud esto es obligatorio además del Security List, porque Ubuntu en OCI trae reglas `REJECT` locales en `iptables`).
3. Crea un archivo `.env` con una `SECRET_KEY` aleatoria segura.
4. Construye e inicia la API con persistencia de la base SQLite en volumen Docker (`taskflow_data`).

> **Nota si prefieres sin Docker (`systemd`):** también incluimos `deploy/taskflow-api.service`. Puedes crear `.venv`, instalar `requirements.txt` y registrar el servicio con `systemctl`.

---

## Paso 6: Verificar que la API responde desde Internet

1. En el mismo servidor:
   ```bash
   curl http://127.0.0.1:8000/health
   ```
2. Desde el navegador de tu PC:
   - `http://TU_IP_PUBLICA:8000/health` → debe mostrar `{"status":"ok","service":"TaskFlow API","version":"1.0.0"}`
   - `http://TU_IP_PUBLICA:8000/docs` → interfaz Swagger lista para probar `/register`, `/login` y `/tasks`.

---

## Paso 7: Conectar tu app Android a la instancia de Oracle

En la raíz del proyecto Android (`/home/emma/AndroidStudioProjects/TaskFlow`), edita tu archivo local `local.properties` (que está ignorado por Git) y agrega:

```properties
taskflow.apiUrl=http://TU_IP_PUBLICA:8000/
```

Vuelve a compilar/ejecutar desde Android Studio.
- Si **no** agregas `taskflow.apiUrl` en `local.properties`, en compilaciones **Debug** la app apunta automáticamente a `http://10.0.2.2:8000/` (ideal para probar contra tu `uvicorn` local desde el emulador Android).
- Importante por rúbrica: el tráfico HTTP en texto plano (`usesCleartextTraffic` / `network_security_config`) está habilitado **únicamente en el variante `debug`** (`app/src/debug/`). Si algún día publicas en modo `release`, usa HTTPS (p. ej. poniendo Caddy delante con un dominio gratuito o DuckDNS/sslip.io).
