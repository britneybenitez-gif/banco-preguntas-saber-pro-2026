
package co.edu.unicauca.saberpro.domain.validation;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import java.util.List;

/**
 * Define las operaciones de validación de una pregunta.
 */
public interface IValidadorPregunta {

    List<String> validar(Pregunta pregunta);
}
