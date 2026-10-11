# Banco de Preguntas Saber Pro

**Universidad del Cauca**  
**Programa:** Ingeniería de Sistemas  
**Asignatura:** Ingeniería de Software II  
**Periodo académico:** 2026.2

## 1. Descripción del proyecto

El Banco de Preguntas Saber Pro es un proyecto académico que tiene como objetivo desarrollar una aplicación de escritorio en Java para gestionar preguntas de selección múltiple con única respuesta.

El sistema permitirá registrar, validar, consultar y administrar preguntas, además de gestionar su proceso de revisión mediante diferentes roles de usuario.

## 2. Tecnologías

- Java 21
- Java Swing para la interfaz gráfica
- Apache Maven para la gestión de dependencias
- JUnit 5 para las pruebas unitarias
- Git y GitHub para el control de versiones

## 3. Arquitectura del software

El proyecto está diseñado bajo una arquitectura monolítica de tres capas:

- **Presentación:** interfaces gráficas y controladores mediante MVC.
- **Dominio:** entidades, servicios, validaciones y reglas de negocio.
- **Infraestructura:** implementación de persistencia y notificaciones.

Se aplicarán principios SOLID y patrones de diseño GoF para favorecer la modificabilidad y mantenibilidad del sistema.

## 4. Historias de usuario previstas

| Historia | Descripción |
|---|---|
| HU-01 | Registrar preguntas con validación estructural. |
| HU-02 | Cambiar preguntas de borrador a pendiente de revisión. |
| HU-03 | Listar preguntas del autor mediante filtros y paginación. |
| HU-04 | Asignar revisores y enviar notificaciones por correo electrónico. |

## 5. Estructura del proyecto

```text
src/
├── main/
│   ├── java/co/edu/unicauca/saberpro/
│   │   ├── Main.java
│   │   ├── presentation/
│   │   │   ├── view/
│   │   │   └── controller/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   └── validation/
│   │   └── infrastructure/
│   │       ├── persistence/
│   │       └── notification/
│   └── resources/
└── test/
    └── java/
```

## 6. Compilación y ejecución

**Requisitos:** JDK 21 y Apache Maven.

Para compilar el proyecto:

```bash
mvn clean compile
```

Para ejecutar la clase principal inicial:

```bash
java -cp target/classes co.edu.unicauca.saberpro.Main
```

Para ejecutar las pruebas automatizadas, cuando se incorporen:

```bash
mvn test
```

## 7. Organización del repositorio

- `main`: estructura principal y versiones estables del proyecto.
- `feature/hu-01`: desarrollo de HU-01.
- `feature/hu-02`: desarrollo de HU-02.
- `feature/hu-03`: desarrollo de HU-03.
- `feature/hu-04`: desarrollo de HU-04.

Las funcionalidades serán integradas mediante Pull Requests, después de su revisión y verificación.

## 8. Estado actual

**Proyecto en desarrollo — configuración inicial.**

Actualmente se dispone de la estructura de paquetes, la configuración Maven y una clase principal ejecutable en consola.

Las interfaces gráficas, funcionalidades de las historias de usuario, persistencia y pruebas unitarias se implementarán durante el desarrollo del primer corte académico.
