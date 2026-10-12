
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica el listado, los filtros y la paginación en SQLite.
 */
class SqliteConsultaPreguntaRepositoryTest {

    @TempDir
    Path directorioTemporal;

    /**
     * Verifica que únicamente se listen las preguntas del autor.
     */
    @Test
    void debeListarSoloPreguntasDelAutor() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        repositorio.guardar(
                crearPregunta("AUTOR01", "Media", "Pregunta 1")
        );

        repositorio.guardar(
                crearPregunta("AUTOR02", "Media", "Pregunta 2")
        );

        repositorio.guardar(
                crearPregunta("AUTOR01", "Alta", "Pregunta 3")
        );

        List<Pregunta> resultado = repositorio.listarPorAutor(
                "AUTOR01", null, null, 10, 0
        );

        assertEquals(2, resultado.size());

        assertTrue(
                resultado.stream().allMatch(
                        pregunta -> pregunta.getAutorId()
                                .equals("AUTOR01")
                )
        );
    }

    /**
     * Verifica que se recuperen los datos originales.
     */
    @Test
    void debeRecuperarDatosCompletosDeLasPreguntas() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        Pregunta original = crearPregunta(
                "AUTOR01",
                "Media",
                "¿Cuál es la respuesta correcta?"
        );

        repositorio.guardar(original);

        Pregunta recuperada = repositorio.listarPorAutor(
                "AUTOR01", null, null, 10, 0
        ).get(0);

        assertAll(
                () -> assertEquals(
                        original.getId(),
                        recuperada.getId()
                ),
                () -> assertEquals(
                        original.getPreguntaDirecta(),
                        recuperada.getPreguntaDirecta()
                ),
                () -> assertEquals(
                        original.getDistractores(),
                        recuperada.getDistractores()
                ),
                () -> assertEquals(
                        original.getCompetencia(),
                        recuperada.getCompetencia()
                ),
                () -> assertEquals(
                        original.getDificultad(),
                        recuperada.getDificultad()
                ),
                () -> assertEquals(
                        EstadoPregunta.BORRADOR,
                        recuperada.getEstado()
                )
        );
    }

    /**
     * Verifica los filtros por estado de la pregunta.
     */
    @Test
    void debeFiltrarPorEstado() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        Pregunta borrador = crearPregunta(
                "AUTOR01", "Media", "Pregunta borrador"
        );

        Pregunta pendiente = crearPregunta(
                "AUTOR01", "Alta", "Pregunta pendiente"
        );

        repositorio.guardar(borrador);
        repositorio.guardar(pendiente);

        enviarARevision(repositorio, pendiente);

        List<Pregunta> borradores = repositorio.listarPorAutor(
                "AUTOR01",
                EstadoPregunta.BORRADOR,
                null,
                10,
                0
        );

        List<Pregunta> pendientes = repositorio.listarPorAutor(
                "AUTOR01",
                EstadoPregunta.PENDIENTE_REVISION,
                null,
                10,
                0
        );

        assertEquals(1, borradores.size());
        assertEquals(borrador.getId(), borradores.get(0).getId());

        assertEquals(1, pendientes.size());
        assertEquals(pendiente.getId(), pendientes.get(0).getId());
    }

    /**
     * Verifica el filtro por nivel de dificultad.
     */
    @Test
    void debeFiltrarPorDificultad() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        repositorio.guardar(
                crearPregunta("AUTOR01", "Media", "Pregunta 1")
        );

        repositorio.guardar(
                crearPregunta("AUTOR01", "Alta", "Pregunta 2")
        );

        repositorio.guardar(
                crearPregunta("AUTOR01", "Media", "Pregunta 3")
        );

        List<Pregunta> resultado = repositorio.listarPorAutor(
                "AUTOR01",
                null,
                "Media",
                10,
                0
        );

        assertEquals(2, resultado.size());

        assertTrue(
                resultado.stream().allMatch(
                        pregunta -> pregunta.getDificultad()
                                .equals("Media")
                )
        );
    }

    /**
     * Verifica la aplicación simultánea de varios filtros.
     */
    @Test
    void debeCombinarFiltrosDeEstadoYDificultad() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        Pregunta borradorMedia = crearPregunta(
                "AUTOR01", "Media", "Pregunta 1"
        );

        Pregunta pendienteMedia = crearPregunta(
                "AUTOR01", "Media", "Pregunta 2"
        );

        Pregunta pendienteAlta = crearPregunta(
                "AUTOR01", "Alta", "Pregunta 3"
        );

        repositorio.guardar(borradorMedia);
        repositorio.guardar(pendienteMedia);
        repositorio.guardar(pendienteAlta);

        enviarARevision(repositorio, pendienteMedia);
        enviarARevision(repositorio, pendienteAlta);

        List<Pregunta> resultado = repositorio.listarPorAutor(
                "AUTOR01",
                EstadoPregunta.PENDIENTE_REVISION,
                "Media",
                10,
                0
        );

        assertEquals(1, resultado.size());

        assertEquals(
                pendienteMedia.getId(),
                resultado.get(0).getId()
        );
    }

    /**
     * Verifica el orden y la división de resultados en páginas.
     */
    @Test
    void debePaginarLasPreguntasCorrectamente() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        List<Pregunta> registradas = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {

            Pregunta pregunta = crearPregunta(
                    "AUTOR01",
                    "Media",
                    "Pregunta " + i
            );

            repositorio.guardar(pregunta);
            registradas.add(pregunta);
        }

        List<Pregunta> paginaUno = repositorio.listarPorAutor(
                "AUTOR01", null, null, 2, 0
        );

        List<Pregunta> paginaDos = repositorio.listarPorAutor(
                "AUTOR01", null, null, 2, 2
        );

        List<Pregunta> paginaTres = repositorio.listarPorAutor(
                "AUTOR01", null, null, 2, 4
        );

        assertEquals(2, paginaUno.size());
        assertEquals(2, paginaDos.size());
        assertEquals(1, paginaTres.size());

        assertEquals(
                registradas.get(4).getId(),
                paginaUno.get(0).getId()
        );

        assertEquals(
                registradas.get(2).getId(),
                paginaDos.get(0).getId()
        );

        assertEquals(
                registradas.get(0).getId(),
                paginaTres.get(0).getId()
        );
    }

    /**
     * Verifica el conteo según los filtros seleccionados.
     */
    @Test
    void debeContarPreguntasConFiltros() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        Pregunta primera = crearPregunta(
                "AUTOR01", "Media", "Pregunta 1"
        );

        Pregunta segunda = crearPregunta(
                "AUTOR01", "Media", "Pregunta 2"
        );

        Pregunta tercera = crearPregunta(
                "AUTOR01", "Alta", "Pregunta 3"
        );

        repositorio.guardar(primera);
        repositorio.guardar(segunda);
        repositorio.guardar(tercera);

        repositorio.guardar(
                crearPregunta("AUTOR02", "Media", "Pregunta ajena")
        );

        enviarARevision(repositorio, segunda);
        enviarARevision(repositorio, tercera);

        assertEquals(
                3,
                repositorio.contarPorAutor(
                        "AUTOR01", null, null
                )
        );

        assertEquals(
                2,
                repositorio.contarPorAutor(
                        "AUTOR01",
                        EstadoPregunta.PENDIENTE_REVISION,
                        null
                )
        );

        assertEquals(
                2,
                repositorio.contarPorAutor(
                        "AUTOR01", null, "Media"
                )
        );

        assertEquals(
                1,
                repositorio.contarPorAutor(
                        "AUTOR01",
                        EstadoPregunta.PENDIENTE_REVISION,
                        "Media"
                )
        );
    }

    /**
     * Verifica que una consulta sin coincidencias esté vacía.
     */
    @Test
    void debeDevolverListaVaciaSiNoHayPreguntas() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        List<Pregunta> resultado = repositorio.listarPorAutor(
                "AUTOR_INEXISTENTE",
                null,
                null,
                10,
                0
        );

        long total = repositorio.contarPorAutor(
                "AUTOR_INEXISTENTE",
                null,
                null
        );

        assertTrue(resultado.isEmpty());
        assertEquals(0, total);
    }

    /**
     * Verifica que se rechacen identificadores de autor inválidos.
     */
    @Test
    void debeRechazarAutorInvalido() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPorAutor(
                        null, null, null, 10, 0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPorAutor(
                        " ", null, null, 10, 0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.contarPorAutor(
                        null, null, null
                )
        );
    }

    /**
     * Verifica que se rechacen parámetros de paginación inválidos.
     */
    @Test
    void debeRechazarPaginacionInvalida() {

        SqlitePreguntaRepository repositorio = crearRepositorio();

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPorAutor(
                        "AUTOR01", null, null, 0, 0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPorAutor(
                        "AUTOR01", null, null, -1, 0
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPorAutor(
                        "AUTOR01", null, null, 10, -1
                )
        );
    }

    /**
     * Crea un repositorio con una base de datos temporal.
     */
    private SqlitePreguntaRepository crearRepositorio() {

        Path ruta = directorioTemporal.resolve("hu03.db");

        return new SqlitePreguntaRepository(ruta);
    }

    /**
     * Construye una pregunta válida para las pruebas.
     */
    private Pregunta crearPregunta(
            String autorId,
            String dificultad,
            String enunciado) {

        return new Pregunta(
                autorId,
                "Contexto de prueba",
                enunciado,
                List.of("3", "5", "6", "8"),
                "4",
                "Justificación académica",
                "Bibliografía de prueba",
                "Razonamiento cuantitativo",
                "Aritmética",
                "Suma",
                dificultad
        );
    }

    /**
     * Actualiza una pregunta para probar los filtros por estado.
     */
    private void enviarARevision(
            SqlitePreguntaRepository repositorio,
            Pregunta pregunta) {

        boolean actualizado = repositorio.actualizarEstado(
                pregunta.getId(),
                pregunta.getAutorId(),
                EstadoPregunta.BORRADOR,
                EstadoPregunta.PENDIENTE_REVISION
        );

        assertTrue(actualizado);
    }
}
