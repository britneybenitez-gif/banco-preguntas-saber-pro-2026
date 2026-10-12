
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.NotificacionPendiente;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del repositorio SQLite de notificaciones de revisión.
 */
class SqliteNotificacionRevisionRepositoryTest {

    @TempDir
    Path directorioTemporal;

    private Path rutaBaseDatos;

    private SqlitePreguntaRepository repositorioPreguntas;

    private SqliteAsignacionRevisionRepository
            repositorioAsignaciones;

    private SqliteNotificacionRevisionRepository
            repositorioNotificaciones;

    /**
     * Prepara una base de datos independiente para cada prueba.
     */
    @BeforeEach
    void preparar() {

        rutaBaseDatos = directorioTemporal.resolve(
                "hu04-notificaciones.db"
        );

        // Primero se crean las tablas de preguntas.
        repositorioPreguntas =
                new SqlitePreguntaRepository(rutaBaseDatos);

        // Después, las tablas de asignaciones y notificaciones.
        repositorioAsignaciones =
                new SqliteAsignacionRevisionRepository(
                        rutaBaseDatos
                );

        repositorioNotificaciones =
                new SqliteNotificacionRevisionRepository(
                        rutaBaseDatos
                );
    }

    /**
     * Prueba 1: sin asignaciones no existen notificaciones pendientes.
     */
    @Test
    void debeRetornarListaVaciaSinNotificaciones() {

        List<NotificacionPendiente> pendientes =
                repositorioNotificaciones.listarPendientes(10);

        assertTrue(pendientes.isEmpty());
    }

    /**
     * Prueba 2: una asignación genera una notificación pendiente.
     */
    @Test
    void debeListarNotificacionPendiente() {

        String preguntaId = registrarAsignacion("REV001");

        List<NotificacionPendiente> pendientes =
                repositorioNotificaciones.listarPendientes(10);

        assertEquals(1, pendientes.size());

        NotificacionPendiente notificacion =
                pendientes.get(0);

        assertEquals(
                preguntaId,
                notificacion.preguntaId()
        );

        assertEquals(
                "REV001",
                notificacion.revisorId()
        );

        assertEquals(
                0,
                notificacion.intentos()
        );
    }

    /**
     * Prueba 3: una notificación enviada deja de estar pendiente.
     */
    @Test
    void debeMarcarNotificacionComoEnviada() {

        String preguntaId = registrarAsignacion("REV001");

        boolean actualizada =
                repositorioNotificaciones.marcarEnviada(
                        preguntaId,
                        "REV001"
                );

        assertTrue(actualizada);

        assertTrue(
                repositorioNotificaciones
                        .listarPendientes(10)
                        .isEmpty()
        );

        // No debe volver a marcarse como enviada.
        assertFalse(
                repositorioNotificaciones.marcarEnviada(
                        preguntaId,
                        "REV001"
                )
        );
    }

    /**
     * Prueba 4: un fallo mantiene la notificación pendiente
     * y aumenta su contador de intentos.
     */
    @Test
    void debeRegistrarFalloYAumentarIntentos() {

        String preguntaId = registrarAsignacion("REV001");

        assertTrue(
                repositorioNotificaciones.registrarFallo(
                        preguntaId,
                        "REV001",
                        "Servidor SMTP no disponible"
                )
        );

        List<NotificacionPendiente> pendientes =
                repositorioNotificaciones.listarPendientes(10);

        assertEquals(1, pendientes.size());
        assertEquals(1, pendientes.get(0).intentos());

        // Simulamos un segundo fallo.
        assertTrue(
                repositorioNotificaciones.registrarFallo(
                        preguntaId,
                        "REV001",
                        "Tiempo de espera agotado"
                )
        );

        pendientes =
                repositorioNotificaciones.listarPendientes(10);

        assertEquals(1, pendientes.size());
        assertEquals(2, pendientes.get(0).intentos());
    }

    /**
     * Prueba 5: un envío exitoso después de un fallo
     * retira la notificación de la lista de pendientes.
     */
    @Test
    void debeEnviarNotificacionDespuesDeUnFallo() {

        String preguntaId = registrarAsignacion("REV001");

        assertTrue(
                repositorioNotificaciones.registrarFallo(
                        preguntaId,
                        "REV001",
                        "Error temporal SMTP"
                )
        );

        assertEquals(
                1,
                repositorioNotificaciones
                        .listarPendientes(10)
                        .get(0)
                        .intentos()
        );

        assertTrue(
                repositorioNotificaciones.marcarEnviada(
                        preguntaId,
                        "REV001"
                )
        );

        assertTrue(
                repositorioNotificaciones
                        .listarPendientes(10)
                        .isEmpty()
        );

        // Una notificación enviada no acepta nuevos fallos.
        assertFalse(
                repositorioNotificaciones.registrarFallo(
                        preguntaId,
                        "REV001",
                        "Otro error"
                )
        );
    }

    /**
     * Prueba 6: la consulta respeta el límite solicitado.
     */
    @Test
    void debeRespetarLimiteDeNotificaciones() {

        registrarAsignacion("REV001");
        registrarAsignacion("REV002");

        List<NotificacionPendiente> primeraConsulta =
                repositorioNotificaciones.listarPendientes(1);

        assertEquals(1, primeraConsulta.size());

        List<NotificacionPendiente> segundaConsulta =
                repositorioNotificaciones.listarPendientes(10);

        assertEquals(2, segundaConsulta.size());
    }

    /**
     * Prueba 7: no permite límites de consulta inválidos.
     */
    @Test
    void debeRechazarLimitesInvalidos() {

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones
                        .listarPendientes(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones
                        .listarPendientes(-1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones
                        .listarPendientes(1001)
        );
    }

    /**
     * Prueba 8: valida los identificadores de las operaciones.
     */
    @Test
    void debeRechazarIdentificadoresInvalidos() {

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones.marcarEnviada(
                        null,
                        "REV001"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones.marcarEnviada(
                        "PREG001",
                        " "
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones.registrarFallo(
                        "",
                        "REV001",
                        "Error de prueba"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones.registrarFallo(
                        "PREG001",
                        null,
                        "Error de prueba"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorioNotificaciones.registrarFallo(
                        "PREG001",
                        "REV001",
                        " "
                )
        );
    }

    /**
     * Prueba 9: no actualiza notificaciones inexistentes.
     */
    @Test
    void noDebeActualizarNotificacionInexistente() {

        assertFalse(
                repositorioNotificaciones.marcarEnviada(
                        "PREGUNTA_INEXISTENTE",
                        "REV999"
                )
        );

        assertFalse(
                repositorioNotificaciones.registrarFallo(
                        "PREGUNTA_INEXISTENTE",
                        "REV999",
                        "Error de prueba"
                )
        );

        assertTrue(
                repositorioNotificaciones
                        .listarPendientes(10)
                        .isEmpty()
        );
    }

    /**
     * Prueba 10: las notificaciones permanecen guardadas
     * cuando se crea una nueva instancia del repositorio.
     */
    @Test
    void debeConservarNotificacionesEnSQLite() {

        String preguntaId = registrarAsignacion("REV001");

        assertTrue(
                repositorioNotificaciones.registrarFallo(
                        preguntaId,
                        "REV001",
                        "Fallo temporal"
                )
        );

        SqliteNotificacionRevisionRepository nuevoRepositorio =
                new SqliteNotificacionRevisionRepository(
                        rutaBaseDatos
                );

        List<NotificacionPendiente> pendientes =
                nuevoRepositorio.listarPendientes(10);

        assertEquals(1, pendientes.size());

        assertEquals(
                preguntaId,
                pendientes.get(0).preguntaId()
        );

        assertEquals(
                "REV001",
                pendientes.get(0).revisorId()
        );

        assertEquals(
                1,
                pendientes.get(0).intentos()
        );
    }

    /**
     * Registra una pregunta pendiente y le asigna
     * un docente revisor.
     *
     * @param revisorId identificador del revisor
     * @return identificador de la pregunta registrada
     */
    private String registrarAsignacion(String revisorId) {

        Pregunta pregunta = new Pregunta(
                "AUTOR001",
                "Contexto académico de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación académica",
                "Bibliografía de prueba",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );

        repositorioPreguntas.guardar(pregunta);

        boolean actualizada =
                repositorioPreguntas.actualizarEstado(
                        pregunta.getId(),
                        pregunta.getAutorId(),
                        EstadoPregunta.BORRADOR,
                        EstadoPregunta.PENDIENTE_REVISION
                );

        assertTrue(actualizada);

        Revisor revisor = new Revisor(
                revisorId,
                "Docente " + revisorId,
                revisorId.toLowerCase() + "@unicauca.edu.co"
        );

        repositorioAsignaciones.guardarRevisor(revisor);

        AsignacionRevision asignacion =
                new AsignacionRevision(
                        pregunta.getId(),
                        revisorId,
                        "ADMIN001",
                        Instant.parse("2026-10-12T15:00:00Z")
                );

        assertTrue(
                repositorioAsignaciones.guardarAsignacion(
                        asignacion
                )
        );

        return pregunta.getId();
    }
}
