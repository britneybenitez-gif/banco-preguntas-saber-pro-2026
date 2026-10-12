
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones de cambio de estado de preguntas.
 */
public interface IPreguntaEstadoService {

    /**
     * Consulta una pregunta perteneciente al autor.
     *
     * @param preguntaId identificador de la pregunta
     * @return pregunta encontrada o vacío si no está disponible
     */
    Optional<Pregunta> consultarPregunta(String preguntaId);

    /**
     * Envía una pregunta a revisión.
     *
     * @param preguntaId identificador de la pregunta
     * @return errores de validación o lista vacía si tuvo éxito
     */
    List<String> enviarARevision(String preguntaId);
}
