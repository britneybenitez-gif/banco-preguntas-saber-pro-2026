
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;
import co.edu.unicauca.saberpro.domain.repository.IConsultaPreguntaRepository;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del servicio de consulta de preguntas.
 */
class ConsultaPreguntaServiceImplTest {

    /**
     * Verifica que la consulta utilice al autor en sesión.
     */
    @Test
    void autorConsultaSusPropiasPreguntas() {

        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.total = 1;
        repositorio.datos = List.of(crearPregunta());

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        PaginaPreguntas resultado = servicio.listarMisPreguntas(
                null, null, 1, 10
        );

        assertEquals("AUTOR01", repositorio.autorConteo);
        assertEquals("AUTOR01", repositorio.autorListado);
        assertEquals(1, resultado.preguntas().size());
        assertEquals(1, repositorio.llamadasListado);
    }

    /**
     * Verifica que administradores y revisores no puedan consultar.
     */
    @Test
    void otrosRolesNoPuedenConsultarPreguntas() {

        for (RolUsuario rol : List.of(
                RolUsuario.ADMINISTRADOR,
                RolUsuario.REVISOR)) {

            RepositorioFalso repositorio =
                    new RepositorioFalso();

            IConsultaPreguntaService servicio =
                    crearServicio(repositorio, rol);

            assertThrows(
                    SecurityException.class,
                    () -> servicio.listarMisPreguntas(
                            null, null, 1, 10
                    )
            );

            assertEquals(0, repositorio.llamadasConteo);
            assertEquals(0, repositorio.llamadasListado);
        }
    }

    /**
     * Verifica que los filtros se envíen al repositorio.
     */
    @Test
    void aplicaFiltrosDeEstadoYDificultad() {

        RepositorioFalso repositorio = new RepositorioFalso();

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        servicio.listarMisPreguntas(
                EstadoPregunta.PENDIENTE_REVISION,
                " Media ",
                1,
                10
        );

        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                repositorio.estadoConteo
        );

        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                repositorio.estadoListado
        );

        assertEquals("Media", repositorio.dificultadConteo);
        assertEquals("Media", repositorio.dificultadListado);
    }

    /**
     * Verifica que una dificultad vacía no aplique filtro.
     */
    @Test
    void dificultadVaciaSeInterpretaComoSinFiltro() {

        RepositorioFalso repositorio = new RepositorioFalso();

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        servicio.listarMisPreguntas(null, "   ", 1, 10);

        assertNull(repositorio.dificultadConteo);
        assertNull(repositorio.dificultadListado);
    }

    /**
     * Verifica el cálculo de páginas y desplazamiento.
     */
    @Test
    void calculaCorrectamenteLaPaginacion() {

        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.total = 23;
        repositorio.datos = List.of(crearPregunta());

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        PaginaPreguntas resultado = servicio.listarMisPreguntas(
                null, null, 2, 10
        );

        assertEquals(23, resultado.totalRegistros());
        assertEquals(2, resultado.paginaActual());
        assertEquals(3, resultado.totalPaginas());

        assertEquals(10, repositorio.limite);
        assertEquals(10, repositorio.desplazamiento);
    }

    /**
     * Verifica el resultado cuando no hay preguntas.
     */
    @Test
    void consultaSinResultadosDevuelveCeroPaginas() {

        RepositorioFalso repositorio = new RepositorioFalso();

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        PaginaPreguntas resultado = servicio.listarMisPreguntas(
                null, null, 1, 10
        );

        assertTrue(resultado.preguntas().isEmpty());
        assertEquals(0, resultado.totalRegistros());
        assertEquals(0, resultado.totalPaginas());
        assertEquals(1, resultado.paginaActual());
    }

    /**
     * Verifica que se rechacen números de página inválidos.
     */
    @Test
    void rechazaNumeroDePaginaInvalido() {

        RepositorioFalso repositorio = new RepositorioFalso();

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.listarMisPreguntas(
                        null, null, 0, 10
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.listarMisPreguntas(
                        null, null, -1, 10
                )
        );

        assertEquals(0, repositorio.llamadasConteo);
    }

    /**
     * Verifica que el tamaño de página esté entre 1 y 100.
     */
    @Test
    void rechazaTamanoDePaginaInvalido() {

        RepositorioFalso repositorio = new RepositorioFalso();

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        for (int tamano : List.of(-1, 0, 101)) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> servicio.listarMisPreguntas(
                            null, null, 1, tamano
                    )
            );
        }

        assertEquals(0, repositorio.llamadasConteo);
    }

    /**
     * Verifica que el desplazamiento no supere el límite entero.
     */
    @Test
    void rechazaDesplazamientoDemasiadoGrande() {

        RepositorioFalso repositorio = new RepositorioFalso();

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.listarMisPreguntas(
                        null, null, Integer.MAX_VALUE, 2
                )
        );
    }

    /**
     * Verifica que se rechacen cantidades excesivas de páginas.
     */
    @Test
    void rechazaCantidadExcesivaDePaginas() {

        RepositorioFalso repositorio = new RepositorioFalso();

        repositorio.total = (long) Integer.MAX_VALUE + 1;

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        assertThrows(
                IllegalStateException.class,
                () -> servicio.listarMisPreguntas(
                        null, null, 1, 1
                )
        );
    }

    /**
     * Verifica que los resultados no puedan modificarse.
     */
    @Test
    void resultadosSonInmutables() {

        RepositorioFalso repositorio = new RepositorioFalso();

        List<Pregunta> datos = new ArrayList<>();
        datos.add(crearPregunta());

        repositorio.total = 1;
        repositorio.datos = datos;

        IConsultaPreguntaService servicio = crearServicio(
                repositorio, RolUsuario.AUTOR
        );

        PaginaPreguntas resultado = servicio.listarMisPreguntas(
                null, null, 1, 10
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> resultado.preguntas().clear()
        );

        datos.clear();

        assertEquals(1, resultado.preguntas().size());
    }

    /**
     * Construye un servicio con una sesión de prueba.
     */
    private IConsultaPreguntaService crearServicio(
            IConsultaPreguntaRepository repositorio,
            RolUsuario rol) {

        SesionUsuario sesion = new SesionUsuario(
                "AUTOR01", rol
        );

        return new ConsultaPreguntaServiceImpl(
                repositorio, sesion
        );
    }

    /**
     * Construye una pregunta válida para las pruebas.
     */
    private Pregunta crearPregunta() {

        return new Pregunta(
                "AUTOR01",
                "Contexto de prueba",
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
    }

    /**
     * Simula el repositorio para aislar las pruebas del servicio.
     */
    private static class RepositorioFalso
            implements IConsultaPreguntaRepository {

        private long total;
        private List<Pregunta> datos = List.of();

        private String autorConteo;
        private String autorListado;

        private EstadoPregunta estadoConteo;
        private EstadoPregunta estadoListado;

        private String dificultadConteo;
        private String dificultadListado;

        private int limite;
        private int desplazamiento;

        private int llamadasConteo;
        private int llamadasListado;

        /**
         * Simula la consulta paginada de preguntas.
         */
        @Override
        public List<Pregunta> listarPorAutor(
                String autorId,
                EstadoPregunta estado,
                String dificultad,
                int limite,
                int desplazamiento) {

            llamadasListado++;

            autorListado = autorId;
            estadoListado = estado;
            dificultadListado = dificultad;

            this.limite = limite;
            this.desplazamiento = desplazamiento;

            return datos;
        }

        /**
         * Simula el conteo de preguntas filtradas.
         */
        @Override
        public long contarPorAutor(
                String autorId,
                EstadoPregunta estado,
                String dificultad) {

            llamadasConteo++;

            autorConteo = autorId;
            estadoConteo = estado;
            dificultadConteo = dificultad;

            return total;
        }
    }
}
