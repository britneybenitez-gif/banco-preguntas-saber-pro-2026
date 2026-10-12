
package co.edu.unicauca.saberpro.domain.model;

/**
 * Representa una notificación que necesita enviarse.
 *
 * @param preguntaId identificador de la pregunta
 * @param revisorId identificador del destinatario
 * @param intentos número de intentos realizados
 */
public record NotificacionPendiente(
        String preguntaId,
        String revisorId,
        int intentos) {

    /**
     * Valida los datos de una notificación pendiente.
     */
    public NotificacionPendiente {

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

        if (intentos < 0) {
            throw new IllegalArgumentException(
                    "Los intentos no pueden ser negativos."
            );
        }
    }
}
