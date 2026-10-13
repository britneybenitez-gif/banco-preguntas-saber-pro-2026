
package co.edu.unicauca.saberpro.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Representa la asignación de un revisor a una pregunta.
 *
 * @param preguntaId identificador de la pregunta
 * @param revisorId identificador del revisor asignado
 * @param administradorId administrador que realiza la asignación
 * @param fechaAsignacion fecha y hora de la asignación
 */
public record AsignacionRevision(
        String preguntaId,
        String revisorId,
        String administradorId,
        Instant fechaAsignacion) {

    /**
     * Valida los datos de la asignación.
     */
    public AsignacionRevision {

        if (preguntaId == null || preguntaId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador de la pregunta es obligatorio."
            );
        }

        if (revisorId == null || revisorId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del revisor es obligatorio."
            );
        }

        if (administradorId == null
                || administradorId.isBlank()) {

            throw new IllegalArgumentException(
                    "El identificador del administrador es obligatorio."
            );
        }

        Objects.requireNonNull(
                fechaAsignacion,
                "La fecha de asignación es obligatoria."
        );
    }
}
