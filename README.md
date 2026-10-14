# Banco de Preguntas Saber Pro

**Universidad del Cauca · Ingeniería de Sistemas**  
**Asignatura:** Ingeniería de Software II  
**Periodo académico:** 2026.2  
**Desarrollado por:** Britney Benítez Ordoñez  
**Modalidad:** Desarrollo individual

## Descripción

**Banco de Preguntas Saber Pro** es una aplicación de escritorio desarrollada en **Java 21 y Java Swing** para gestionar el registro, envío a revisión, consulta y asignación de preguntas de selección múltiple con única respuesta.

El proyecto integra las cuatro historias de usuario **HU-01, HU-02, HU-03 y HU-04**, con validaciones de negocio, persistencia local mediante **SQLite**, notificaciones por correo electrónico mediante **SMTP**, pruebas automatizadas con **JUnit 5** y seguimiento del trabajo en **GitHub y Jira**.

## Funcionalidades implementadas

| Historia de usuario | Funcionalidad | Estado |
|---|---|---|
| **HU-01** | Registro de preguntas Saber Pro con validaciones | Completada |
| **HU-02** | Envío de preguntas a revisión y control de estados | Completada |
| **HU-03** | Consulta de preguntas con filtros y paginación | Completada |
| **HU-04** | Registro y asignación de revisores con notificaciones por correo | Completada |

### HU-01. Registro de preguntas

El perfil **Autor** permite registrar preguntas con contexto, enunciado, cuatro distractores, respuesta correcta, justificación, bibliografía, competencia, tema, subtema y nivel de dificultad.

El sistema aplica validaciones y almacena cada pregunta con un identificador único y estado inicial `BORRADOR`.

### HU-02. Envío a revisión

El autor puede enviar sus preguntas desde el estado `BORRADOR` a `PENDIENTE_REVISION`.

El sistema verifica las condiciones de la transición y que la pregunta pertenezca al autor que realiza la operación.

### HU-03. Consulta de preguntas

Permite consultar las preguntas del autor, aplicar filtros por estado y dificultad, visualizar los detalles e identificar sus estados.

Los resultados se presentan con **paginación de 10 registros**.

### HU-04. Asignación de revisores

El perfil **Administrador** permite registrar docentes revisores, consultar preguntas pendientes, asignar uno o varios revisores y visualizar las asignaciones efectuadas.

El sistema controla los registros duplicados y envía notificaciones mediante correo electrónico.

Las asignaciones se almacenan en SQLite. Cuando una notificación no puede enviarse, su estado se conserva para permitir el **reintento del envío**.

## Tecnologías utilizadas

| Tecnología | Aplicación |
|---|---|
| **Java 21** | Desarrollo del sistema |
| **Java Swing** | Interfaz gráfica |
| **Apache Maven** | Compilación y gestión de dependencias |
| **SQLite y JDBC** | Persistencia de datos |
| **Jakarta Mail y Eclipse Angus Mail** | Notificaciones SMTP |
| **JUnit 5** | Pruebas automatizadas |
| **Git y GitHub** | Control de versiones y Pull Requests |
| **Jira** | Gestión de historias de usuario y sprints |

## Arquitectura del sistema

El proyecto utiliza una **arquitectura monolítica modular de tres capas**, complementada con el patrón **MVC (Modelo–Vista–Controlador)**.

### Capa de presentación

Contiene las interfaces gráficas de Java Swing y los controladores responsables de recibir las acciones del usuario.

### Capa de dominio

Contiene los modelos, interfaces, servicios, validadores y reglas de negocio que permiten desarrollar las funcionalidades del sistema.

### Capa de infraestructura

Contiene los componentes de persistencia SQLite y los servicios de notificaciones mediante SMTP.

### Principios y patrones de diseño

La implementación incorpora:

- **SOLID:** separación de responsabilidades y uso de abstracciones.
- **MVC:** organización de vistas, controladores y lógica de negocio.
- **Repository:** separación entre el dominio y el acceso a los datos.
- **Decorator:** ampliación del comportamiento del notificador mediante el registro de resultados.
- **Inyección de dependencias:** comunicación entre componentes mediante interfaces.

El patrón **Decorator** se implementa en `NotificadorRevisionConRegistro`, que complementa el envío de correos con el registro de los resultados de las notificaciones.

### Estructura del proyecto

```text
src/
├── main/
│   └── java/co/edu/unicauca/saberpro/
│       ├── Main.java
│       ├── presentation/
│       │   ├── view/
│       │   └── controller/
│       ├── domain/
│       │   ├── model/
│       │   ├── service/
│       │   ├── repository/
│       │   └── validation/
│       └── infrastructure/
│           ├── persistence/
│           └── notification/
└── test/
    └── java/
```

Los diagramas **C4 y UML**, requisitos, escenarios de calidad y decisiones arquitectónicas se encuentran en el [documento técnico del proyecto](docs/Banco_Preguntas_Saber_Pro_Documento_Final.pdf).

## Instalación y ejecución

### Requisitos

- Java JDK 21.
- Apache Maven.
- Git.
- Entorno gráfico compatible con Java Swing.

SQLite no requiere instalar un servidor de base de datos.

### 1. Clonar el repositorio

```bash
git clone https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026.git
cd banco-preguntas-saber-pro-2026
```

### 2. Compilar el proyecto

```bash
mvn clean compile
```

### 3. Ejecutar las pruebas

```bash
mvn clean test
```

### 4. Iniciar la aplicación

```bash
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
```

Al iniciar la aplicación, se puede seleccionar uno de los siguientes perfiles:

- **Autor:** registrar preguntas, enviarlas a revisión y consultarlas (HU-01, HU-02 y HU-03).
- **Administrador:** registrar y asignar revisores, consultar asignaciones y gestionar notificaciones (HU-04).

## Persistencia de datos

La aplicación utiliza SQLite para conservar la información de preguntas, revisores, asignaciones y notificaciones.

La base de datos local se encuentra en:

```text
data/banco_preguntas.db
```

Los datos permanecen almacenados entre ejecuciones de la aplicación.

## Notificaciones por correo electrónico

La HU-04 utiliza **SMTP con STARTTLS** para enviar notificaciones a los docentes revisores.

El sistema obtiene los datos de conexión mediante variables de entorno.

| Variable | Descripción |
|---|---|
| `SMTP_HOST` | Servidor SMTP |
| `SMTP_PORT` | Puerto SMTP |
| `SMTP_USER` | Correo de autenticación |
| `SMTP_PASSWORD` | Contraseña de aplicación |
| `SMTP_FROM` | Correo remitente |

### Configuración en Windows con PowerShell

```powershell
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"
$env:SMTP_USER = Read-Host "Correo Gmail remitente"
$env:SMTP_FROM = $env:SMTP_USER
$clave = Read-Host "Contraseña de aplicación" -AsSecureString
$env:SMTP_PASSWORD = [System.Net.NetworkCredential]::new("", $clave).Password

mvn exec:java "-Dexec.mainClass=co.edu.unicauca.saberpro.Main"
```

### Configuración en Linux

```bash
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"

read -rp "Correo Gmail remitente: " SMTP_USER
export SMTP_USER
export SMTP_FROM="$SMTP_USER"

read -rsp "Contraseña de aplicación: " SMTP_PASSWORD
echo
SMTP_PASSWORD="${SMTP_PASSWORD//[[:space:]]/}"
export SMTP_PASSWORD

mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
```

Las credenciales de correo son privadas y no se almacenan en el repositorio.

## Pruebas automatizadas

Se implementaron pruebas utilizando **JUnit 5** para verificar el comportamiento de las funcionalidades del sistema.

Las pruebas abarcan:

- Registro y validación de preguntas.
- Reglas de negocio.
- Transiciones de estado.
- Filtros y paginación.
- Controladores y servicios.
- Persistencia SQLite.
- Registro y asignación de revisores.
- Control de registros duplicados.
- Notificaciones y reintentos.

### Resultado registrado de las pruebas

| Indicador | Resultado |
|---|---|
| Pruebas ejecutadas | **180** |
| Fallos | **0** |
| Errores | **0** |
| Pruebas omitidas | **0** |
| Resultado Maven | **BUILD SUCCESS** |

Para ejecutar las pruebas automatizadas:

```bash
mvn clean test
```

## Gestión del proyecto con Scrum

El desarrollo se organizó mediante **cuatro sprints**, correspondientes a las historias de usuario HU-01, HU-02, HU-03 y HU-04.

Se utilizó **Jira** para gestionar las historias de usuario, sus actividades y el seguimiento de los sprints.

Mediante **Git y GitHub** se realizó el control de versiones, la creación de ramas de desarrollo y la integración del código a través de Pull Requests.

El desarrollo, las pruebas y la documentación de esta entrega se realizaron individualmente.

## Documentación del proyecto

| Recurso | Enlace |
|---|---|
| **Repositorio GitHub** | [Banco de Preguntas Saber Pro 2026](https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026) |
| **Proyecto Jira** | [Tablero y sprints PSP2](https://banco-preguntas-unicauca.atlassian.net/jira/software/projects/PSP2/summary) |
| **Documento técnico PDF** | [Arquitectura, requisitos y diseño](docs/Banco_Preguntas_Saber_Pro_Documento_Final.pdf) |
| **Backlog y seguimiento Excel** | [Backlog del proyecto](docs/Backlog_Jira_PSP2_Saber_Pro_2026.xlsx) |
| **Material de sustentación** | [Google Drive](https://drive.google.com/drive/folders/1_5HzJgT9fSfKCVkjcS_2WyRDT6QKCIa8?usp=sharing) |
| **Pull Request de documentación** | [PR #8](https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026/pull/8) |

---

**Britney Benítez Ordoñez**  
Ingeniería de Sistemas — Universidad del Cauca  
Ingeniería de Software II · 2026.2
