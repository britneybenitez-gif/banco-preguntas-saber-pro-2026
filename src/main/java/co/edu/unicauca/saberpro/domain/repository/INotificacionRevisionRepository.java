
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.NotificacionPendiente;

import java.util.List;

/**
 * Define las operaciones de persistencia de notificaciones.
 */
public interface INotificacionRevisionRepository {

    /**
     * Consulta las notificaciones que aún necesitan enviarse.
     *
     * @param limite cantidad máxima de resultados
     * @return notificaciones pendientes
     */
    List<NotificacionPendiente> listarPendientes(int limite);

    /**
     * Marca una notificación como enviada.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del destinatario
     * @return true si se actualizó correctamente
     */
    boolean marcarEnviada(
            String preguntaId,
            String revisorId
    );

    /**
     * Registra un intento fallido de envío.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del destinatario
     * @param descripcion explicación segura del fallo
     * @return true si se registró el fallo
     */
    boolean registrarFallo(
            String preguntaId,
            String revisorId,
            String descripcion
    );
}
