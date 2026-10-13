
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqlitePreguntaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica la integración entre asignaciones SQLite
 * y notificaciones a los revisores.
 */
class AsignacionRevisionNotificacionTest {

    @TempDir
    Path directorioTemporal;

    private SqlitePreguntaRepository repositorioPreguntas;
    private SqliteAsignacionRevisionRepository repositorioAsignaciones;
    private NotificadorFalso notificador;
    private IAsignacionRevisionService servicio;

    /**
     * Prepara una base de datos temporal y un notificador
     * independiente para cada prueba.
     */
    @BeforeEach
    void preparar() {

        Path ruta = directorioTemporal.resolve(
                "hu04-notificaciones.db"
        );

        repositorioPreguntas =
                new SqlitePreguntaRepository(ruta);

        repositorioAsignaciones =
                new SqliteAsignacionRevisionRepository(ruta);

        notificador = new NotificadorFalso();

        servicio = crearServicio(RolUsuario.ADMINISTRADOR);
    }

    /**
     * Verifica que una asignación nueva genere
     * exactamente una notificación.
     */
    @Test
    void asignacionNuevaGeneraNotificacion() {

        Pregunta pregunta = crearPregunta(true);
        registrarRevisor("REV001");

        boolean resultado = servicio.asignarRevisor(
                pregunta.getId(),
                "REV001"
        );

        assertTrue(resultado);
        assertEquals(1, notificador.envios);

        assertEquals(
                "REV001",
                notificador.ultimoRevisorId
        );

        assertEquals(
                pregunta.getId(),
                notificador.ultimaPreguntaId
        );

        assertEquals(
                1,
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .size()
        );
    }

    /**
     * Verifica que una asignación duplicada
     * no genere un segundo correo.
     */
    @Test
    void asignacionDuplicadaNoGeneraOtroCorreo() {

        Pregunta pregunta = crearPregunta(true);
        registrarRevisor("REV001");

        assertTrue(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertFalse(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertEquals(1, notificador.envios);

        assertEquals(
                1,
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .size()
        );
    }

    /**
     * Verifica que no se envíen notificaciones
     * para preguntas que siguen en borrador.
     */
    @Test
    void preguntaEnBorradorNoGeneraCorreo() {

        Pregunta pregunta = crearPregunta(false);
        registrarRevisor("REV001");

        assertThrows(
                IllegalStateException.class,
                () -> servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertEquals(0, notificador.envios);

        assertTrue(
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .isEmpty()
        );
    }

    /**
     * Verifica que un revisor inexistente
     * no pueda recibir una asignación.
     */
    @Test
    void revisorInexistenteNoGeneraCorreo() {

        Pregunta pregunta = crearPregunta(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV999"
                )
        );

        assertEquals(0, notificador.envios);

        assertTrue(
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .isEmpty()
        );
    }

    /**
     * Verifica que los usuarios sin permisos
     * no puedan generar asignaciones ni correos.
     */
    @Test
    void usuarioNoAutorizadoNoGeneraCorreo() {

        Pregunta pregunta = crearPregunta(true);
        registrarRevisor("REV001");

        for (RolUsuario rol : List.of(
                RolUsuario.AUTOR,
                RolUsuario.REVISOR)) {

            IAsignacionRevisionService otroServicio =
                    crearServicio(rol);

            assertThrows(
                    SecurityException.class,
                    () -> otroServicio.asignarRevisor(
                            pregunta.getId(),
                            "REV001"
                    )
            );
        }

        assertEquals(0, notificador.envios);

        assertTrue(
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .isEmpty()
        );
    }

    /**
     * Verifica que un fallo de notificación
     * no se confunda con un fallo al guardar.
     */
    @Test
    void falloDeCorreoConservaAsignacionGuardada() {

        Pregunta pregunta = crearPregunta(true);
        registrarRevisor("REV001");

        notificador.simularFallo = true;

        NotificacionRevisionException error =
                assertThrows(
                        NotificacionRevisionException.class,
                        () -> servicio.asignarRevisor(
                                pregunta.getId(),
                                "REV001"
                        )
                );

        assertTrue(
                error.getMessage().contains(
                        "asignación quedó guardada"
                )
        );

        assertNotNull(error.getCause());

        assertEquals(1, notificador.envios);

        List<AsignacionRevision> asignaciones =
                repositorioAsignaciones.listarAsignaciones(
                        pregunta.getId()
                );

        assertEquals(1, asignaciones.size());
        assertEquals(
                "REV001",
                asignaciones.get(0).revisorId()
        );
    }

    /**
     * Verifica que varios revisores de una pregunta
     * reciban notificaciones individuales.
     */
    @Test
    void variosRevisoresGeneranNotificacionesSeparadas() {

        Pregunta pregunta = crearPregunta(true);

        registrarRevisor("REV001");
        registrarRevisor("REV002");

        assertTrue(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertTrue(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV002"
                )
        );

        assertEquals(2, notificador.envios);

        assertEquals(
                2,
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .size()
        );
    }

    /**
     * Construye un servicio con el rol solicitado.
     *
     * @param rol rol que se desea comprobar
     * @return servicio configurado
     */
    private IAsignacionRevisionService crearServicio(
            RolUsuario rol) {

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN001",
                rol
        );

        return new AsignacionRevisionServiceImpl(
                repositorioAsignaciones,
                repositorioPreguntas,
                sesion,
                notificador
        );
    }

    /**
     * Registra una pregunta en SQLite y opcionalmente
     * la cambia a pendiente de revisión.
     *
     * @param pendiente indica si debe enviarse a revisión
     * @return pregunta registrada
     */
    private Pregunta crearPregunta(boolean pendiente) {

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

        if (pendiente) {

            boolean actualizado =
                    repositorioPreguntas.actualizarEstado(
                            pregunta.getId(),
                            pregunta.getAutorId(),
                            EstadoPregunta.BORRADOR,
                            EstadoPregunta.PENDIENTE_REVISION
                    );

            assertTrue(actualizado);
        }

        return pregunta;
    }

    /**
     * Registra un docente revisor en SQLite.
     *
     * @param id identificador del docente
     */
    private void registrarRevisor(String id) {

        Revisor revisor = new Revisor(
                id,
                "Docente " + id,
                id.toLowerCase() + "@unicauca.edu.co"
        );

        repositorioAsignaciones.guardarRevisor(revisor);
    }

    /**
     * Simula el envío de correos sin utilizar SMTP.
     */
    private static class NotificadorFalso
            implements INotificadorRevision {

        private int envios;
        private boolean simularFallo;

        private String ultimoRevisorId;
        private String ultimaPreguntaId;

        /**
         * Registra el intento de notificación
         * y permite simular errores.
         */
        @Override
        public void notificarAsignacion(
                Revisor revisor,
                Pregunta pregunta) {

            envios++;

            ultimoRevisorId = revisor.id();
            ultimaPreguntaId = pregunta.getId();

            if (simularFallo) {

                throw new IllegalStateException(
                        "Fallo SMTP simulado."
                );
            }
        }
    }
}
