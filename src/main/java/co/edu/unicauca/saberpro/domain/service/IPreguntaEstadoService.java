
package co.edu.unicauca.saberpro.domain.service;

import java.util.List;

/**
 * Define las operaciones de cambio de estado de preguntas.
 */
public interface IPreguntaEstadoService {

    /**
     * Envía una pregunta a revisión.
     *
     * @param preguntaId identificador de la pregunta
     * @return errores encontrados o lista vacía si tuvo éxito
     */
    List<String> enviarARevision(String preguntaId);
}
