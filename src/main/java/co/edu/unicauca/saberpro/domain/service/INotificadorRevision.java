
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;

/**
 * Define el contrato de notificación para revisores.
 */
public interface INotificadorRevision {

    /**
     * Notifica a un docente que recibió una pregunta.
     *
     * @param revisor docente que recibe la asignación
     * @param pregunta pregunta asignada
     */
    void notificarAsignacion(
            Revisor revisor,
            Pregunta pregunta
    );
}
