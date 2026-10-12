
package co.edu.unicauca.saberpro.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de transición de estados de las preguntas.
 */
class PreguntaCambioEstadoTest {

    @Test
    void debeCambiarDeBorradorAPendienteRevision() {

        Pregunta pregunta = crearPregunta();

        assertEquals(
                EstadoPregunta.BORRADOR,
                pregunta.getEstado()
        );

        String idOriginal = pregunta.getId();

        pregunta.enviarARevision();

        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                pregunta.getEstado()
        );

        assertEquals(idOriginal, pregunta.getId());
    }

    @Test
    void noDebeEnviarDosVecesLaMismaPregunta() {

        Pregunta pregunta = crearPregunta();

        pregunta.enviarARevision();

        assertThrows(
                IllegalStateException.class,
                pregunta::enviarARevision
        );

        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                pregunta.getEstado()
        );
    }

    /**
     * Construye una pregunta para las pruebas.
     */
    private Pregunta crearPregunta() {

        return new Pregunta(
                "AUTOR01",
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
    
    
    @Test
    void debeReconstruirPreguntaConIdYEstadoOriginal() {

        Pregunta pregunta = Pregunta.reconstruir(
                "P001",
                "AUTOR01",
                "Contexto de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación",
                "Bibliografía",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media",
                EstadoPregunta.PENDIENTE_REVISION
        );

        assertEquals("P001", pregunta.getId());
        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                pregunta.getEstado()
        );

        assertThrows(
                IllegalStateException.class,
                pregunta::enviarARevision
        );
    }

    @Test
    void debeRechazarReconstruccionSinIdentificador() {

        assertThrows(
                IllegalArgumentException.class,
                () -> Pregunta.reconstruir(
                        "",
                        "AUTOR01",
                        "Contexto",
                        "Pregunta directa",
                        List.of("A", "B", "C", "D"),
                        "E",
                        "Justificación",
                        "Bibliografía",
                        "Competencia",
                        "Tema",
                        "Subtema",
                        "Media",
                        EstadoPregunta.BORRADOR
                )
        );
    }


}
