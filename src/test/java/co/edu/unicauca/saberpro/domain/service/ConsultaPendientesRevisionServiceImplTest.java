
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.domain.repository.IConsultaPendientesRevisionRepository;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del servicio administrativo
 * de consulta de preguntas pendientes de HU-04.
 */
class ConsultaPendientesRevisionServiceImplTest {

    /**
     * Prueba 1: verifica que el administrador pueda
     * consultar preguntas de diferentes autores.
     */
    @Test
    void administradorPuedeConsultarPendientes() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        repositorio.total = 2;

        repositorio.datos = List.of(
                crearPregunta("AUTOR001"),
                crearPregunta("AUTOR002")
        );

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        PaginaPreguntas resultado =
                servicio.listarPendientes(1, 10);

        assertEquals(2, resultado.preguntas().size());
        assertEquals(2, resultado.totalRegistros());

        assertEquals(
                "AUTOR001",
                resultado.preguntas().get(0).getAutorId()
        );

        assertEquals(
                "AUTOR002",
                resultado.preguntas().get(1).getAutorId()
        );

        assertEquals(1, repositorio.llamadasConteo);
        assertEquals(1, repositorio.llamadasListado);
    }

    /**
     * Prueba 2: verifica que los roles AUTOR y REVISOR
     * no puedan consultar el listado administrativo.
     */
    @Test
    void autorYRevisorNoPuedenConsultarPendientes() {

        for (RolUsuario rol : List.of(
                RolUsuario.AUTOR,
                RolUsuario.REVISOR)) {

            RepositorioFalso repositorio =
                    new RepositorioFalso();

            IConsultaPendientesRevisionService servicio =
                    crearServicio(repositorio, rol);

            assertThrows(
                    SecurityException.class,
                    () -> servicio.listarPendientes(1, 10)
            );

            assertEquals(0, repositorio.llamadasConteo);
            assertEquals(0, repositorio.llamadasListado);
        }
    }

    /**
     * Prueba 3: verifica el cálculo de páginas
     * y el desplazamiento enviado al repositorio.
     */
    @Test
    void calculaCorrectamenteLaPaginacion() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        repositorio.total = 23;

        repositorio.datos = List.of(
                crearPregunta("AUTOR001")
        );

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        PaginaPreguntas resultado =
                servicio.listarPendientes(2, 10);

        assertEquals(23, resultado.totalRegistros());
        assertEquals(2, resultado.paginaActual());
        assertEquals(3, resultado.totalPaginas());

        assertEquals(10, repositorio.limite);
        assertEquals(10, repositorio.desplazamiento);
    }

    /**
     * Prueba 4: verifica que una consulta sin resultados
     * devuelva una lista vacía y cero páginas.
     */
    @Test
    void sinPendientesDevuelveCeroPaginas() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        PaginaPreguntas resultado =
                servicio.listarPendientes(1, 10);

        assertTrue(resultado.preguntas().isEmpty());

        assertEquals(0, resultado.totalRegistros());
        assertEquals(0, resultado.totalPaginas());
        assertEquals(1, resultado.paginaActual());
    }

    /**
     * Prueba 5: verifica que el número de página
     * sea mayor o igual a uno.
     */
    @Test
    void rechazaNumeroDePaginaInvalido() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        for (int pagina : List.of(-1, 0)) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> servicio.listarPendientes(
                            pagina,
                            10
                    )
            );
        }

        assertEquals(0, repositorio.llamadasConteo);
        assertEquals(0, repositorio.llamadasListado);
    }

    /**
     * Prueba 6: verifica que el tamaño de página
     * esté entre uno y cien.
     */
    @Test
    void rechazaTamanoDePaginaInvalido() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        for (int tamano : List.of(-1, 0, 101)) {

            assertThrows(
                    IllegalArgumentException.class,
                    () -> servicio.listarPendientes(
                            1,
                            tamano
                    )
            );
        }

        assertEquals(0, repositorio.llamadasConteo);
        assertEquals(0, repositorio.llamadasListado);
    }

    /**
     * Prueba 7: verifica que el desplazamiento
     * no supere el límite de números enteros.
     */
    @Test
    void rechazaDesplazamientoDemasiadoGrande() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.listarPendientes(
                        Integer.MAX_VALUE,
                        2
                )
        );

        assertEquals(0, repositorio.llamadasListado);
    }

    /**
     * Prueba 8: verifica que la cantidad total de páginas
     * no supere el rango de números enteros.
     */
    @Test
    void rechazaCantidadExcesivaDePaginas() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        repositorio.total =
                (long) Integer.MAX_VALUE + 1;

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        assertThrows(
                IllegalStateException.class,
                () -> servicio.listarPendientes(1, 1)
        );

        assertEquals(0, repositorio.llamadasListado);
    }

    /**
     * Prueba 9: verifica que el resultado devuelto
     * no permita modificar la lista de preguntas.
     */
    @Test
    void resultadoDevuelveListaInmutable() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        List<Pregunta> datos = new ArrayList<>();

        datos.add(
                crearPregunta("AUTOR001")
        );

        repositorio.total = 1;
        repositorio.datos = datos;

        IConsultaPendientesRevisionService servicio =
                crearServicio(
                        repositorio,
                        RolUsuario.ADMINISTRADOR
                );

        PaginaPreguntas resultado =
                servicio.listarPendientes(1, 10);

        assertThrows(
                UnsupportedOperationException.class,
                () -> resultado.preguntas().clear()
        );

        datos.clear();

        assertEquals(1, resultado.preguntas().size());
    }

    /**
     * Prueba 10: verifica que las dependencias
     * del constructor sean obligatorias.
     */
    @Test
    void rechazaDependenciasNulas() {

        RepositorioFalso repositorio =
                new RepositorioFalso();

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN001",
                RolUsuario.ADMINISTRADOR
        );

        assertThrows(
                NullPointerException.class,
                () -> new ConsultaPendientesRevisionServiceImpl(
                        null,
                        sesion
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new ConsultaPendientesRevisionServiceImpl(
                        repositorio,
                        null
                )
        );
    }

    /**
     * Construye un servicio con el rol solicitado.
     *
     * @param repositorio repositorio simulado
     * @param rol rol que se desea probar
     * @return servicio configurado
     */
    private IConsultaPendientesRevisionService crearServicio(
            IConsultaPendientesRevisionRepository repositorio,
            RolUsuario rol) {

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN001",
                rol
        );

        return new ConsultaPendientesRevisionServiceImpl(
                repositorio,
                sesion
        );
    }

    /**
     * Crea una pregunta válida para las pruebas.
     *
     * @param autorId identificador del autor
     * @return pregunta construida
     */
    private Pregunta crearPregunta(String autorId) {

        return new Pregunta(
                autorId,
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
    }

    /**
     * Simula el repositorio de consulta administrativa.
     */
    private static class RepositorioFalso
            implements IConsultaPendientesRevisionRepository {

        private long total;

        private List<Pregunta> datos = List.of();

        private int limite;
        private int desplazamiento;

        private int llamadasConteo;
        private int llamadasListado;

        /**
         * Simula el listado paginado de preguntas pendientes.
         */
        @Override
        public List<Pregunta> listarPendientes(
                int limite,
                int desplazamiento) {

            llamadasListado++;

            this.limite = limite;
            this.desplazamiento = desplazamiento;

            return datos;
        }

        /**
         * Simula el conteo de preguntas pendientes.
         */
        @Override
        public long contarPendientes() {

            llamadasConteo++;

            return total;
        }
    }
}
