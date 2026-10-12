
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.NotificacionPendiente;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.infrastructure.notification.NotificadorRevisionConRegistro;

import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteNotificacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqlitePreguntaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración del servicio de reintentos.
 *
 * Utiliza SQLite temporal y un servidor SMTP simulado.
 */
class ReintentoNotificacionesServiceImplTest {

    @TempDir
    Path directorioTemporal;

    private SqlitePreguntaRepository repositorioPreguntas;

    private SqliteAsignacionRevisionRepository
            repositorioAsignaciones;

    private SqliteNotificacionRevisionRepository
            repositorioNotificaciones;

    private CorreoFalso correo;

    private IReintentoNotificacionesService servicio;

    /**
     * Prepara una base de datos y un notificador
     * independiente para cada prueba.
     */
    @BeforeEach
    void preparar() {

        Path ruta = directorioTemporal.resolve(
                "hu04-reintentos.db"
        );

        repositorioPreguntas =
                new SqlitePreguntaRepository(ruta);

        repositorioAsignaciones =
                new SqliteAsignacionRevisionRepository(ruta);

        repositorioNotificaciones =
                new SqliteNotificacionRevisionRepository(ruta);

        correo = new CorreoFalso();

        servicio = crearServicio(RolUsuario.ADMINISTRADOR);
    }

    /**
     * Prueba 1: verifica el resultado cuando
     * no existen notificaciones pendientes.
     */
    @Test
    void sinPendientesDevuelveResultadoVacio() {

        ResultadoReintentos resultado =
                servicio.reintentarPendientes(10);

        assertEquals(0, resultado.procesadas());
        assertEquals(0, resultado.enviadas());
        assertEquals(0, resultado.fallidas());

        assertEquals(0, correo.intentosEnvio);
    }

    /**
     * Prueba 2: verifica que el administrador
     * pueda enviar una notificación pendiente.
     */
    @Test
    void administradorPuedeReintentarNotificacion() {

        registrarAsignacion("REV001");

        ResultadoReintentos resultado =
                servicio.reintentarPendientes(10);

        assertEquals(1, resultado.procesadas());
        assertEquals(1, resultado.enviadas());
        assertEquals(0, resultado.fallidas());

        assertEquals(1, correo.intentosEnvio);

        assertTrue(
                repositorioNotificaciones
                        .listarPendientes(10)
                        .isEmpty()
        );
    }

    /**
     * Prueba 3: verifica que un fallo SMTP
     * conserve la notificación pendiente.
     */
    @Test
    void falloSMTPConservaNotificacionPendiente() {

        registrarAsignacion("REV001");

        correo.revisoresConFallo.add("REV001");

        ResultadoReintentos resultado =
                servicio.reintentarPendientes(10);

        assertEquals(1, resultado.procesadas());
        assertEquals(0, resultado.enviadas());
        assertEquals(1, resultado.fallidas());

        List<NotificacionPendiente> pendientes =
                repositorioNotificaciones.listarPendientes(10);

        assertEquals(1, pendientes.size());
        assertEquals("REV001", pendientes.get(0).revisorId());
        assertEquals(1, pendientes.get(0).intentos());
    }

    /**
     * Prueba 4: verifica que un correo fallido
     * pueda enviarse en un intento posterior.
     */
    @Test
    void puedeRecuperarCorreoFallido() {

        registrarAsignacion("REV001");

        correo.revisoresConFallo.add("REV001");

        ResultadoReintentos primerIntento =
                servicio.reintentarPendientes(10);

        assertEquals(1, primerIntento.fallidas());

        assertEquals(
                1,
                repositorioNotificaciones
                        .listarPendientes(10)
                        .get(0)
                        .intentos()
        );

        // Simulamos que el servidor SMTP vuelve a funcionar.
        correo.revisoresConFallo.clear();

        ResultadoReintentos segundoIntento =
                servicio.reintentarPendientes(10);

        assertEquals(1, segundoIntento.procesadas());
        assertEquals(1, segundoIntento.enviadas());
        assertEquals(0, segundoIntento.fallidas());

        assertEquals(2, correo.intentosEnvio);

        assertTrue(
                repositorioNotificaciones
                        .listarPendientes(10)
                        .isEmpty()
        );
    }

    /**
     * Prueba 5: verifica que un correo fallido
     * no detenga los envíos de otros revisores.
     */
    @Test
    void falloDeUnCorreoNoDetieneLosDemas() {

        registrarAsignacion("REV001");
        registrarAsignacion("REV002");

        correo.revisoresConFallo.add("REV002");

        ResultadoReintentos resultado =
                servicio.reintentarPendientes(10);

        assertEquals(2, resultado.procesadas());
        assertEquals(1, resultado.enviadas());
        assertEquals(1, resultado.fallidas());

        assertEquals(2, correo.intentosEnvio);

        List<NotificacionPendiente> pendientes =
                repositorioNotificaciones.listarPendientes(10);

        assertEquals(1, pendientes.size());

        assertEquals(
                "REV002",
                pendientes.get(0).revisorId()
        );

        assertEquals(
                1,
                pendientes.get(0).intentos()
        );
    }

    /**
     * Prueba 6: verifica que el servicio
     * respete el límite de procesamiento.
     */
    @Test
    void respetaLimiteDeReintentos() {

        registrarAsignacion("REV001");
        registrarAsignacion("REV002");

        ResultadoReintentos resultado =
                servicio.reintentarPendientes(1);

        assertEquals(1, resultado.procesadas());
        assertEquals(1, resultado.enviadas());
        assertEquals(0, resultado.fallidas());

        assertEquals(1, correo.intentosEnvio);

        assertEquals(
                1,
                repositorioNotificaciones
                        .listarPendientes(10)
                        .size()
        );
    }

    /**
     * Prueba 7: verifica que autores y revisores
     * no puedan ejecutar reintentos.
     */
    @Test
    void soloAdministradorPuedeReintentar() {

        registrarAsignacion("REV001");

        for (RolUsuario rol : List.of(
                RolUsuario.AUTOR,
                RolUsuario.REVISOR)) {

            IReintentoNotificacionesService otroServicio =
                    crearServicio(rol);

            assertThrows(
                    SecurityException.class,
                    () -> otroServicio.reintentarPendientes(10)
            );
        }

        assertEquals(0, correo.intentosEnvio);

        assertEquals(
                1,
                repositorioNotificaciones
                        .listarPendientes(10)
                        .size()
        );
    }

    /**
     * Prueba 8: verifica que se rechacen
     * cantidades inválidas de reintentos.
     */
    @Test
    void rechazaLimitesInvalidos() {

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.reintentarPendientes(0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.reintentarPendientes(-1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.reintentarPendientes(1001)
        );

        assertEquals(0, correo.intentosEnvio);
    }

    /**
     * Prueba 9: verifica que un correo enviado
     * no sea procesado nuevamente.
     */
    @Test
    void noReintentaNotificacionYaEnviada() {

        registrarAsignacion("REV001");

        ResultadoReintentos primerResultado =
                servicio.reintentarPendientes(10);

        assertEquals(1, primerResultado.enviadas());

        ResultadoReintentos segundoResultado =
                servicio.reintentarPendientes(10);

        assertEquals(0, segundoResultado.procesadas());
        assertEquals(0, segundoResultado.enviadas());
        assertEquals(0, segundoResultado.fallidas());

        assertEquals(1, correo.intentosEnvio);
    }

    /**
     * Prueba 10: verifica la consistencia
     * del resumen de reintentos.
     */
    @Test
    void resultadoReintentosValidaSusCantidades() {

        ResultadoReintentos resultado =
                new ResultadoReintentos(3, 2, 1);

        assertEquals(3, resultado.procesadas());
        assertEquals(2, resultado.enviadas());
        assertEquals(1, resultado.fallidas());

        assertThrows(
                IllegalArgumentException.class,
                () -> new ResultadoReintentos(-1, 0, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ResultadoReintentos(2, 3, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new ResultadoReintentos(3, 1, 1)
        );
    }

    /**
     * Construye el servicio con el rol indicado.
     *
     * @param rol rol del usuario de prueba
     * @return servicio configurado
     */
    private IReintentoNotificacionesService crearServicio(
            RolUsuario rol) {

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN001",
                rol
        );

        INotificadorRevision notificadorConRegistro =
                new NotificadorRevisionConRegistro(
                        correo,
                        repositorioNotificaciones
                );

        return new ReintentoNotificacionesServiceImpl(
                repositorioNotificaciones,
                repositorioAsignaciones,
                repositorioPreguntas,
                notificadorConRegistro,
                sesion
        );
    }

    /**
     * Registra una pregunta pendiente con un revisor
     * y genera su notificación mediante el trigger.
     *
     * @param revisorId identificador del docente
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
                        Instant.now()
                );

        assertTrue(
                repositorioAsignaciones.guardarAsignacion(
                        asignacion
                )
        );

        return pregunta.getId();
    }

    /**
     * Simula un servidor de correo electrónico.
     *
     * Permite provocar errores para determinados revisores.
     */
    private static class CorreoFalso
            implements INotificadorRevision {

        private int intentosEnvio;

        private final Set<String> revisoresConFallo =
                new HashSet<>();

        /**
         * Registra un intento y simula fallos SMTP
         * cuando el revisor está configurado para fallar.
         */
        @Override
        public void notificarAsignacion(
                Revisor revisor,
                Pregunta pregunta) {

            intentosEnvio++;

            if (revisoresConFallo.contains(revisor.id())) {

                throw new IllegalStateException(
                        "Servidor SMTP no disponible."
                );
            }
        }
    }
}
