
package co.edu.unicauca.saberpro.domain.validation;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del validador de opciones.
 */
class ValidadorOpcionesTest {

    private final IValidadorPregunta validador =
            new ValidadorOpciones();

    @Test
    void debeAceptarOpcionesValidas() {
        Pregunta pregunta = crearPregunta(
                List.of("A", "B", "C", "D"), "E"
        );

        assertTrue(validador.validar(pregunta).isEmpty());
    }

    @Test
    void debeRechazarMenosDeCuatroDistractores() {
        Pregunta pregunta = crearPregunta(
                List.of("A", "B", "C"), "D"
        );

        assertTrue(validador.validar(pregunta)
                .contains("La pregunta debe tener exactamente cuatro distractores."));
    }

    @Test
    void debeRechazarDistractoresRepetidos() {
        Pregunta pregunta = crearPregunta(
                List.of("A", "B", "a", "D"), "E"
        );

        assertTrue(validador.validar(pregunta)
                .contains("Los distractores no pueden repetirse."));
    }

    @Test
    void debeRechazarDistractoresVacios() {
        Pregunta pregunta = crearPregunta(
                List.of("A", " ", "C", "D"), "E"
        );

        assertTrue(validador.validar(pregunta)
                .contains("El distractor 2 es obligatorio."));
    }

    @Test
    void debeRechazarRespuestaRepetida() {
        Pregunta pregunta = crearPregunta(
                List.of("A", "B", "C", "D"), " a "
        );

        assertTrue(validador.validar(pregunta)
                .contains("La respuesta correcta no puede coincidir con un distractor."));
    }

    @Test
    void debeRechazarRespuestaVacia() {
        Pregunta pregunta = crearPregunta(
                List.of("A", "B", "C", "D"), " "
        );

        assertTrue(validador.validar(pregunta)
                .contains("La respuesta correcta es obligatoria."));
    }

    private Pregunta crearPregunta(
            List<String> distractores,
            String respuestaCorrecta) {

        return new Pregunta(
                "AUTOR01",
                "Contexto de prueba",
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
}
