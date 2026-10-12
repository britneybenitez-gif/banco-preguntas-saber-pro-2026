
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import java.util.List;

/**
 * Define las operaciones del servicio de preguntas.
 */
public interface IPreguntaService {

    /**
     * Registra una pregunta si cumple las validaciones.
     *
     * @param pregunta pregunta que se desea registrar
     * @return lista de errores, vacía si se registró correctamente
     */
    List<String> registrarPregunta(Pregunta pregunta);
}
