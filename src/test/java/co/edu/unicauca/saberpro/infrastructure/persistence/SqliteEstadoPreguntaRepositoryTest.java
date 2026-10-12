
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de persistencia para el cambio de estado.
 */
class SqliteEstadoPreguntaRepositoryTest {

    @TempDir
    Path directorioTemporal;

    @Test
    void debeRecuperarPreguntaConSusDatosOriginales() {

        Path ruta = directorioTemporal.resolve("consulta.db");

        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        Pregunta original = crearPregunta("AUTOR01");

        repositorio.guardar(original);

        SqlitePreguntaRepository nuevoRepositorio =
                new SqlitePreguntaRepository(ruta);

        Pregunta recuperada = nuevoRepositorio
                .buscarPorId(original.getId())
                .orElseThrow();

        assertAll(
                () -> assertEquals(
                        original.getId(),
                        recuperada.getId()
                ),
                () -> assertEquals(
                        original.getAutorId(),
                        recuperada.getAutorId()
                ),
                () -> assertEquals(
                        original.getContexto(),
                        recuperada.getContexto()
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
                        original.getRespuestaCorrecta(),
                        recuperada.getRespuestaCorrecta()
                ),
                () -> assertEquals(
                        EstadoPregunta.BORRADOR,
                        recuperada.getEstado()
                )
        );
    }

    @Test
    void debeDevolverVacioSiLaPreguntaNoExiste() {

        Path ruta = directorioTemporal.resolve("inexistente.db");

        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        assertTrue(
                repositorio.buscarPorId("P999").isEmpty()
        );
    }

    @Test
    void debeActualizarPreguntaAPendienteRevision() {

        Path ruta = directorioTemporal.resolve("actualizacion.db");

        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        Pregunta pregunta = crearPregunta("AUTOR01");

        repositorio.guardar(pregunta);

        boolean actualizado = repositorio.actualizarEstado(
                pregunta.getId(),
                "AUTOR01",
                EstadoPregunta.BORRADOR,
                EstadoPregunta.PENDIENTE_REVISION
        );

        assertTrue(actualizado);

        SqlitePreguntaRepository nuevoRepositorio =
                new SqlitePreguntaRepository(ruta);

        Pregunta recuperada = nuevoRepositorio
                .buscarPorId(pregunta.getId())
                .orElseThrow();

        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                recuperada.getEstado()
        );

        assertEquals(
                pregunta.getId(),
                recuperada.getId()
        );

        assertEquals(
                pregunta.getDistractores(),
                recuperada.getDistractores()
        );
    }

    @Test
    void noDebeActualizarPreguntaDeOtroAutor() {

        Path ruta = directorioTemporal.resolve("autor.db");

        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        Pregunta pregunta = crearPregunta("AUTOR01");

        repositorio.guardar(pregunta);

        boolean actualizado = repositorio.actualizarEstado(
                pregunta.getId(),
                "AUTOR02",
                EstadoPregunta.BORRADOR,
                EstadoPregunta.PENDIENTE_REVISION
        );

        assertFalse(actualizado);

        Pregunta recuperada = repositorio
                .buscarPorId(pregunta.getId())
                .orElseThrow();

        assertEquals(
                EstadoPregunta.BORRADOR,
                recuperada.getEstado()
        );
    }

    @Test
    void noDebeActualizarSiElEstadoAnteriorNoCoincide() {

        Path ruta = directorioTemporal.resolve("estado.db");

        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        Pregunta pregunta = crearPregunta("AUTOR01");

        repositorio.guardar(pregunta);

        boolean primeraActualizacion =
                repositorio.actualizarEstado(
                        pregunta.getId(),
                        "AUTOR01",
                        EstadoPregunta.BORRADOR,
                        EstadoPregunta.PENDIENTE_REVISION
                );

        assertTrue(primeraActualizacion);

        boolean segundaActualizacion =
                repositorio.actualizarEstado(
                        pregunta.getId(),
                        "AUTOR01",
                        EstadoPregunta.BORRADOR,
                        EstadoPregunta.PENDIENTE_REVISION
                );

        assertFalse(segundaActualizacion);

        Pregunta recuperada = repositorio
                .buscarPorId(pregunta.getId())
                .orElseThrow();

        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                recuperada.getEstado()
        );
    }

    /**
     * Construye una pregunta válida de prueba.
     */
    private Pregunta crearPregunta(String autorId) {

        return new Pregunta(
                autorId,
                "Contexto de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación",
                "Bibliografía",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );
    }
}
