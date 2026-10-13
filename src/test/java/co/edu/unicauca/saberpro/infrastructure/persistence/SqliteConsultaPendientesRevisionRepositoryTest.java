
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de consulta administrativa de preguntas
 * pendientes de revisión mediante SQLite.
 */
class SqliteConsultaPendientesRevisionRepositoryTest {

    @TempDir
    Path directorioTemporal;

    private Path rutaBaseDatos;
    private SqlitePreguntaRepository repositorioPreguntas;
    private SqliteConsultaPendientesRevisionRepository repositorio;

    /**
     * Prepara una base de datos temporal independiente.
     */
    @BeforeEach
    void preparar() {

        rutaBaseDatos = directorioTemporal.resolve(
                "hu04-consulta.db"
        );

        repositorioPreguntas =
                new SqlitePreguntaRepository(rutaBaseDatos);

        repositorio =
                new SqliteConsultaPendientesRevisionRepository(
                        rutaBaseDatos,
                        repositorioPreguntas
                );
    }

    /**
     * Verifica el resultado cuando no existen pendientes.
     */
    @Test
    void sinPreguntasPendientesDevuelveListaVacia() {

        crearPregunta("AUTOR01", false);

        assertTrue(
                repositorio.listarPendientes(10, 0).isEmpty()
        );

        assertEquals(0, repositorio.contarPendientes());
    }

    /**
     * Verifica que se recuperen preguntas pendientes
     * de diferentes autores.
     */
    @Test
    void listaPendientesDeTodosLosAutores() {

        crearPregunta("AUTOR01", false);
        crearPregunta("AUTOR01", true);
        crearPregunta("AUTOR02", true);

        List<Pregunta> resultado =
                repositorio.listarPendientes(10, 0);

        assertEquals(2, resultado.size());
        assertEquals(2, repositorio.contarPendientes());

        Set<String> autores = resultado.stream()
                .map(Pregunta::getAutorId)
                .collect(Collectors.toSet());

        assertEquals(
                Set.of("AUTOR01", "AUTOR02"),
                autores
        );

        assertTrue(
                resultado.stream().allMatch(p ->
                        p.getEstado()
                                == EstadoPregunta.PENDIENTE_REVISION
                )
        );
    }

    /**
     * Verifica el límite y desplazamiento de la consulta.
     */
    @Test
    void respetaLaPaginacion() {

        Pregunta primera = crearPregunta("AUTOR01", true);
        Pregunta segunda = crearPregunta("AUTOR02", true);
        Pregunta tercera = crearPregunta("AUTOR03", true);

        List<Pregunta> paginaUno =
                repositorio.listarPendientes(2, 0);

        List<Pregunta> paginaDos =
                repositorio.listarPendientes(2, 2);

        assertEquals(2, paginaUno.size());
        assertEquals(1, paginaDos.size());

        assertEquals(
                tercera.getId(),
                paginaUno.get(0).getId()
        );

        assertEquals(
                segunda.getId(),
                paginaUno.get(1).getId()
        );

        assertEquals(
                primera.getId(),
                paginaDos.get(0).getId()
        );

        assertEquals(3, repositorio.contarPendientes());
    }

    /**
     * Verifica que se recuperen los campos completos
     * de una pregunta almacenada.
     */
    @Test
    void recuperaLosDatosCompletosDeLaPregunta() {

        Pregunta original = crearPregunta("AUTOR01", true);

        Pregunta recuperada =
                repositorio.listarPendientes(10, 0).get(0);

        assertEquals(original.getId(), recuperada.getId());

        assertEquals(
                original.getPreguntaDirecta(),
                recuperada.getPreguntaDirecta()
        );

        assertEquals(
                original.getDistractores(),
                recuperada.getDistractores()
        );

        assertEquals(
                original.getCompetencia(),
                recuperada.getCompetencia()
        );

        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                recuperada.getEstado()
        );
    }

    /**
     * Verifica que se rechacen parámetros inválidos.
     */
    @Test
    void rechazaParametrosDePaginacionInvalidos() {

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPendientes(0, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPendientes(-1, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> repositorio.listarPendientes(10, -1)
        );
    }

    /**
     * Verifica que la consulta funcione después
     * de crear una nueva instancia del repositorio.
     */
    @Test
    void conservaLasPreguntasPendientesEnSQLite() {

        Pregunta registrada =
                crearPregunta("AUTOR02", true);

        SqliteConsultaPendientesRevisionRepository nuevo =
                new SqliteConsultaPendientesRevisionRepository(
                        rutaBaseDatos,
                        repositorioPreguntas
                );

        assertEquals(1, nuevo.contarPendientes());

        List<Pregunta> resultado =
                nuevo.listarPendientes(10, 0);

        assertEquals(1, resultado.size());

        assertEquals(
                registrada.getId(),
                resultado.get(0).getId()
        );

        assertEquals(
                "AUTOR02",
                resultado.get(0).getAutorId()
        );
    }

    /**
     * Crea una pregunta de un autor y opcionalmente
     * cambia su estado a pendiente de revisión.
     *
     * @param autorId identificador del autor
     * @param pendiente indica si debe enviarse a revisión
     * @return pregunta registrada
     */
    private Pregunta crearPregunta(
            String autorId,
            boolean pendiente) {

        Pregunta pregunta = new Pregunta(
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

        repositorioPreguntas.guardar(pregunta);

        if (pendiente) {

            boolean actualizado =
                    repositorioPreguntas.actualizarEstado(
                            pregunta.getId(),
                            autorId,
                            EstadoPregunta.BORRADOR,
                            EstadoPregunta.PENDIENTE_REVISION
                    );

            assertTrue(actualizado);
        }

        return pregunta;
    }
}
