
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.repository.IPreguntaRepository;
import co.edu.unicauca.saberpro.domain.validation.ValidadorCamposObligatorios;
import co.edu.unicauca.saberpro.domain.validation.ValidadorOpciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del servicio de registro de preguntas.
 */
class PreguntaServiceImplTest {

    private RepositorioFalso repositorio;
    private IPreguntaService servicio;

    @BeforeEach
    void setUp() {

        repositorio = new RepositorioFalso();

        servicio = new PreguntaServiceImpl(
                repositorio,
                List.of(
                        new ValidadorCamposObligatorios(),
                        new ValidadorOpciones()
                )
        );
    }

    @Test
    void debeRegistrarPreguntaValida() {

        Pregunta pregunta = crearPregunta(
                "Contexto válido",
                List.of("A", "B", "C", "D"),
                "E"
        );

        List<String> errores = servicio.registrarPregunta(pregunta);

        assertTrue(errores.isEmpty());
        assertEquals(1, repositorio.preguntas.size());
        assertSame(pregunta, repositorio.preguntas.get(0));
    }

    @Test
    void noDebeRegistrarPreguntaSinContexto() {

        Pregunta pregunta = crearPregunta(
                "",
                List.of("A", "B", "C", "D"),
                "E"
        );

        List<String> errores = servicio.registrarPregunta(pregunta);

        assertFalse(errores.isEmpty());
        assertTrue(repositorio.preguntas.isEmpty());
    }

    @Test
    void noDebeRegistrarDistractoresRepetidos() {

        Pregunta pregunta = crearPregunta(
                "Contexto válido",
                List.of("A", "B", "A", "D"),
                "E"
        );

        List<String> errores = servicio.registrarPregunta(pregunta);

        assertFalse(errores.isEmpty());
        assertTrue(repositorio.preguntas.isEmpty());
    }

    @Test
    void noDebeRegistrarPreguntaNula() {

        List<String> errores = servicio.registrarPregunta(null);

        assertFalse(errores.isEmpty());
        assertTrue(repositorio.preguntas.isEmpty());
    }

    @Test
    void debeRechazarServicioSinValidadores() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new PreguntaServiceImpl(
                        repositorio,
                        List.of()
                )
        );
    }

    private Pregunta crearPregunta(
            String contexto,
            List<String> distractores,
            String respuestaCorrecta) {

        return new Pregunta(
                "AUTOR01",
                contexto,
                "¿Cuál es la respuesta correcta?",
                distractores,
                respuestaCorrecta,
                "Justificación",
                "Bibliografía",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );
    }

    /**
     * Simula el almacenamiento para probar el servicio.
     */
    private static class RepositorioFalso
            implements IPreguntaRepository {

        private final List<Pregunta> preguntas = new ArrayList<>();

        @Override
        public void guardar(Pregunta pregunta) {
            preguntas.add(pregunta);
        }
    }
}
