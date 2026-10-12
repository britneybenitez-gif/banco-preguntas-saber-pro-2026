
package co.edu.unicauca.saberpro.domain.service;

/**
 * Indica que la asignación fue guardada,
 * pero no pudo enviarse su notificación.
 */
public class NotificacionRevisionException
        extends RuntimeException {

    /**
     * Construye el error conservando su causa.
     *
     * @param mensaje descripción del problema
     * @param causa error original de notificación
     */
    public NotificacionRevisionException(
            String mensaje,
            Throwable causa) {

        super(mensaje, causa);
    }
}
