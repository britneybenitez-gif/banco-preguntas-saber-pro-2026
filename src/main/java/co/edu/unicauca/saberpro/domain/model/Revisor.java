
package co.edu.unicauca.saberpro.domain.model;

/**
 * Representa a un docente encargado de revisar preguntas.
 *
 * @param id identificador del revisor
 * @param nombre nombre del docente
 * @param correo dirección para recibir notificaciones
 */
public record Revisor(
        String id,
        String nombre,
        String correo) {

    /**
     * Valida los datos del revisor.
     */
    public Revisor {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del revisor es obligatorio."
            );
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del revisor es obligatorio."
            );
        }

        if (correo == null
                || !correo.matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"
                )) {

            throw new IllegalArgumentException(
                    "El correo del revisor no es válido."
            );
        }
    }
}
