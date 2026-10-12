
package co.edu.unicauca.saberpro.domain.model;

/**
 * Define los estados de una pregunta.
 */
public enum EstadoPregunta {

    /** Pregunta guardada sin enviar a revisión. */
    BORRADOR,

    /** Pregunta en espera de asignación de revisor. */
    PENDIENTE_REVISION
}
