
# Banco de Preguntas Saber Pro

**Universidad del Cauca**  
**Programa:** Ingeniería de Sistemas  
**Asignatura:** Ingeniería de Software II  
**Periodo académico:** 2026.2  
**Desarrollo:** Proyecto académico individual

**Aplicación de escritorio desarrollada en Java para el registro, consulta y gestión de preguntas Saber Pro.**

---

## 1. Descripción del proyecto

**Banco de Preguntas Saber Pro** es una aplicación académica de escritorio que permite registrar, validar, consultar y gestionar preguntas de selección múltiple con única respuesta.

El sistema apoya el proceso de construcción y revisión de preguntas mediante dos perfiles demostrativos: **Autor** y **Administrador**. No dispone de autenticación real de usuarios.

Se implementó en **Java 21 y Java Swing**, con una arquitectura monolítica de **tres capas** (presentación, dominio e infraestructura), el patrón **MVC**, principios **SOLID**, patrones de diseño y persistencia local mediante **SQLite**.

La primera entrega comprende las cuatro historias de usuario **HU-01 a HU-04**. El proyecto fue desarrollado individualmente y se gestionó mediante GitHub y Jira.

---

## 2. Historias de usuario implementadas

| Historia | Funcionalidad | Estado |
|---|---|---|
| **HU-01** | Registrar preguntas Saber Pro con validaciones estructurales. | Implementada |
| **HU-02** | Enviar preguntas propias desde BORRADOR a PENDIENTE_REVISION. | Implementada |
| **HU-03** | Consultar preguntas del autor con filtros, paginación y detalles. | Implementada |
| **HU-04** | Registrar docentes revisores, asignarlos a preguntas pendientes, notificar por correo y reintentar envíos fallidos. | Implementada |

### HU-01. Registro de preguntas

El perfil **Autor** permite registrar preguntas con los siguientes datos:

- Contexto y enunciado.
- Cuatro distractores y una respuesta correcta.
- Justificación de la respuesta.
- Bibliografía.
- Competencia, tema y subtema.
- Nivel de dificultad.

El sistema valida los campos obligatorios y las opciones antes de guardar la pregunta en SQLite con un identificador único y estado inicial **BORRADOR**.

### HU-02. Envío de preguntas a revisión

El autor puede cambiar una pregunta propia de `BORRADOR` a `PENDIENTE_REVISION`. La operación valida la propiedad de la pregunta y su estado actual; no permite realizar repetidamente una transición que ya fue efectuada.

### HU-03. Consulta de preguntas

El autor puede:

- Consultar únicamente sus preguntas.
- Filtrar por estado y dificultad.
- Navegar por páginas de **10 registros**.
- Consultar el total de resultados y los detalles de cada pregunta.
- Identificar visualmente el estado de las preguntas.

### HU-04. Asignación de revisores y notificaciones

El perfil **Administrador** permite:

- Registrar docentes revisores con identificador, nombre y correo electrónico.
- Rechazar registros de revisores duplicados.
- Consultar preguntas pendientes de revisión.
- Asignar uno o varios docentes a una pregunta.
- Evitar que se repita la misma pareja pregunta–revisor.
- Consultar las asignaciones existentes.
- Enviar notificaciones mediante SMTP.
- Registrar estados `PENDIENTE` y `ENVIADA`.
- Reintentar notificaciones pendientes después de un fallo.

**La asignación permanece almacenada en SQLite incluso si falla el envío del correo electrónico.**

---

## 3. Tecnologías utilizadas

| Tecnología | Uso en el proyecto |
|---|---|
| **Java 21** | Lenguaje de programación. |
| **Java Swing** | Interfaz gráfica de escritorio. |
| **Apache Maven** | Compilación, dependencias y ejecución de pruebas. |
| **SQLite / JDBC** | Persistencia local de datos. |
| **Jakarta Mail y Eclipse Angus Mail** | Notificaciones por SMTP. |
| **JUnit 5** | Pruebas automatizadas. |
| **Git y GitHub** | Control de versiones y Pull Requests. |
| **Jira** | Historias de usuario, subtareas y seguimiento de sprints. |
| **Visual Studio Code** | Entorno de desarrollo compatible con Java y Maven. |

---

## 4. Arquitectura del sistema

La aplicación utiliza una **arquitectura monolítica modular de tres capas** y organiza la interacción de las interfaces mediante **MVC (Modelo–Vista–Controlador)**.

### 4.1. Capa de presentación

Contiene las vistas **Java Swing** y sus controladores. Gestiona formularios, acciones de usuario, listados y mensajes, y delega la lógica de negocio a los servicios.

Entre sus componentes se encuentran `RegistroPreguntaView`, `PreguntaEstadoView`, `ConsultaPreguntaView`, `AsignacionRevisionView` y `RegistroRevisorView`.

### 4.2. Capa de dominio

Contiene las entidades, contratos, validadores y servicios responsables de ejecutar las reglas de negocio: registro, cambio de estado, consulta, autorización demostrativa, asignación de revisores y reintento de notificaciones.

### 4.3. Capa de infraestructura

Contiene los repositorios **SQLite**, la comunicación **JDBC** y los componentes de notificación por **SMTP/STARTTLS**.

### 4.4. Estructura principal del código

```text
src/
├── main/
│   └── java/
│       └── co/edu/unicauca/saberpro/
│           ├── Main.java
│           ├── presentation/
│           │   ├── view/
│           │   └── controller/
│           ├── domain/
│           │   ├── model/
│           │   ├── service/
│           │   ├── repository/
│           │   └── validation/
│           └── infrastructure/
│               ├── persistence/
│               └── notification/
└── test/
    └── java/
```

### 4.5. Principios y patrones de diseño

- **MVC:** separa la interacción visual, la coordinación de acciones y la lógica de negocio.
- **Repository:** evita que los servicios dependan directamente de sentencias SQL.
- **Decorator (GoF):** `NotificadorRevisionConRegistro` agrega el registro de resultados a las notificaciones sin modificar la responsabilidad del notificador SMTP.
- **Inyección de dependencias:** los servicios reciben interfaces de repositorios y notificación, facilitando el reemplazo de implementaciones y las pruebas.
- **SOLID:** promueve separación de responsabilidades y dependencias sobre contratos.

Los diagramas **C4, UML, modelos de datos, decisiones arquitectónicas y escenarios de modificabilidad** se encuentran en el [documento de arquitectura](docs/Banco_Preguntas_Saber_Pro_Documento_Final.pdf).

---

## 5. Instalación y ejecución

### 5.1. Requisitos

- **JDK 21**.
- **Apache Maven**.
- **Git**.
- **Visual Studio Code** u otro IDE compatible con Maven (opcional).
- **Entorno gráfico** capaz de mostrar ventanas Swing.

No se necesita instalar un servidor de base de datos: **SQLite** utiliza un archivo local.

### 5.2. Clonar el repositorio

```bash
git clone https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026.git
cd banco-preguntas-saber-pro-2026
```

### 5.3. Verificar Java y Maven

```bash
java -version
mvn -version
```

### 5.4. Compilar el proyecto

```bash
mvn clean compile
```

### 5.5. Ejecutar las pruebas automatizadas

```bash
mvn clean test
```

### 5.6. Iniciar la aplicación

```bash
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
```

Al iniciar, selecciona uno de los perfiles:

- **Autor (HU-01 a HU-03):** registra, envía a revisión y consulta preguntas propias.
- **Administrador (HU-04):** registra y asigna docentes revisores, consulta asignaciones y gestiona notificaciones.

**Nota:** los perfiles son demostrativos. No se ha implementado inicio de sesión con contraseñas ni autenticación de usuarios productivos.

---

## 6. Base de datos SQLite

Al iniciar la aplicación, se crea automáticamente el archivo de base de datos local:

```text
data/banco_preguntas.db
```

Se almacena información de preguntas, distractores, revisores, asignaciones y estados de notificación.

La carpeta `data/` está excluida del control de versiones mediante `.gitignore`. Por tanto, **cada instalación tiene su propia base de datos**, y los registros de un computador no se transfieren automáticamente a otro.

Para probar **HU-04** desde una instalación limpia, primero debes registrar una pregunta con el perfil Autor, enviarla a revisión y, después, registrar un docente desde el perfil Administrador.

---

## 7. Notificaciones por correo electrónico — HU-04

La aplicación envía notificaciones mediante **SMTP**, con autenticación y cifrado **STARTTLS**. Al asignar un revisor, el sistema intenta enviar un correo a la dirección registrada para dicho docente.

El mensaje contiene el nombre del revisor y la información necesaria para identificar la pregunta asignada. El asunto utilizado es **«Nueva pregunta asignada para revisión»**.

### 7.1. Verificación funcional de correo

En una comprobación manual registrada durante el desarrollo se obtuvo:

| Resultado | Cantidad |
|---|---:|
| Notificaciones procesadas | 1 |
| Notificaciones enviadas | 1 |
| Notificaciones fallidas | 0 |

Se comprobó adicionalmente la recepción del mensaje en la bandeja del destinatario de prueba. Este resultado corresponde a esa ejecución concreta; el envío en otros equipos depende de la configuración SMTP y del proveedor.

### 7.2. Variables de entorno SMTP

**No se incluyen credenciales privadas ni contraseñas en GitHub.** Para enviar correos reales, cada evaluador debe configurar sus propias variables de entorno:

| Variable | Propósito |
|---|---|
| `SMTP_HOST` | Dirección del servidor de correo. |
| `SMTP_PORT` | Puerto de conexión SMTP. |
| `SMTP_USER` | Usuario de autenticación. |
| `SMTP_PASSWORD` | Contraseña SMTP o contraseña de aplicación. |
| `SMTP_FROM` | Dirección del remitente. |

Con Gmail, un ejemplo de servidor y puerto es:

```text
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
```

La cuenta remitente debe permitir el acceso SMTP y contar con una contraseña de aplicación válida, cuando corresponda. **No se debe publicar una contraseña real en el repositorio.**

### 7.3. Configuración en Windows (PowerShell)

En una terminal de PowerShell:

```powershell
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"

$env:SMTP_USER = Read-Host "Correo Gmail remitente"
$env:SMTP_FROM = $env:SMTP_USER

$clave = Read-Host "Contraseña de aplicación" -AsSecureString
$env:SMTP_PASSWORD = [System.Net.NetworkCredential]::new("", $clave).Password
```

Luego, **en esa misma terminal**, ejecuta:

```powershell
mvn exec:java "-Dexec.mainClass=co.edu.unicauca.saberpro.Main"
```

### 7.4. Configuración en Linux o Google Cloud Shell

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
```

Luego, en esa misma terminal:

```bash
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
```

**Google Cloud Shell:** una terminal por sí sola no muestra interfaces gráficas Java Swing. Para la demostración se necesita un entorno gráfico, por ejemplo uno configurado mediante Xvfb y noVNC. En Windows con escritorio, esto no es necesario.

### 7.5. ¿Qué sucede si falla el correo?

1. La asignación del docente permanece guardada en SQLite.
2. La notificación conserva el estado `PENDIENTE`.
3. El administrador puede corregir la configuración SMTP.
4. Después puede utilizar la opción **Reintentar notificaciones**.
5. Si el proveedor acepta el envío y se guarda correctamente el resultado, el estado cambia a `ENVIADA`.

El sistema también impide duplicar la asignación de un mismo revisor a una misma pregunta.

**Importante:** la aceptación del mensaje por el servidor SMTP no garantiza su entrega en la bandeja principal del destinatario; también puede ser necesario revisar la carpeta de correo no deseado.

### 7.6. Seguridad de credenciales

- No almacenar contraseñas dentro de `Main.java`, `pom.xml` ni otros archivos versionados.
- No subir capturas que muestren secretos ni compartir contraseñas de aplicación.
- Configurar SMTP por variables de entorno propias.
- Los archivos `.env` se excluyen mediante `.gitignore`, pero la aplicación **no los lee automáticamente**: utiliza `System.getenv`.

---

## 8. Pruebas automatizadas

Las pruebas se desarrollaron con **JUnit 5** y abarcan:

- Entidades y validaciones de campos.
- Registro y consulta de preguntas.
- Cambios de estado y reglas de autorización.
- Filtros, paginación y controladores.
- Persistencia con SQLite.
- Registro y asignación de revisores.
- Restricciones de duplicidad.
- Notificaciones, fallos y reintentos.

Para ejecutarlas desde la raíz del proyecto:

```bash
mvn clean test
```

### Resultado documentado de una ejecución exitosa

| Indicador | Resultado registrado |
|---|---:|
| Pruebas ejecutadas | **180** |
| Fallos | **0** |
| Errores | **0** |
| Pruebas omitidas | **0** |
| Resultado Maven | **BUILD SUCCESS** |

Estos datos corresponden a una ejecución exitosa documentada durante el desarrollo; para verificar el estado de la versión descargada es necesario volver a ejecutar `mvn clean test`.

Las pruebas automatizadas no requieren credenciales personales de Gmail. El envío real de correo se verificó por separado mediante una prueba manual.

---

## 9. Estado de la entrega y limitaciones

### 9.1. Trabajo implementado

Las cuatro historias de usuario HU-01 a HU-04 están integradas en `main`, junto con las interfaces Swing, la persistencia SQLite, la notificación SMTP y los tests automatizados.

### 9.2. Documentación disponible

El **documento de arquitectura en PDF** y el **backlog de Jira en Excel** ya fueron incorporados a la carpeta [`docs/`](docs/) mediante el **[Pull Request #8](https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026/pull/8)**.

El documento presenta requisitos, criterios de aceptación, diagramas C4 y UML, patrones de diseño, escenarios de calidad, pruebas y evidencias de gestión del proyecto.

### 9.3. Aspectos que deben verificarse para la entrega académica

- **Usabilidad:** se documentó una autoevaluación; la prueba independiente con usuarios externos debe distinguirse de los ejercicios simulados o propuestos.
- **Video de sustentación:** se proporciona una carpeta de Google Drive en la sección 11; debe comprobarse que incluya el video y permita acceso. La guía académica solicita publicación en YouTube.
- **Instalación limpia:** es recomendable comprobar compilación y apertura de la interfaz en otro computador con Java 21, Maven y entorno gráfico.
- **Correo en otros equipos:** el envío SMTP requiere variables de entorno y credenciales propias del evaluador.

### 9.4. Limitaciones conocidas

- **Perfiles demostrativos:** se puede elegir Autor o Administrador, pero no existe autenticación productiva.
- **Base de datos local:** no hay sincronización multiusuario entre computadores.
- **Dependencia externa SMTP:** el correo depende de la conexión, la configuración y las políticas del proveedor.

Estas limitaciones describen el alcance de la primera entrega y no se presentan como funcionalidades implementadas.

---

## 10. Control de versiones y gestión ágil

El código se administra con **Git y GitHub**. La rama `main` contiene los cambios integrados mediante Pull Requests.

| Rama | Propósito |
|---|---|
| `main` | Versión principal del proyecto. |
| `feature/hu-01` | Desarrollo de HU-01. |
| `feature/hu-02` | Desarrollo de HU-02. |
| `feature/hu-03` | Desarrollo de HU-03. |
| `feature/hu-04` | Desarrollo de HU-04. |
| `docs/entregables-primer-corte` | Incorporación del PDF y del Excel a `docs/`, integrada a `main` mediante PR #8. |

El seguimiento se realizó en **Jira (proyecto PSP2)** mediante cuatro sprints correspondientes a las cuatro historias de usuario. Los sprints se cerraron y las subtareas de seguimiento se registraron posteriormente; por tanto, la vista actual de Jira no debe confundirse con una captura de la planificación histórica original.

El desarrollo y la documentación corresponden a una única desarrolladora: **Britney Benítez Ordoñez**.

**Repositorio:** https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026

---

## 11. Documentación y enlaces de la entrega

| Recurso | Enlace |
|---|---|
| **Tablero y sprints en Jira** | [Abrir proyecto PSP2](https://banco-preguntas-unicauca.atlassian.net/jira/software/projects/PSP2/summary) |
| **Documento de arquitectura y requisitos (PDF)** | [Ver documento](docs/Banco_Preguntas_Saber_Pro_Documento_Final.pdf) |
| **Backlog y seguimiento de sprints (Excel)** | [Descargar Excel](docs/Backlog_Jira_PSP2_Saber_Pro_2026.xlsx) |
| **Sustentación (carpeta de Google Drive)** | [Abrir carpeta de Drive](https://drive.google.com/drive/folders/1_5HzJgT9fSfKCVkjcS_2WyRDT6QKCIa8?usp=sharing) |
| **Pull Request de documentación** | [Consultar PR #8](https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026/pull/8) |
| **Código fuente** | [Repositorio GitHub](https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026) |

**Nota sobre el video:** el enlace de Google Drive lleva a una carpeta, no directamente a un video. Antes de la entrega, verificar sus permisos y, si corresponde, agregar también el enlace del video publicado en YouTube.

---

**Proyecto académico — Universidad del Cauca**  
**Ingeniería de Software II · Periodo 2026.2**
