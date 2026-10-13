
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.Revisor;

import co.edu.unicauca.saberpro.domain.service.IAsignacionRevisionService;
import co.edu.unicauca.saberpro.domain.service.IConsultaPendientesRevisionService;
import co.edu.unicauca.saberpro.domain.service.IReintentoNotificacionesService;
import co.edu.unicauca.saberpro.domain.service.PaginaPreguntas;
import co.edu.unicauca.saberpro.domain.service.ResultadoReintentos;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del controlador MVC de HU-04.
 */
class AsignacionRevisionControllerTest {

    /**
     * Prueba 1: verifica que el controlador delegue
     * la asignación al servicio correspondiente.
     */
    @Test
    void debeDelegarAsignacionAlServicio() {

        ServicioAsignacionesFalso asignaciones =
                new ServicioAsignacionesFalso();

        asignaciones.resultadoAsignacion = true;

        AsignacionRevisionController controlador =
                crearControlador(
                        asignaciones,
                        new ServicioReintentosFalso(),
                        new ServicioPendientesFalso()
                );

        boolean resultado = controlador.asignarRevisor(
                "PREG001",
                "REV001"
        );

        assertTrue(resultado);

        assertEquals(
                "PREG001",
                asignaciones.preguntaRecibida
        );

        assertEquals(
                "REV001",
                asignaciones.revisorRecibido
        );

        assertEquals(1, asignaciones.llamadasAsignar);
    }

    /**
     * Prueba 2: verifica que una asignación duplicada
     * devuelva false.
     */
    @Test
    void debeRetornarFalseSiAsignacionNoEsNueva() {

        ServicioAsignacionesFalso asignaciones =
                new ServicioAsignacionesFalso();

        AsignacionRevisionController controlador =
                crearControlador(
                        asignaciones,
                        new ServicioReintentosFalso(),
                        new ServicioPendientesFalso()
                );

        assertFalse(
                controlador.asignarRevisor(
                        "PREG001",
                        "REV001"
                )
        );

        assertEquals(1, asignaciones.llamadasAsignar);
    }

    /**
     * Prueba 3: verifica que el controlador
     * consulte los revisores disponibles.
     */
    @Test
    void debeListarRevisores() {

        ServicioAsignacionesFalso asignaciones =
                new ServicioAsignacionesFalso();

        List<Revisor> esperados = List.of(
                new Revisor(
                        "REV001",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        asignaciones.revisores = esperados;

        AsignacionRevisionController controlador =
                crearControlador(
                        asignaciones,
                        new ServicioReintentosFalso(),
                        new ServicioPendientesFalso()
                );

        List<Revisor> resultado =
                controlador.listarRevisores();

        assertSame(esperados, resultado);

        assertEquals(
                1,
                asignaciones.llamadasListarRevisores
        );
    }

    /**
     * Prueba 4: verifica que el controlador
     * consulte las asignaciones de una pregunta.
     */
    @Test
    void debeListarAsignaciones() {

        ServicioAsignacionesFalso asignaciones =
                new ServicioAsignacionesFalso();

        List<AsignacionRevision> esperadas = List.of(
                new AsignacionRevision(
                        "PREG001",
                        "REV001",
                        "ADMIN001",
                        Instant.parse("2026-10-12T15:00:00Z")
                )
        );

        asignaciones.asignaciones = esperadas;

        AsignacionRevisionController controlador =
                crearControlador(
                        asignaciones,
                        new ServicioReintentosFalso(),
                        new ServicioPendientesFalso()
                );

        List<AsignacionRevision> resultado =
                controlador.listarAsignaciones("PREG001");

        assertSame(esperadas, resultado);

        assertEquals(
                "PREG001",
                asignaciones.preguntaConsultada
        );
    }

    /**
     * Prueba 5: verifica que los reintentos
     * se deleguen al servicio correspondiente.
     */
    @Test
    void debeDelegarReintentos() {

        ServicioReintentosFalso reintentos =
                new ServicioReintentosFalso();

        ResultadoReintentos esperado =
                new ResultadoReintentos(3, 2, 1);

        reintentos.respuesta = esperado;

        AsignacionRevisionController controlador =
                crearControlador(
                        new ServicioAsignacionesFalso(),
                        reintentos,
                        new ServicioPendientesFalso()
                );

        ResultadoReintentos resultado =
                controlador.reintentarNotificaciones(10);

        assertSame(esperado, resultado);
        assertEquals(10, reintentos.limiteRecibido);
        assertEquals(1, reintentos.llamadas);
    }

    /**
     * Prueba 6: verifica que los errores
     * de autorización se propaguen.
     */
    @Test
    void debePropagarErrorDeAutorizacion() {

        ServicioAsignacionesFalso asignaciones =
                new ServicioAsignacionesFalso();

        asignaciones.error = new SecurityException(
                "Solo los administradores pueden asignar."
        );

        AsignacionRevisionController controlador =
                crearControlador(
                        asignaciones,
                        new ServicioReintentosFalso(),
                        new ServicioPendientesFalso()
                );

        assertThrows(
                SecurityException.class,
                () -> controlador.asignarRevisor(
                        "PREG001",
                        "REV001"
                )
        );
    }

    /**
     * Prueba 7: verifica que los servicios
     * del controlador sean obligatorios.
     */
    @Test
    void debeRechazarServiciosNulos() {

        ServicioAsignacionesFalso asignaciones =
                new ServicioAsignacionesFalso();

        ServicioReintentosFalso reintentos =
                new ServicioReintentosFalso();

        ServicioPendientesFalso pendientes =
                new ServicioPendientesFalso();

        assertThrows(
                NullPointerException.class,
                () -> new AsignacionRevisionController(
                        null,
                        reintentos,
                        pendientes
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new AsignacionRevisionController(
                        asignaciones,
                        null,
                        pendientes
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new AsignacionRevisionController(
                        asignaciones,
                        reintentos,
                        null
                )
        );
    }

    /**
     * Prueba 8: verifica que el controlador
     * delegue la consulta de preguntas pendientes.
     */
    @Test
    void debeDelegarConsultaDePendientes() {

        ServicioPendientesFalso pendientes =
                new ServicioPendientesFalso();

        PaginaPreguntas esperado =
                new PaginaPreguntas(
                        List.of(),
                        23,
                        2,
                        3
                );

        pendientes.respuesta = esperado;

        AsignacionRevisionController controlador =
                crearControlador(
                        new ServicioAsignacionesFalso(),
                        new ServicioReintentosFalso(),
                        pendientes
                );

        PaginaPreguntas resultado =
                controlador.listarPreguntasPendientes(
                        2,
                        10
                );

        assertSame(esperado, resultado);

        assertEquals(
                2,
                pendientes.paginaRecibida
        );

        assertEquals(
                10,
                pendientes.tamanoPaginaRecibido
        );

        assertEquals(1, pendientes.llamadas);
    }

    /**
     * Prueba 9: verifica que una consulta vacía
     * devuelva la página proporcionada por el servicio.
     */
    @Test
    void debeDevolverPaginaVacia() {

        ServicioPendientesFalso pendientes =
                new ServicioPendientesFalso();

        AsignacionRevisionController controlador =
                crearControlador(
                        new ServicioAsignacionesFalso(),
                        new ServicioReintentosFalso(),
                        pendientes
                );

        PaginaPreguntas resultado =
                controlador.listarPreguntasPendientes(
                        1,
                        10
                );

        assertTrue(resultado.preguntas().isEmpty());

        assertEquals(0, resultado.totalRegistros());
        assertEquals(0, resultado.totalPaginas());
        assertEquals(1, resultado.paginaActual());
    }

    /**
     * Prueba 10: verifica que los errores de
     * autorización de consulta se propaguen.
     */
    @Test
    void debePropagarErrorEnConsultaAdministrativa() {

        ServicioPendientesFalso pendientes =
                new ServicioPendientesFalso();

        pendientes.error = new SecurityException(
                "Solo los administradores pueden consultar."
        );

        AsignacionRevisionController controlador =
                crearControlador(
                        new ServicioAsignacionesFalso(),
                        new ServicioReintentosFalso(),
                        pendientes
                );

        assertThrows(
                SecurityException.class,
                () -> controlador.listarPreguntasPendientes(
                        1,
                        10
                )
        );

        assertEquals(1, pendientes.llamadas);
    }

    /**
     * Prueba 11: verifica que los parámetros
     * de paginación lleguen sin modificaciones.
     */
    @Test
    void debeEnviarParametrosDePaginacionAlServicio() {

        ServicioPendientesFalso pendientes =
                new ServicioPendientesFalso();

        AsignacionRevisionController controlador =
                crearControlador(
                        new ServicioAsignacionesFalso(),
                        new ServicioReintentosFalso(),
                        pendientes
                );

        controlador.listarPreguntasPendientes(
                3,
                5
        );

        assertEquals(3, pendientes.paginaRecibida);
        assertEquals(5, pendientes.tamanoPaginaRecibido);
        assertEquals(1, pendientes.llamadas);
    }

    /**
     * Construye un controlador con servicios simulados.
     *
     * @param asignaciones servicio de asignaciones
     * @param reintentos servicio de reintentos
     * @param pendientes servicio de consulta
     * @return controlador configurado
     */
    private AsignacionRevisionController crearControlador(
            IAsignacionRevisionService asignaciones,
            IReintentoNotificacionesService reintentos,
            IConsultaPendientesRevisionService pendientes) {

        return new AsignacionRevisionController(
                asignaciones,
                reintentos,
                pendientes
        );
    }

    /**
     * Simula el servicio de asignaciones de revisores.
     */
    private static class ServicioAsignacionesFalso
            implements IAsignacionRevisionService {

        private String preguntaRecibida;
        private String revisorRecibido;
        private String preguntaConsultada;

        private boolean resultadoAsignacion;

        private int llamadasAsignar;
        private int llamadasListarRevisores;

        private RuntimeException error;

        private List<Revisor> revisores = List.of();

        private List<AsignacionRevision> asignaciones =
                List.of();

        /**
         * Simula una operación de asignación.
         */
        @Override
        public boolean asignarRevisor(
                String preguntaId,
                String revisorId) {

            llamadasAsignar++;

            preguntaRecibida = preguntaId;
            revisorRecibido = revisorId;

            if (error != null) {
                throw error;
            }

            return resultadoAsignacion;
        }

        /**
         * Devuelve los revisores simulados.
         */
        @Override
        public List<Revisor> listarRevisores() {

            llamadasListarRevisores++;

            return revisores;
        }

        /**
         * Devuelve las asignaciones simuladas.
         */
        @Override
        public List<AsignacionRevision> listarAsignaciones(
                String preguntaId) {

            preguntaConsultada = preguntaId;

            return asignaciones;
        }
    }

    /**
     * Simula el servicio de reintentos de notificaciones.
     */
    private static class ServicioReintentosFalso
            implements IReintentoNotificacionesService {

        private int limiteRecibido;
        private int llamadas;

        private ResultadoReintentos respuesta =
                new ResultadoReintentos(0, 0, 0);

        /**
         * Devuelve un resumen simulado de reintentos.
         */
        @Override
        public ResultadoReintentos reintentarPendientes(
                int limite) {

            llamadas++;
            limiteRecibido = limite;

            return respuesta;
        }
    }

    /**
     * Simula el servicio de consulta de preguntas pendientes.
     */
    private static class ServicioPendientesFalso
            implements IConsultaPendientesRevisionService {

        private int paginaRecibida;
        private int tamanoPaginaRecibido;
        private int llamadas;

        private RuntimeException error;

        private PaginaPreguntas respuesta =
                new PaginaPreguntas(
                        List.of(),
                        0,
                        1,
                        0
                );

        /**
         * Registra la consulta y devuelve un resultado simulado.
         */
        @Override
        public PaginaPreguntas listarPendientes(
                int pagina,
                int tamanoPagina) {

            llamadas++;

            paginaRecibida = pagina;
            tamanoPaginaRecibido = tamanoPagina;

            if (error != null) {
                throw error;
            }

            return respuesta;
        }
    }
}
