# Banco de Preguntas Saber Pro

**Universidad del Cauca**  
**Programa:** Ingeniería de Sistemas  
**Asignatura:** Ingeniería de Software II  
**Periodo académico:** 2026.2

**Aplicación de escritorio desarrollada en Java para la gestión de preguntas Saber Pro.**

---

## 1. Descripción del proyecto

**Banco de Preguntas Saber Pro** es una aplicación académica que permite registrar, validar, consultar y administrar preguntas de selección múltiple con única respuesta.

El sistema facilita el proceso de construcción y revisión de preguntas mediante dos perfiles: **Autor** y **Administrador**.

El proyecto fue desarrollado utilizando una arquitectura monolítica de tres capas, el patrón de arquitectura MVC, principios SOLID y mecanismos de persistencia con SQLite.

La implementación comprende las cuatro historias de usuario correspondientes al primer corte de Ingeniería de Software II.

## 2. Historias de usuario implementadas

| Historia | Funcionalidad | Estado |
|---|---|---|
| **HU-01** | Registrar preguntas Saber Pro con sus respectivos campos y validaciones estructurales. | Implementada |
| **HU-02** | Cambiar el estado de las preguntas de borrador a pendiente de revisión. | Implementada |
| **HU-03** | Consultar las preguntas del autor mediante filtros, paginación y visualización de detalles. | Implementada |
| **HU-04** | Registrar docentes revisores, asignarlos a preguntas pendientes y enviar notificaciones por correo electrónico. | Implementada |

### HU-01. Registro de preguntas

Permite al autor registrar preguntas de selección múltiple con única respuesta.

Cada pregunta contiene:

- Contexto y enunciado.
- Cuatro distractores y respuesta correcta.
- Justificación de la respuesta.
- Bibliografía.
- Competencia, tema y subtema.
- Nivel de dificultad.

El sistema valida los campos obligatorios y la estructura de las preguntas antes de almacenarlas.

### HU-02. Envío de preguntas a revisión

Permite al autor cambiar el estado de una pregunta de `BORRADOR` a `PENDIENTE_REVISION`.

El cambio de estado está sujeto a las reglas de negocio y a la autorización del autor correspondiente.

### HU-03. Consulta de preguntas

Permite consultar las preguntas registradas por el autor mediante:

- Listado de preguntas.
- Filtros por estado y dificultad.
- Paginación de resultados.
- Visualización de los detalles de cada pregunta.
- Identificación visual de los estados.

### HU-04. Asignación de revisores

Permite al administrador gestionar el proceso de asignación de docentes revisores.

Sus principales funciones son:

- Registrar docentes revisores.
- Consultar las preguntas pendientes de revisión.
- Asignar uno o varios docentes a una pregunta.
- Evitar asignaciones duplicadas.
- Consultar los docentes asignados.
- Enviar notificaciones por correo electrónico.
- Registrar los resultados de los envíos.
- Reintentar notificaciones que no pudieron enviarse.

La asignación se almacena en SQLite y se conserva incluso si ocurre un problema durante el envío del correo.

---

## 3. Tecnologías utilizadas

| Tecnología | Utilización |
|---|---|
| **Java 21** | Lenguaje principal de desarrollo. |
| **Java Swing** | Construcción de la interfaz gráfica. |
| **Apache Maven** | Gestión de dependencias y compilación. |
| **SQLite** | Almacenamiento persistente de información. |
| **Jakarta Mail** | Implementación del envío de correos SMTP. |
| **Eclipse Angus Mail** | Implementación del proveedor de correo. |
| **JUnit 5** | Pruebas unitarias automatizadas. |
| **Git y GitHub** | Control de versiones y colaboración. |
| **Visual Studio Code** | Entorno de desarrollo compatible con el proyecto. |

## 4. Arquitectura del sistema

El proyecto utiliza una **arquitectura monolítica de tres capas**, complementada con el patrón MVC.

### Capa de presentación

Contiene las interfaces Java Swing y los controladores responsables de gestionar la interacción entre los usuarios y el sistema.

### Capa de dominio

Contiene las entidades, interfaces de servicios, validaciones, reglas de negocio y casos de uso.

### Capa de infraestructura

Contiene las implementaciones de los repositorios SQLite y los componentes responsables de las notificaciones por correo electrónico.

### Estructura principal

```text
src/
├── main/
│   └── java/
│       └── co/edu/unicauca/saberpro/
│           ├── Main.java
│           │
│           ├── presentation/
│           │   ├── view/
│           │   └── controller/
│           │
│           ├── domain/
│           │   ├── model/
│           │   ├── service/
│           │   ├── repository/
│           │   └── validation/
│           │
│           └── infrastructure/
│               ├── persistence/
│               └── notification/
│
└── test/
    └── java/
```

Esta separación favorece la mantenibilidad, la modificabilidad y las pruebas de los diferentes componentes.

Se utilizan interfaces para desacoplar los servicios de sus implementaciones. En HU-04, el componente `NotificadorRevisionConRegistro` aplica el patrón Decorator para registrar los resultados de las notificaciones sin modificar la responsabilidad principal del notificador SMTP.

---

## 5. Instalación y ejecución

### Requisitos

Para ejecutar el proyecto es necesario contar con:

- **JDK 21**.
- **Apache Maven**.
- **Git**.
- **Visual Studio Code** con extensiones Java, o un IDE compatible con Maven.
- Un entorno gráfico para visualizar las ventanas Java Swing.

No es necesario instalar un servidor de base de datos, ya que SQLite utiliza un archivo local.

### Paso 1. Clonar el repositorio

```bash
git clone https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026.git
```

Ingresar a la carpeta del proyecto:

```bash
cd banco-preguntas-saber-pro-2026
```

### Paso 2. Abrir el proyecto

Abrir la carpeta en Visual Studio Code y esperar a que Maven reconozca las dependencias definidas en `pom.xml`.

### Paso 3. Verificar Java y Maven

```bash
java -version
```

```bash
mvn -version
```

### Paso 4. Compilar el proyecto

```bash
mvn clean compile
```

### Paso 5. Ejecutar las pruebas

```bash
mvn clean test
```

### Paso 6. Iniciar la aplicación

Desde la carpeta principal del proyecto, ejecutar:

```bash
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
```

Se abrirá una ventana para seleccionar el perfil demostrativo:

**Autor (HU-01 a HU-03):** permite registrar preguntas, enviarlas a revisión y consultar las preguntas propias.

**Administrador (HU-04):** permite registrar docentes revisores, asignarlos a preguntas y gestionar las notificaciones.

**Nota:** la selección de perfiles es una implementación académica demostrativa. Actualmente no existe un sistema de inicio de sesión con autenticación real.

---

## 6. Base de datos

El sistema utiliza SQLite para almacenar la información de manera persistente.

Al ejecutar la aplicación, se crea automáticamente la base de datos local:

```text
data/banco_preguntas.db
```

En ella se almacena información relacionada con preguntas, docentes revisores, asignaciones y notificaciones.

La carpeta `data/` está excluida del control de versiones mediante `.gitignore`.

**Importante:** al descargar el proyecto desde GitHub, cada usuario comienza con su propia base de datos. Los registros creados en otro computador no se transfieren automáticamente.

Para comprobar HU-04 desde una instalación nueva, primero deben registrarse preguntas, cambiarlas a estado pendiente de revisión y crear los docentes revisores.

---

## 7. Notificaciones por correo electrónico — HU-04

El sistema implementa notificaciones mediante **SMTP con autenticación y cifrado STARTTLS**.

Cuando el administrador asigna un docente revisor a una pregunta pendiente, el sistema intenta enviar un correo electrónico a la dirección registrada para ese docente.

El mensaje incluye:

- Nombre del docente revisor.
- Identificador de la pregunta.
- Enunciado de la pregunta.
- Información sobre la asignación para revisión académica.

El asunto utilizado es:

**Nueva pregunta asignada para revisión**

### 7.1. Estado actual del envío de correos

El envío de correos electrónicos **está implementado y fue probado exitosamente**.

Durante la verificación funcional se obtuvo:

| Resultado | Cantidad |
|---|---:|
| Notificaciones procesadas | 1 |
| Notificaciones enviadas | 1 |
| Notificaciones fallidas | 0 |

También se comprobó que el mensaje llegó correctamente a la bandeja de entrada del destinatario.

Esta verificación demuestra el funcionamiento del envío SMTP con una configuración válida. No significa que las credenciales utilizadas estén incluidas en el proyecto.

### 7.2. Configuración SMTP

Por razones de seguridad, **el repositorio no contiene contraseñas ni credenciales privadas de correo electrónico**.

Cada persona que desee probar el envío deberá configurar sus propias credenciales SMTP mediante variables de entorno.

| Variable | Descripción |
|---|---|
| `SMTP_HOST` | Dirección del servidor SMTP. |
| `SMTP_PORT` | Puerto del servidor. |
| `SMTP_USER` | Cuenta utilizada para autenticar el envío. |
| `SMTP_PASSWORD` | Contraseña SMTP o contraseña de aplicación. |
| `SMTP_FROM` | Dirección del correo remitente. |

Para Gmail se utiliza:

```text
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
```

La cuenta remitente debe permitir el uso de SMTP y contar con una contraseña de aplicación válida.

**No debe utilizarse la contraseña normal de Gmail como sustituto de una contraseña de aplicación.**

### 7.3. Configuración SMTP en Windows

Desde una terminal **PowerShell** de Visual Studio Code:

```powershell
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"

$env:SMTP_USER = Read-Host "Correo Gmail remitente"
$env:SMTP_FROM = $env:SMTP_USER

$clave = Read-Host "Contraseña de aplicación" -AsSecureString

$env:SMTP_PASSWORD = [System.Net.NetworkCredential]::new("", $clave).Password
```

Después, desde esa misma terminal, iniciar el programa:

```powershell
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
```

Las variables configuradas se mantienen en esa sesión de terminal y no se guardan en GitHub.

### 7.4. Configuración SMTP en Linux o Google Cloud Shell

Ejecutar:

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

La contraseña se introduce de manera oculta.

Después ejecutar el programa desde la misma terminal:

```bash
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
```

**Nota sobre Google Cloud Shell:** para mostrar las ventanas Swing se necesita un escritorio gráfico. Durante las pruebas se utilizó un entorno virtual con Xvfb y noVNC. En un computador con escritorio gráfico, como Windows, esto no es necesario.

### 7.5. ¿Qué ocurre si el correo no se envía?

El sistema contempla fallos de autenticación, problemas de conexión o ausencia de configuración SMTP.

En estos casos:

1. La asignación del docente permanece guardada en SQLite.
2. La notificación conserva el estado `PENDIENTE`.
3. El administrador puede corregir la configuración SMTP.
4. Posteriormente puede seleccionar **Reintentar notificaciones**.
5. Si el envío se completa y se registra correctamente, la notificación cambia al estado `ENVIADA`.

El sistema también evita crear una segunda asignación cuando el mismo docente ya se encuentra asignado a esa pregunta.

Que el servidor SMTP acepte un mensaje no garantiza su recepción en la bandeja principal. Si es necesario, debe comprobarse también la carpeta de correo no deseado.

### 7.6. Seguridad

Las credenciales SMTP se gestionan mediante variables de entorno y no se almacenan dentro del código Java.

Por seguridad:

- No deben publicarse contraseñas en GitHub.
- No deben compartirse contraseñas de aplicación entre integrantes o evaluadores.
- No deben introducirse credenciales directamente en `Main.java` o `pom.xml`.
- Cada persona debe utilizar una cuenta o proveedor SMTP autorizado.

Los archivos `.env` están excluidos mediante `.gitignore`, pero la aplicación actual **no carga automáticamente un archivo `.env`**: lee las variables del entorno de ejecución.

---

## 8. Pruebas unitarias

Se implementaron pruebas automatizadas utilizando **JUnit 5**.

Estas pruebas verifican el comportamiento de los componentes principales, incluyendo:

- Entidades y validaciones.
- Registro y consulta de preguntas.
- Cambios de estado.
- Servicios y reglas de negocio.
- Autorización según el rol demostrativo.
- Persistencia con SQLite.
- Asignación de revisores.
- Prevención de duplicados.
- Controladores.
- Registro y reintento de notificaciones.

Para ejecutar las pruebas:

```bash
mvn clean test
```

### Último resultado verificado

| Indicador | Resultado |
|---|---:|
| Pruebas ejecutadas | **180** |
| Fallos | **0** |
| Errores | **0** |
| Pruebas omitidas | **0** |
| Compilación | **BUILD SUCCESS** |

Las pruebas unitarias pueden ejecutarse sin utilizar credenciales personales de Gmail.

El envío real de correo se comprobó adicionalmente mediante una prueba funcional manual.

---

## 9. Estado actual y aspectos pendientes

### Funcionalidades completadas

Se encuentran implementadas e integradas en la rama principal `main`:

- HU-01: registro y validación de preguntas.
- HU-02: transición de preguntas a pendiente de revisión.
- HU-03: consulta con filtros y paginación.
- HU-04: registro y asignación de revisores.
- Persistencia local mediante SQLite.
- Notificaciones SMTP y reintentos.
- Pruebas automatizadas con JUnit 5.

### Aspectos pendientes de la entrega académica

Aunque las cuatro historias de usuario están implementadas, deben verificarse los siguientes entregables y actividades:

- **Documentación de arquitectura:** incorporar o enlazar el documento con los diagramas y decisiones técnicas exigidos por la guía del curso.
- **Sustentación:** preparar el video con la participación de los integrantes del equipo.
- **Verificación de instalación:** comprobar que el proyecto se ejecuta correctamente desde una copia limpia del repositorio en otro computador.
- **Configuración de correo en otros equipos:** cada evaluador o integrante que desee enviar notificaciones reales deberá configurar sus propias credenciales SMTP.

### Limitaciones conocidas

**Perfiles demostrativos:** actualmente se permite seleccionar el perfil Autor o Administrador para realizar las pruebas académicas. No se ha implementado autenticación real de usuarios.

**Base de datos local:** SQLite almacena los datos en cada equipo. La aplicación no dispone de una base de datos compartida entre diferentes computadores.

**Notificaciones externas:** el envío de correos depende de que exista una configuración SMTP válida y de que el proveedor de correo permita la conexión.

Estas limitaciones no impiden utilizar las funcionalidades implementadas, pero deben considerarse al evaluar el alcance del sistema.

---

## 10. Control de versiones y colaboración

El proyecto se administra mediante Git y GitHub.

La rama `main` contiene los avances integrados y verificados.

El desarrollo de las historias de usuario se realizó mediante ramas de trabajo:

| Rama | Propósito |
|---|---|
| `main` | Versión principal del proyecto. |
| `feature/hu-01` | Registro de preguntas. |
| `feature/hu-02` | Envío de preguntas a revisión. |
| `feature/hu-03` | Consulta de preguntas. |
| `feature/hu-04` | Asignación de revisores y notificaciones. |

Los cambios se integran mediante **Pull Requests**, manteniendo organizado el historial del repositorio y evitando modificaciones directas sobre la rama principal protegida.

### Repositorio oficial

https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026

---

**Proyecto académico — Universidad del Cauca**  
Ingeniería de Software II · Periodo 2026.2
