Banco de Preguntas Saber Pro
Universidad del Cauca
Programa: Ingeniería de Sistemas
Asignatura: Ingeniería de Software II
Periodo académico: 2026.2
Tipo de aplicación: Escritorio Java (Swing)
Repositorio: https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026
1. Descripción
Aplicación académica de escritorio para registrar, validar, consultar y gestionar preguntas de selección múltiple con única respuesta, como parte de un Banco de Preguntas Saber Pro. Incluye el envío de preguntas a revisión, la asignación de docentes revisores y la notificación por correo electrónico.
La implementación corresponde a las cuatro historias de usuario solicitadas para el primer corte. La aplicación utiliza una arquitectura monolítica de tres capas y el patrón MVC en la interfaz gráfica.
2. Funcionalidades implementadas
Historia	Funcionalidad	Estado
HU-01	Registrar preguntas con contexto, enunciado, cuatro distractores, respuesta correcta, justificación, bibliografía, competencia, tema, subtema y dificultad; aplicar validaciones.	Implementada
HU-02	Cambiar preguntas propias de BORRADOR a PENDIENTE_REVISION.	Implementada
HU-03	Consultar las preguntas del autor con filtros por estado y dificultad, paginación y visualización de detalles.	Implementada
HU-04	Registrar docentes revisores, consultar preguntas pendientes, asignar revisores, impedir asignaciones duplicadas, consultar asignados y gestionar notificaciones por correo.	Implementada


Pruebas realizadas: ejecución de mvn clean test con 180 pruebas exitosas, 0 fallos y 0 errores. También se realizó una prueba manual de envío SMTP: 1 notificación enviada, 0 fallidas, y se confirmó su recepción en la bandeja del destinatario. Esto describe la prueba realizada; cada persona debe configurar sus propias credenciales SMTP para reproducirla.
3. Tecnologías y arquitectura
- Java 21: lenguaje y versión de compilación.
- Java Swing: interfaz de escritorio.
- Apache Maven: gestión del proyecto y dependencias.
- SQLite (sqlite-jdbc): almacenamiento local de preguntas, revisores, asignaciones y estados de notificación.
- Jakarta Mail + Eclipse Angus Mail: envío de correo mediante SMTP y STARTTLS.
- JUnit 5: pruebas automatizadas.
- Git y GitHub: control de versiones y Pull Requests.
Arquitectura monolítica de tres capas:
src/
├── main/java/co/edu/unicauca/saberpro/
│   ├── Main.java
│   ├── presentation/
│   │   ├── view/             # Ventanas Java Swing
│   │   └── controller/       # Controladores MVC
│   ├── domain/
│   │   ├── model/            # Entidades y estados
│   │   ├── service/          # Casos de uso y reglas de negocio
│   │   ├── repository/       # Interfaces de persistencia
│   │   └── validation/       # Validadores
│   └── infrastructure/
│       ├── persistence/      # Repositorios SQLite
│       └── notification/     # Notificador SMTP y registro de envíos
└── test/java/               # Pruebas JUnit 5
La solución separa la interfaz gráfica, la lógica de negocio y la infraestructura mediante interfaces y servicios, aplicando principios SOLID. En las notificaciones se utiliza un decorador (NotificadorRevisionConRegistro) para registrar el resultado del envío sin mezclarlo con la implementación SMTP.
4. Requisitos para ejecutar
- JDK 21 instalado y disponible desde la terminal (java -version).
- Apache Maven instalado (mvn -version).
- Git para clonar el repositorio.
- Visual Studio Code con extensiones Java, o cualquier IDE compatible con Maven.
- Entorno gráfico de escritorio para mostrar Java Swing.
- Conexión a Internet la primera vez para descargar las dependencias Maven; también para enviar correos SMTP.
No se necesita instalar un servidor de base de datos: SQLite utiliza un archivo local.
5. Descargar, probar y ejecutar desde Visual Studio Code
1. Abrir una terminal y clonar el repositorio:
   git clone https://github.com/britneybenitez-gif/banco-preguntas-saber-pro-2026.git
   cd banco-preguntas-saber-pro-2026
2. Abrir esa carpeta en Visual Studio Code y esperar a que Java/Maven reconozcan el proyecto (pom.xml).
3. Verificar las herramientas:
   java -version
   mvn -version
4. Ejecutar todas las pruebas:
   mvn clean test
5. Iniciar la interfaz gráfica (desde la carpeta que contiene pom.xml):
   mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
Nota: se recomienda ejecutar con Maven para incluir las dependencias de SQLite y Jakarta Mail. Ejecutar únicamente java -cp target/classes ... no incluye automáticamente estas bibliotecas.
Al iniciar, aparece un selector de perfiles demostrativos:
- Autor (HU-01 a HU-03): registrar preguntas, enviarlas a revisión y consultar las propias.
- Administrador (HU-04): registrar revisores, asignarlos a preguntas pendientes y gestionar notificaciones.
El selector de perfiles es una simulación académica; no constituye un sistema real de inicio de sesión.
6. Guía de prueba funcional
Flujo del autor: HU-01, HU-02 y HU-03
1. Iniciar la aplicación y seleccionar Autor.
2. Completar el formulario de registro de una pregunta y guardarla.
3. En el menú Preguntas, abrir Enviar a revisión (HU-02), buscar la pregunta por su identificador y pasarla a PENDIENTE_REVISION.
4. Abrir Mis preguntas (HU-03) para consultar filtros, paginación y detalles.
Flujo del administrador: HU-04
1. Reiniciar la aplicación y seleccionar Administrador.
2. Abrir Gestión de revisores > Registrar docente revisor (HU-04), completar los datos y guardar.
3. Seleccionar una pregunta en estado PENDIENTE_REVISION.
4. Elegir un docente del selector y pulsar Asignar revisor.
5. Utilizar Ver asignados para consultar las asignaciones. La misma pareja pregunta-revisor no puede registrarse dos veces.
6. Si hubo un problema con SMTP, configurar el correo, reiniciar la aplicación y utilizar Reintentar notificaciones.
7. Base de datos SQLite
La aplicación crea automáticamente la carpeta y el archivo de base de datos en la primera ejecución:
data/banco_preguntas.db
Los datos se almacenan localmente y permanecen entre ejecuciones en el mismo equipo, si no se borra el archivo. La ruta data/ está excluida de Git mediante .gitignore.
Importante para evaluadores: al clonar el repositorio se comienza con una base de datos nueva. Primero se deben crear preguntas, enviarlas a revisión y registrar revisores antes de poder asignarlos. Los datos creados en el computador de otro integrante no se descargan desde GitHub.
8. Notificaciones por correo electrónico (HU-04)
La aplicación envía un correo cuando un administrador registra una nueva asignación de un docente revisor a una pregunta pendiente. El asunto utilizado es «Nueva pregunta asignada para revisión» y el mensaje incluye el identificador y el enunciado de la pregunta.
El correo se envía al email registrado para el docente revisor, que puede ser diferente del correo remitente. La aplicación utiliza SMTP con STARTTLS obligatorio y autenticación. Los datos de acceso se reciben mediante variables de entorno y no están almacenados en el repositorio.
8.1 Variables necesarias
Variable	Descripción	Ejemplo para Gmail
SMTP_HOST	Servidor de correo saliente	smtp.gmail.com
SMTP_PORT	Puerto SMTP; por defecto 587	587
SMTP_USER	Correo o usuario que autentica el envío	remitente@gmail.com
SMTP_PASSWORD	Credencial SMTP o contraseña de aplicación	No publicar
SMTP_FROM	Dirección que aparece como remitente; por defecto SMTP_USER	remitente@gmail.com


Para Gmail, se requiere una contraseña de aplicación si la cuenta permite esta opción; no se utiliza la contraseña normal de acceso a Google. Cada usuario que necesite enviar correos debe obtener sus propias credenciales válidas o usar otro proveedor SMTP compatible. La capacidad de envío depende también de la conectividad y las políticas del proveedor.
8.2 Configuración en Windows (terminal PowerShell de VS Code)
Ejecutar en la misma terminal desde la que se iniciará Java:
$env:SMTP_HOST = "smtp.gmail.com"
$env:SMTP_PORT = "587"
$env:SMTP_USER = Read-Host "Correo Gmail remitente"
$env:SMTP_FROM = $env:SMTP_USER
$clave = Read-Host "Contrasena de aplicacion (no se mostrara)" -AsSecureString
$env:SMTP_PASSWORD = [System.Net.NetworkCredential]::new("", $clave).Password
Después ejecutar:
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
Estas variables pertenecen a esa sesión de terminal. Si se abre una terminal nueva, se deberán configurar nuevamente. Aunque la lectura es oculta, durante la ejecución SMTP_PASSWORD existe como variable de entorno del proceso y debe tratarse como un secreto.
8.3 Configuración en Linux o Google Cloud Shell (Bash)
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"
read -rp "Correo Gmail remitente: " SMTP_USER
export SMTP_USER
export SMTP_FROM="$SMTP_USER"
read -rsp "Contraseña de aplicación: " SMTP_PASSWORD
echo
SMTP_PASSWORD="${SMTP_PASSWORD//[[:space:]]/}"
export SMTP_PASSWORD
Para comprobar que se introdujeron 16 caracteres sin imprimir la contraseña:
echo "Longitud de la contraseña: ${#SMTP_PASSWORD}"
Después iniciar Java desde esa misma terminal:
mvn exec:java -Dexec.mainClass=co.edu.unicauca.saberpro.Main
Google Cloud Shell no incluye un escritorio gráfico por defecto. La aplicación Swing requiere un entorno gráfico (por ejemplo, un escritorio virtual con Xvfb y noVNC). En Windows o Linux con escritorio no es necesario utilizar noVNC.
8.4 Comportamiento ante fallos de envío
- SMTP configurado y envío correcto: la aplicación registra el resultado como ENVIADA en SQLite.
- SMTP sin configurar, credenciales inválidas o problemas de conexión: la asignación permanece guardada y la notificación queda PENDIENTE.
- Reintentos: el administrador puede pulsar Reintentar notificaciones después de corregir la configuración. La pantalla muestra notificaciones procesadas, enviadas y fallidas.
- Evitar duplicados: repetir la misma asignación no genera una nueva asignación ni otro correo por esa operación. Los reintentos de notificaciones pendientes son un mecanismo independiente.
Que el servicio SMTP acepte un mensaje no garantiza que termine en la bandeja de entrada; comprobar también Spam y la dirección del destinatario.
8.5 Seguridad de las credenciales
- No subir contraseñas, tokens, contraseñas de aplicación o claves SMTP a GitHub.
- No escribir credenciales dentro de Main.java, pom.xml ni README.md.
- No compartir cuentas personales con docentes, compañeros o evaluadores.
- Usar credenciales propias para cada equipo y mantenerlas fuera del control de versiones.
- Los archivos .env y la carpeta data/ están excluidos por .gitignore; la aplicación actual no carga automáticamente un .env: utiliza System.getenv(...).
9. Pruebas automatizadas
Desde la raíz del proyecto:
mvn clean test
La última verificación funcional reportó 180 pruebas, sin fallos ni errores. Las pruebas comprueban entidades, validaciones, servicios, autorización por rol demostrativo, persistencia, controladores y seguimiento de notificaciones. El envío SMTP real se verificó manualmente; no depende de credenciales personales durante las pruebas unitarias.
10. Estado del proyecto y pendientes
Implementado para el alcance funcional del primer corte: HU-01, HU-02, HU-03 y HU-04; interfaz Swing, SQLite, validaciones, asignación de revisores, correo SMTP con reintentos y pruebas unitarias.
Aspectos pendientes de documentar o verificar para la entrega académica:
- Incorporar o enlazar el documento de arquitectura con los diagramas y decisiones técnicas exigidos por la guía, si aún no se ha entregado.
- Preparar el video de sustentación con la participación del equipo, conforme al protocolo del curso.
- Comprobar la instalación y ejecución desde una copia limpia del repositorio en otro computador y documentar cualquier incidencia.
- Configurar una cuenta o proveedor SMTP propio en el equipo del evaluador si desea verificar el envío real. No se distribuyen credenciales con el proyecto.
Limitaciones actuales: la selección de perfiles es demostrativa, sin autenticación real de usuarios; la información se almacena en SQLite local, no en una base de datos compartida entre equipos. No se incluye una funcionalidad adicional de HU-05 en el alcance de estas cuatro historias.
11. Colaboración y control de versiones
La rama main contiene los cambios integrados. El equipo desarrolla ajustes en ramas de trabajo, realiza commits y utiliza Pull Requests para integrar a main; no se recomienda trabajar directamente sobre la rama principal.
Proyecto académico — Universidad del Cauca, Ingeniería de Software II, periodo 2026.2.
