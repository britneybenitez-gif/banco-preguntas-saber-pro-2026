
package co.edu.unicauca.saberpro.domain.service;

/**
 * Define el servicio de recuperación de notificaciones
 * de asignación pendientes.
 */
public interface IReintentoNotificacionesService {

    /**
     * Reintenta las notificaciones pendientes.
     *
     * @param limite cantidad máxima de correos a procesar
     * @return resumen de los intentos realizados
     */
    ResultadoReintentos reintentarPendientes(int limite);
}
