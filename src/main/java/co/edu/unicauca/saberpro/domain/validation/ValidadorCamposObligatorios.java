
package co.edu.unicauca.saberpro.domain.validation;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import java.util.ArrayList;
import java.util.List;

/**
 * Verifica los campos obligatorios de una pregunta.
 */
public class ValidadorCamposObligatorios implements IValidadorPregunta {

    @Override
    public List<String> validar(Pregunta pregunta) {

        List<String> errores = new ArrayList<>();

        if (pregunta == null) {
            errores.add("La pregunta no puede ser nula.");
            return errores;
        }

        verificar(pregunta.getAutorId(), "Autor", errores);
        verificar(pregunta.getContexto(), "Contexto", errores);
        verificar(pregunta.getPreguntaDirecta(), "Pregunta directa", errores);
        verificar(pregunta.getRespuestaCorrecta(), "Respuesta correcta", errores);
        verificar(pregunta.getJustificacion(), "Justificación", errores);
        verificar(pregunta.getBibliografia(), "Bibliografía", errores);
        verificar(pregunta.getCompetencia(), "Competencia", errores);
        verificar(pregunta.getTema(), "Tema", errores);
        verificar(pregunta.getSubtema(), "Subtema", errores);
        verificar(pregunta.getDificultad(), "Dificultad", errores);

        return errores;
    }

    /**
     * Registra un error si el campo está vacío.
     */
    private void verificar(String valor, String campo, List<String> errores) {
        if (valor == null || valor.isBlank()) {
            errores.add(campo + " es obligatorio.");
        }
    }
}
