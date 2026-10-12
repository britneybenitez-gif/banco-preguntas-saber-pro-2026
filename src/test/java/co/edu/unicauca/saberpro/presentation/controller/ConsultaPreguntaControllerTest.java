
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.service.IConsultaPreguntaService;
import co.edu.unicauca.saberpro.domain.service.PaginaPreguntas;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del controlador de consulta de preguntas.
 */
class ConsultaPreguntaControllerTest {

    /**
     * Verifica que el controlador delegue filtros y paginación.
     */
    @Test
    void debeDelegarFiltrosYPaginacionAlServicio() {

        ServicioFalso servicio = new ServicioFalso();

        PaginaPreguntas respuestaEsperada =
                new PaginaPreguntas(
                        List.of(),
                        0,
                        2,
                        0
                );

        servicio.respuesta = respuestaEsperada;

        ConsultaPreguntaController controlador =
                new ConsultaPreguntaController(servicio);

        PaginaPreguntas resultado =
                controlador.listarMisPreguntas(
                        EstadoPregunta.BORRADOR,
                        "Alta",
                        2,
                        5
                );

        assertSame(respuestaEsperada, resultado);

        assertEquals(
                EstadoPregunta.BORRADOR,
                servicio.estadoRecibido
        );

        assertEquals("Alta", servicio.dificultadRecibida);
        assertEquals(2, servicio.paginaRecibida);
        assertEquals(5, servicio.tamanoRecibido);
        assertEquals(1, servicio.numeroConsultas);
    }

    /**
     * Verifica que se permitan consultas sin filtros.
     */
    @Test
    void debePermitirConsultasSinFiltros() {

        ServicioFalso servicio = new ServicioFalso();

        ConsultaPreguntaController controlador =
                new ConsultaPreguntaController(servicio);

        PaginaPreguntas resultado =
                controlador.listarMisPreguntas(
                        null,
                        null,
                        1,
                        10
                );

        assertNull(servicio.estadoRecibido);
        assertNull(servicio.dificultadRecibida);

        assertEquals(1, servicio.paginaRecibida);
        assertEquals(10, servicio.tamanoRecibido);
        assertTrue(resultado.preguntas().isEmpty());
        assertEquals(1, servicio.numeroConsultas);
    }

    /**
     * Verifica que se propaguen los errores de autorización.
     */
    @Test
    void debePropagarErroresDeAutorizacion() {

        ServicioFalso servicio = new ServicioFalso();

        servicio.error = new SecurityException(
                "Solo los autores pueden consultar sus preguntas."
        );

        ConsultaPreguntaController controlador =
                new ConsultaPreguntaController(servicio);

        SecurityException excepcion = assertThrows(
                SecurityException.class,
                () -> controlador.listarMisPreguntas(
                        null,
                        null,
                        1,
                        10
                )
        );

        assertEquals(
                "Solo los autores pueden consultar sus preguntas.",
                excepcion.getMessage()
        );
    }

    /**
     * Verifica que el controlador requiera un servicio válido.
     */
    @Test
    void debeRechazarServicioNulo() {

        assertThrows(
                NullPointerException.class,
                () -> new ConsultaPreguntaController(null)
        );
    }

    /**
     * Simula el servicio de consulta para aislar al controlador.
     */
    private static class ServicioFalso
            implements IConsultaPreguntaService {

        private EstadoPregunta estadoRecibido;
        private String dificultadRecibida;

        private int paginaRecibida;
        private int tamanoRecibido;
        private int numeroConsultas;

        private RuntimeException error;

        private PaginaPreguntas respuesta =
                new PaginaPreguntas(
                        List.of(),
                        0,
                        1,
                        0
                );

        /**
         * Registra los parámetros y devuelve una respuesta simulada.
         */
        @Override
        public PaginaPreguntas listarMisPreguntas(
                EstadoPregunta estado,
                String dificultad,
                int pagina,
                int tamanoPagina) {

            numeroConsultas++;

            estadoRecibido = estado;
            dificultadRecibida = dificultad;
            paginaRecibida = pagina;
            tamanoRecibido = tamanoPagina;

            if (error != null) {
                throw error;
            }

            return respuesta;
        }
    }
}
