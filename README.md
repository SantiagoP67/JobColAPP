# JobCol - Plataforma de Empleabilidad y Evaluación Adaptativa

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-blueviolet?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.4-blue?style=flat-square&logo=react)](https://react.dev/)
[![Vite](https://img.shields.io/badge/Vite-6.4.1-646CFF?style=flat-square&logo=vite)](https://vitejs.dev/)
[![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-316192?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Keycloak](https://img.shields.io/badge/Keycloak-24.0.2-red?style=flat-square&logo=keycloak)](https://www.keycloak.org/)
[![Docker](https://img.shields.io/badge/Docker-Docker%20Compose-blue?style=flat-square&logo=docker)](https://www.docker.com/)

---

## Tabla de Contenido
1. [Descripción del Proyecto](#descripción-del-proyecto)
2. [Características Principales](#características-principales)
3. [Tecnologías Utilizadas](#tecnologías-utilizadas)
4. [Requisitos Previos](#requisitos-previos)
5. [Estructura del Proyecto](#estructura-del-proyecto)
6. [Quick Start (Inicio Rápido)](#quick-start-inicio-rápido)
7. [Configuración de Variables de Entorno](#configuración-de-variables-de-entorno)
8. [Puertos Utilizados](#puertos-utilizados)
9. [Instalación y Ejecución Detallada](#instalación-y-ejecución-detallada)
    * [Paso 1: Infraestructura y Base de Datos (Docker)](#paso-1-infraestructura-y-base-de-datos-docker)
    * [Paso 2: Backend (Spring Boot)](#paso-2-backend-spring-boot)
    * [Paso 3: Frontend (React + Vite)](#paso-3-frontend-react--vite)
10. [Uso con Docker y Docker Compose](#uso-con-docker-y-docker-compose)
11. [Scripts Disponibles](#scripts-disponibles)

---

## Descripción del Proyecto

JobCol es una plataforma web moderna diseñada para la gestión de empleo y la evaluación adaptativa de habilidades, enfocada en optimizar la contratación en el sector informal y formal en Colombia. La plataforma proporciona herramientas para la postulación a ofertas de trabajo, la formalización de acuerdos mediante contratos digitales, la comunicación directa en tiempo real mediante mensajería, la valoración de desempeño a través de reseñas y un sistema innovador de pruebas técnicas adaptativas basadas en Inteligencia Artificial.

---

## Características Principales

* **Autenticación Unificada y Segura**: Control de acceso robusto basado en roles (Candidato y Empleador) gestionado mediante Keycloak con validación de tokens JWT en el servidor de recursos.
* **Evaluación Adaptativa con IA**: Pruebas técnicas dinámicas integradas con OpenAI (GPT-4o-Mini) que ajustan su nivel de dificultad (básico, intermedio, avanzado) según el desempeño del candidato y evalúan preguntas abiertas utilizando rúbricas personalizadas.
* **Arquitectura Orientada a Servicios**: Estructura de backend organizada en servicios de dominio desacoplados (Auth, Hiring, Messaging, Notification, Offer, Post, Postulation, Review, User), permitiendo un desarrollo mantenible y escalable.
* **Base de Datos Evolutiva**: Migraciones automáticas del esquema de base de datos controladas mediante Flyway.
* **Servicio de Mensajería y Notificaciones**: Canal de notificaciones y mensajería directa para facilitar el contacto inmediato entre empleadores y candidatos.
* **Seguridad de Datos y Almacenamiento**: Carga de archivos y gestión de perfiles (tanto de empleadores como de candidatos) con validación estricta de esquemas de datos.
* **Integración con Ngrok**: Configuración predefinida para la exposición segura del backend local mediante túneles públicos.

---

## Tecnologías Utilizadas

### Frontend
* **React 19.2.4**: Biblioteca principal para la construcción de interfaces de usuario.
* **Vite 6.4.1**: Herramienta de compilación y servidor de desarrollo ágil.
* **React Router DOM 7.13.1**: Gestión de enrutamiento del lado del cliente.
* **Axios 1.15.2**: Cliente HTTP para la comunicación con las APIs del backend.
* **Lucide React 0.577.0**: Set de iconos vectoriales modernos.
* **Vitest 4.1.6 y Testing Library**: Entorno y utilidades para pruebas unitarias.

### Backend
* **Spring Boot 3.5.10**: Framework de desarrollo de backend en Java.
* **Java 21**: Versión de soporte de largo plazo (LTS) del lenguaje.
* **Spring Data JPA & Hibernate**: Capa de persistencia y mapeo objeto-relacional.
* **Flyway**: Herramienta de migración y control de versiones de bases de datos.
* **Spring Security & OAuth2 Resource Server**: Autenticación y autorización basada en JWT.
* **Keycloak Admin Client 24.0.2**: Biblioteca para la administración programática de usuarios y credenciales en Keycloak.
* **Springdoc OpenAPI (Swagger UI) 2.5.0**: Generación interactiva y visualización de la documentación de la API REST.
* **MapStruct 1.5.5.Final**: Framework seguro para la conversión y mapeo de objetos (DTOs y Entidades).
* **Lombok**: Generador automático de código repetitivo (getters, setters, constructores).

### Infraestructura y DevOps
* **Docker & Docker Compose**: Contenedores para base de datos PostgreSQL, Keycloak y Ngrok.
* **PostgreSQL 14**: Motor de base de datos relacional para la aplicación y para Keycloak.
* **Keycloak 24.0.2**: Servidor de administración de identidad y accesos.
* **Ngrok**: Proveedor de túneles seguros para desarrollo y pruebas externas.

---

## Requisitos Previos

Antes de configurar y ejecutar el proyecto, asegúrese de tener instalados los siguientes componentes en su máquina de desarrollo:

* **Node.js**: Versión 18.x o 20.x (LTS recomendada).
* **NPM**: Incluido con Node.js (versión 9.x o superior).
* **Java Development Kit (JDK)**: Versión 21.
* **Apache Maven**: Versión 3.9.x (opcional si se utiliza el wrapper de Maven `./mvnw` incluido).
* **Docker & Docker Compose**: Para levantar los servicios de infraestructura de base de datos y seguridad.
* **Git**: Para el control de versiones y descarga del repositorio.

---

## Estructura del Proyecto

La estructura general del workspace está organizada de la siguiente manera:

```text
JobCol/
├── .github/                       # Configuraciones del flujo de CI/CD de GitHub Actions
├── JOBCOL FrontEnd/               # Directorio del proyecto del cliente web
│   ├── public/                    # Archivos públicos estáticos
│   ├── src/                       # Código fuente del cliente
│   │   ├── components/            # Componentes reutilizables e interfaces de diálogo
│   │   │   └── views/             # Secciones específicas y vistas principales del Dashboard
│   │   ├── context/               # Proveedores de contexto global (ej. ToastContext)
│   │   ├── pages/                 # Páginas de nivel de ruta (LandingPage, Dashboard)
│   │   ├── services/              # Clientes de API, integraciones (Axios, OpenAI AI Test)
│   │   └── test/                  # Archivos de pruebas frontend (Vitest)
│   ├── package.json               # Configuración de dependencias y scripts de Node
│   ├── vite.config.js             # Configuración del empaquetador Vite
│   └── PRUEBAS_FRONTEND.md        # Documento detallado de pruebas del Frontend
└── JOBCOL BackEnd/                # Directorio del backend e infraestructura
    ├── .env                       # Archivo de variables de entorno globales
    ├── docker-compose.yml         # Orquestación de infraestructura (Postgres, Keycloak, Ngrok)
    ├── infraestructure/           # Configuraciones y recursos de infraestructura
    │   └── keycloak/              # Dockerfile y JSON de importación del Realm de Keycloak
    └── backend/                   # Proyecto Spring Boot (Maven)
        ├── pom.xml                # Definición de dependencias y plugins de compilación
        ├── dockerfile             # Construcción multi-stage de la imagen del backend
        └── src/
            ├── main/
            │   ├── java/com/jobcol/backend/
            │   │   ├── AuthService/         # Servicio de Autenticación y comunicación con Keycloak
            │   │   ├── UserService/         # Servicio de administración de usuarios locales
            │   │   ├── OfferService/        # Servicio de ofertas laborales
            │   │   ├── PostulationService/  # Servicio de postulaciones a ofertas
            │   │   ├── HiringService/       # Servicio de formalización y contratos
            │   │   ├── MessagingService/    # Servicio de mensajería interna
            │   │   ├── NotificationService/ # Servicio de eventos y alertas
            │   │   ├── ReviewService/       # Servicio de valoraciones de desempeño
            │   │   ├── PostService/         # Servicio de publicaciones informativas
            │   │   ├── config/              # Configuraciones globales (CORS, Seguridad, RestTemplate)
            │   │   └── shared/              # Recursos compartidos (DTOs, Mappers, Entidades comunes)
            │   └── resources/
            │       ├── application.yml      # Configuración de Spring Boot
            │       └── db/migration/        # Scripts SQL de migraciones controladas por Flyway
            └── test/                        # Pruebas unitarias e integradas de backend
```

---

## Quick Start (Inicio Rápido)

Para levantar el proyecto completo de manera rápida en su entorno local, siga los siguientes pasos:

### 1. Clonar el repositorio y acceder a la carpeta del backend
```bash
git clone https://github.com/SantiagoP67/JobColAPP.git
cd jobcol/JOBCOL\ BackEnd
```

### 2. Levantar la infraestructura completa con Docker Compose
Inicie la base de datos, Keycloak, el backend de Spring Boot y Ngrok en segundo plano:
```bash
docker compose up -d
```
*Nota: Este comando descarga las imágenes, compila el backend de Spring Boot usando la etapa de construcción multi-stage de Docker y expone el servicio.*

### 3. Iniciar el Frontend
En otra ventana de la terminal, navegue a la carpeta del frontend, instale las dependencias y corra el servidor de desarrollo:
```bash
cd ../JOBCOL\ FrontEnd
npm install
npm run dev
```

Abra el navegador en `http://localhost:5173`. El frontend se conectará con el servidor REST expuesto en `http://localhost:8080`.

---

## Configuración de Variables de Entorno

El archivo `.env` ubicado en la raíz de `JOBCOL BackEnd` contiene las variables de entorno necesarias para configurar las credenciales, conexiones y servicios de IA, base de datos y mensajería. A continuación, se detalla cada una de ellas:

### Explicación de las Variables

* **POSTGRES_DB**: Nombre de la base de datos principal de la aplicación.
* **POSTGRES_USER**: Usuario de la base de datos PostgreSQL principal.
* **POSTGRES_PASSWORD**: Contraseña de acceso de PostgreSQL principal.
* **KEYCLOAK_DB**: Nombre de la base de datos exclusiva para Keycloak.
* **KEYCLOAK_DB_USER**: Usuario para la base de datos de Keycloak.
* **KEYCLOAK_DB_PASSWORD**: Contraseña de la base de datos de Keycloak.
* **KEYCLOAK_ADMIN**: Usuario administrador inicial para la consola de Keycloak.
* **KEYCLOAK_ADMIN_PASSWORD**: Contraseña del usuario administrador de Keycloak.
* **KEYCLOAK_URL**: URL interna/externa de acceso al servidor de Keycloak.
* **KEYCLOAK_REALM**: Nombre del reino (Realm) de autenticación en Keycloak.
* **KEYCLOAK_CLIENT_ID**: Identificador del cliente REST configurado en Keycloak.
* **MAIL_USERNAME**: Dirección de correo electrónico SMTP para envío de notificaciones.
* **MAIL_PASSWORD**: Contraseña de aplicación generada para el servidor de correo.
* **NGROK_AUTHTOKEN**: Token de autenticación de Ngrok para habilitar túneles públicos.
* **NGROK_DOMAIN**: Dominio estático gratuito asignado en su cuenta de Ngrok.

## Puertos Utilizados

El sistema distribuye la red de sus componentes mediante los siguientes puertos asignados al host y a los contenedores:

| Componente | Puerto Host | Puerto Contenedor | Descripción |
| :--- | :--- | :--- | :--- |
| **PostgreSQL** | `5432` | `5432` | Base de datos principal de JobCol (`jobcol_db`). |
| **Keycloak DB** | N/D | `5432` | Base de datos interna de Keycloak (sin mapeo de puerto en host). |
| **Keycloak** | `8180` | `8080` | Servidor de identidades (Consola de Administración y autenticación). |
| **Backend API** | `8080` | `8080` | API REST de Spring Boot (`jobcol-backend`). |
| **Ngrok Dashboard**| `4040` | `4040` | Consola web local para monitorizar el túnel de Ngrok. |
| **Frontend Client**| `5173` | N/D | Servidor de desarrollo local Vite (ejecutado directamente en host). |

---

## Instalación y Ejecución Detallada

### Paso 1: Infraestructura y Base de Datos (Docker)

Para garantizar la consistencia en el desarrollo, inicie primero la base de datos y Keycloak mediante Docker Compose:

1. Asegúrese de ubicarse en la carpeta de infraestructura:
   ```bash
   cd "JOBCOL BackEnd"
   ```

2. Ejecute Docker Compose para levantar únicamente los contenedores necesarios de soporte (excluyendo el backend si prefiere compilarlo localmente):
   ```bash
   docker compose up -d postgres keycloak-db keycloak
   ```

3. Verifique el estado de los contenedores:
   ```bash
   docker compose ps
   ```

Keycloak se iniciará e importará automáticamente el reino pre-configurado desde el archivo montado en `./infraestructure/keycloak/realm/jobcol-realm.json`.

---

### Paso 2: Backend (Spring Boot)

Si prefiere compilar y ejecutar el backend localmente sin Docker para agilizar la depuración:

#### En Windows (PowerShell / Command Prompt):
1. Ingrese a la carpeta del proyecto backend:
   ```powershell
   cd backend
   ```
2. Asegúrese de que el puerto `8080` esté libre y ejecute el wrapper de Maven para compilar y arrancar la aplicación:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

#### En macOS y Linux (Terminal):
1. Ingrese a la carpeta del proyecto backend:
   ```bash
   cd backend
   ```
2. Asigne permisos de ejecución al script del wrapper:
   ```bash
   chmod +x mvnw
   ```
3. Ejecute la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```

*Nota: Al iniciar la aplicación, Flyway aplicará automáticamente las migraciones definidas en `db/migration` a la base de datos PostgreSQL local.*

Una vez levantado, puede acceder a la documentación interactiva de la API (Swagger UI) en la siguiente dirección:
* **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`

---

### Paso 3: Frontend (React + Vite)

El frontend está desarrollado sobre React y se ejecuta localmente mediante Node y npm.

1. Navegue al directorio del frontend:
   ```bash
   cd "JOBCOL FrontEnd"
   ```

2. Instale los módulos de dependencias definidos en el archivo `package.json`:
   ```bash
   npm install
   ```

3. Inicie el servidor local de desarrollo de Vite:
   ```bash
   npm run dev
   ```

Por defecto, Vite abrirá el puerto `5173`. Acceda a `http://localhost:5173` en su navegador para interactuar con la aplicación.

---

## Uso con Docker y Docker Compose

El proyecto backend y toda su infraestructura pueden levantarse juntos de manera coordinada utilizando el archivo `docker-compose.yml` que se encuentra en la carpeta principal `JOBCOL BackEnd`.

### Comandos de Docker Compose

#### Construir y levantar todo el ecosistema
Este comando compilará el backend utilizando el `dockerfile` multi-stage y levantará todos los servicios de base de datos, seguridad, mensajería y ngrok en segundo plano:
```bash
docker compose up -d --build
```

#### Detener y eliminar los contenedores activos
Para detener los servicios sin perder los datos almacenados en los volúmenes de PostgreSQL y Keycloak:
```bash
docker compose down
```

#### Detener los servicios eliminando volúmenes y datos
Si requiere restablecer el estado completo de la base de datos de la aplicación y Keycloak desde cero:
```bash
docker compose down -v
```

#### Ver logs de un servicio específico
Por ejemplo, para inspeccionar el flujo de inicialización del backend:
```bash
docker compose logs -f backend
```

---

## Scripts Disponibles

### Frontend (`JOBCOL FrontEnd`)

Los scripts declarados en `package.json` para la ejecución, prueba y compilación del cliente web son:

* **`npm run dev`**: Levanta el servidor local de desarrollo con recarga rápida (HMR).
* **`npm run build`**: Compila y optimiza la aplicación para producción en la carpeta `/dist`.
* **`npm run preview`**: Levanta localmente un servidor estático para previsualizar los archivos construidos en `/dist`.
* **`npm run test`**: Ejecuta las pruebas unitarias e integradas utilizando el framework **Vitest**.

### Backend (`JOBCOL BackEnd/backend`)

El proyecto Spring Boot utiliza Maven para gestionar el ciclo de vida del software. Los comandos más utilizados son:

* **`./mvnw clean`**: Limpia la carpeta temporal `/target` donde se almacenan las compilaciones previas.
* **`./mvnw compile`**: Compila las clases del código fuente de la aplicación.
* **`./mvnw test`**: Ejecuta el set de pruebas unitarias y de integración del backend.
* **`./mvnw package -DskipTests`**: Compila el código y genera el paquete de distribución JAR en la ruta `target/backend-0.0.1-SNAPSHOT.jar`, saltando la ejecución de pruebas.

---
