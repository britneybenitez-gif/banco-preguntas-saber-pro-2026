
package co.edu.unicauca.saberpro.infrastructure.notification;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.NotificacionPendiente;
import co.edu.unicauca.saberpro.domain.service.INotificadorRevision;

import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteNotificacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqlitePreguntaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de la integración entre notificaciones y SQLite.
 */
class NotificadorRevisionConRegistroTest {

    @TempDir
    Path directorioTemporal;

    private SqliteNotificacionRevisionRepository repositorio;
    private NotificadorFalso correo;
    private NotificadorRevisionConRegistro notificador;

    private Pregunta pregunta;
    private Revisor revisor;

    /**
     * Prepara una asignación y su notificación pendiente.
     */
    @BeforeEach
    void preparar() {

        Path ruta = directorioTemporal.resolve(
                "notificador-pruebas.db"
        );

        SqlitePreguntaRepository preguntas =
                new SqlitePreguntaRepository(ruta);

        SqliteAsignacionRevisionRepository asignaciones =
                new SqliteAsignacionRevisionRepository(ruta);

        repositorio =
                new SqliteNotificacionRevisionRepository(ruta);

        pregunta = new Pregunta(
                "AUTOR001",
                "Contexto académico de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación de prueba",
                "Bibliografía de prueba",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );

        pregunta.enviarARevision();
        preguntas.guardar(pregunta);

        revisor = new Revisor(
                "REV001",
                "Juan Perez",
                "juan@unicauca.edu.co"
        );

        asignaciones.guardarRevisor(revisor);

        assertTrue(
                asignaciones.guardarAsignacion(
                        new AsignacionRevision(
                                pregunta.getId(),
                                revisor.id(),
                                "ADMIN001",
                                Instant.now()
                        )
                )
        );

        correo = new NotificadorFalso();

        notificador = new NotificadorRevisionConRegistro(
                correo,
                repositorio
        );
    }

    /**
     * Verifica que un envío exitoso cambie el estado a ENVIADA.
     */
    @Test
    void envioExitosoMarcaNotificacionEnviada() {

        notificador.notificarAsignacion(
                revisor,
                pregunta
        );

        assertEquals(1, correo.envios);

        assertTrue(
                repositorio.listarPendientes(10).isEmpty()
        );

        assertFalse(
                repositorio.marcarEnviada(
                        pregunta.getId(),
                        revisor.id()
                )
        );
    }

    /**
     * Verifica que un fallo de correo mantenga
     * la notificación pendiente.
     */
    @Test
    void falloCorreoMantieneNotificacionPendiente() {

        correo.simularFallo = true;

        assertThrows(
                IllegalStateException.class,
                () -> notificador.notificarAsignacion(
                        revisor,
                        pregunta
                )
        );

        List<NotificacionPendiente> pendientes =
                repositorio.listarPendientes(10);

        assertEquals(1, pendientes.size());
        assertEquals(1, pendientes.get(0).intentos());
        assertEquals(1, correo.envios);
    }

    /**
     * Verifica que un segundo envío exitoso
     * permita recuperar un fallo anterior.
     */
    @Test
    void puedeEnviarDespuesDeUnFallo() {

        correo.simularFallo = true;

        assertThrows(
                IllegalStateException.class,
                () -> notificador.notificarAsignacion(
                        revisor,
                        pregunta
                )
        );

        correo.simularFallo = false;

        notificador.notificarAsignacion(
                revisor,
                pregunta
        );

        assertEquals(2, correo.envios);

        assertTrue(
                repositorio.listarPendientes(10).isEmpty()
        );
    }

    /**
     * Verifica que varios fallos incrementen el contador.
     */
    @Test
    void variosFallosIncrementanIntentos() {

        correo.simularFallo = true;

        for (int intento = 0; intento < 2; intento++) {

            assertThrows(
                    IllegalStateException.class,
                    () -> notificador.notificarAsignacion(
                            revisor,
                            pregunta
                    )
            );
        }

        List<NotificacionPendiente> pendientes =
                repositorio.listarPendientes(10);

        assertEquals(1, pendientes.size());
        assertEquals(2, pendientes.get(0).intentos());
    }

    /**
     * Verifica que no se admitan argumentos nulos.
     */
    @Test
    void rechazaDatosNulos() {

        assertThrows(
                NullPointerException.class,
                () -> notificador.notificarAsignacion(
                        null,
                        pregunta
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> notificador.notificarAsignacion(
                        revisor,
                        null
                )
        );

        assertEquals(0, correo.envios);
    }

    /**
     * Simula un servidor de correo sin enviar mensajes reales.
     */
    private static class NotificadorFalso
            implements INotificadorRevision {

        private int envios;
        private boolean simularFallo;

        /**
         * Registra un intento y puede simular un fallo SMTP.
         */
        @Override
        public void notificarAsignacion(
                Revisor revisor,
                Pregunta pregunta) {

            envios++;

            if (simularFallo) {
                throw new IllegalStateException(
                        "Servidor SMTP simulado no disponible."
                );
            }
        }
    }
}
