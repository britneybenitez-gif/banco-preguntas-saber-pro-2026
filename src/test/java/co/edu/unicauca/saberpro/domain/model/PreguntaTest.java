
package co.edu.unicauca.saberpro.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de la entidad Pregunta.
 */
class PreguntaTest {

    private Pregunta pregunta;

    @BeforeEach
    void setUp() {
        pregunta = new Pregunta(
                "AUTOR01",
                "Contexto de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("Opción A", "Opción B", "Opción C", "Opción D"),
                "Opción E",
                "Justificación de prueba",
                "Bibliografía de prueba",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );
    }

    @Test
    void debeCrearPreguntaConIdentificador() {
        assertNotNull(pregunta.getId());
        assertFalse(pregunta.getId().isBlank());
    }

    @Test
    void debeIniciarEnEstadoBorrador() {
        assertEquals(EstadoPregunta.BORRADOR, pregunta.getEstado());
    }

    @Test
    void debeConservarDatosDeLaPregunta() {
        assertEquals("AUTOR01", pregunta.getAutorId());
        assertEquals("Contexto de prueba", pregunta.getContexto());
        assertEquals("Opción E", pregunta.getRespuestaCorrecta());
        assertEquals(4, pregunta.getDistractores().size());
    }

    @Test
    void debeProtegerListaDeDistractores() {

        List<String> distractores = new ArrayList<>(
                List.of("A", "B", "C", "D")
        );

        Pregunta nuevaPregunta = new Pregunta(
                "AUTOR01",
                "Contexto",
                "¿Pregunta?",
                distractores,
                "E",
                "Justificación",
                "Bibliografía",
                "Competencia",
                "Tema",
                "Subtema",
                "Media"
        );

        distractores.set(0, "Modificada");

        assertEquals("A", nuevaPregunta.getDistractores().get(0));

        assertThrows(
                UnsupportedOperationException.class,
                () -> nuevaPregunta.getDistractores().add("F")
        );
    }
}
