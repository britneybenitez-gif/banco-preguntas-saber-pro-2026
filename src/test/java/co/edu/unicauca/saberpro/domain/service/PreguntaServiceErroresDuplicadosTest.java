
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.repository.IPreguntaRepository;
import co.edu.unicauca.saberpro.domain.validation.IValidadorPregunta;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica que los errores de validación no se repitan.
 */
class PreguntaServiceErroresDuplicadosTest {

    @Test
    void noDebeDevolverErroresDuplicados() {

        IPreguntaRepository repositorio = pregunta ->
                fail("No debe guardar una pregunta inválida.");

        IValidadorPregunta validadorUno = pregunta ->
                List.of(
                        "Campo obligatorio.",
                        "Opciones inválidas."
                );

        IValidadorPregunta validadorDos = pregunta ->
                List.of("Campo obligatorio.");

        PreguntaServiceImpl servicio = new PreguntaServiceImpl(
                repositorio,
                List.of(validadorUno, validadorDos)
        );

        Pregunta pregunta = new Pregunta(
                "AUTOR01",
                "Contexto de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación",
                "Bibliografía",
                "Competencia",
                "Tema",
                "Subtema",
                "Media"
        );

        List<String> errores = servicio.registrarPregunta(pregunta);

        assertEquals(
                List.of(
                        "Campo obligatorio.",
                        "Opciones inválidas."
                ),
                errores
        );
    }
}
