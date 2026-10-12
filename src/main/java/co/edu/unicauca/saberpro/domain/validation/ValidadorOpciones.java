
package co.edu.unicauca.saberpro.domain.validation;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Valida los distractores y la respuesta correcta.
 */
public class ValidadorOpciones implements IValidadorPregunta {

    @Override
    public List<String> validar(Pregunta pregunta) {

        List<String> errores = new ArrayList<>();

        if (pregunta == null) {
            errores.add("La pregunta no puede ser nula.");
            return errores;
        }

        List<String> distractores = pregunta.getDistractores();

        if (distractores.size() != 4) {
            errores.add("La pregunta debe tener exactamente cuatro distractores.");
        }

        Set<String> opciones = new HashSet<>();
        boolean repetidos = false;

        for (int i = 0; i < distractores.size(); i++) {

            String distractor = distractores.get(i);

            if (distractor == null || distractor.isBlank()) {
                errores.add("El distractor " + (i + 1) + " es obligatorio.");
                continue;
            }

            if (!opciones.add(normalizar(distractor))) {
                repetidos = true;
            }
        }

        if (repetidos) {
            errores.add("Los distractores no pueden repetirse.");
        }

        String respuesta = pregunta.getRespuestaCorrecta();

        if (respuesta == null || respuesta.isBlank()) {
            errores.add("La respuesta correcta es obligatoria.");
        } else if (opciones.contains(normalizar(respuesta))) {
            errores.add("La respuesta correcta no puede coincidir con un distractor.");
        }

        return errores;
    }

    /**
     * Normaliza el texto para comparar opciones.
     */
    private String normalizar(String texto) {
        return texto.strip().toLowerCase(Locale.ROOT);
    }
}
