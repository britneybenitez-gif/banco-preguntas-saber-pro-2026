
package co.edu.unicauca.saberpro.domain.validation;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del validador de campos obligatorios.
 */
class ValidadorCamposObligatoriosTest {

    private final IValidadorPregunta validador =
            new ValidadorCamposObligatorios();

    @Test
    void debeAceptarCamposCompletos() {

        Pregunta pregunta = crearPregunta("Contexto válido");

        assertTrue(validador.validar(pregunta).isEmpty());
    }

    @Test
    void debeRechazarContextoVacio() {

        Pregunta pregunta = crearPregunta(" ");

        List<String> errores = validador.validar(pregunta);

        assertTrue(errores.contains("Contexto es obligatorio."));
    }

    @Test
    void debeRechazarPreguntaNula() {

        List<String> errores = validador.validar(null);

        assertFalse(errores.isEmpty());
    }

    private Pregunta crearPregunta(String contexto) {

        return new Pregunta(
                "AUTOR01",
                contexto,
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
