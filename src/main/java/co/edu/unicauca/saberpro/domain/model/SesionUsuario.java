
package co.edu.unicauca.saberpro.domain.model;

import java.util.Objects;

/**
 * Representa la identidad y el rol del usuario en sesión.
 */
public record SesionUsuario(
        String usuarioId,
        RolUsuario rol) {

    /**
     * Valida los datos de la sesión.
     */
    public SesionUsuario {

        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del usuario es obligatorio."
            );
        }

        Objects.requireNonNull(
                rol,
                "El rol del usuario es obligatorio."
        );
    }
}
