
package co.edu.unicauca.saberpro.domain.service;

/**
 * Resume el resultado de una ejecución de reintentos.
 *
 * @param procesadas cantidad de notificaciones procesadas
 * @param enviadas cantidad de notificaciones enviadas
 * @param fallidas cantidad de notificaciones fallidas
 */
public record ResultadoReintentos(
        int procesadas,
        int enviadas,
        int fallidas) {

    /**
     * Verifica la consistencia del resultado.
     */
    public ResultadoReintentos {

        if (procesadas < 0 || enviadas < 0 || fallidas < 0) {
            throw new IllegalArgumentException(
                    "Las cantidades no pueden ser negativas."
            );
        }

        if (enviadas + fallidas != procesadas) {
            throw new IllegalArgumentException(
                    "El total procesado no coincide con los resultados."
            );
        }
    }
}
