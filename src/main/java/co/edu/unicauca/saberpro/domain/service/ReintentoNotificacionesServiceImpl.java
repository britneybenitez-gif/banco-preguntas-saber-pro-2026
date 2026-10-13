
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.NotificacionPendiente;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.domain.repository.IAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.domain.repository.IEstadoPreguntaRepository;
import co.edu.unicauca.saberpro.domain.repository.INotificacionRevisionRepository;

import java.util.List;
import java.util.Objects;

/**
 * Implementa el reintento de notificaciones pendientes
 * de asignaciones de revisión.
 */
public class ReintentoNotificacionesServiceImpl
        implements IReintentoNotificacionesService {

    private final INotificacionRevisionRepository repositorioNotificaciones;
    private final IAsignacionRevisionRepository repositorioAsignaciones;
    private final IEstadoPreguntaRepository repositorioPreguntas;
    private final INotificadorRevision notificador;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorioNotificaciones consulta de pendientes
     * @param repositorioAsignaciones consulta de revisores
     * @param repositorioPreguntas consulta de preguntas
     * @param notificador notificador con registro de resultados
     * @param sesion usuario que solicita los reintentos
     */
    public ReintentoNotificacionesServiceImpl(
            INotificacionRevisionRepository repositorioNotificaciones,
            IAsignacionRevisionRepository repositorioAsignaciones,
            IEstadoPreguntaRepository repositorioPreguntas,
            INotificadorRevision notificador,
            SesionUsuario sesion) {

        this.repositorioNotificaciones = Objects.requireNonNull(
                repositorioNotificaciones,
                "El repositorio de notificaciones es obligatorio."
        );

        this.repositorioAsignaciones = Objects.requireNonNull(
                repositorioAsignaciones,
                "El repositorio de asignaciones es obligatorio."
        );

        this.repositorioPreguntas = Objects.requireNonNull(
                repositorioPreguntas,
                "El repositorio de preguntas es obligatorio."
        );

        this.notificador = Objects.requireNonNull(
                notificador,
                "El notificador es obligatorio."
        );

        this.sesion = Objects.requireNonNull(
                sesion,
                "La sesión es obligatoria."
        );
    }

    /**
     * Reintenta las notificaciones pendientes y resume
     * los resultados de la operación.
     *
     * @param limite cantidad máxima de pendientes a procesar
     * @return cantidades procesadas, enviadas y fallidas
     */
    @Override
    public ResultadoReintentos reintentarPendientes(
            int limite) {

        if (sesion.rol() != RolUsuario.ADMINISTRADOR) {

            throw new SecurityException(
                    "Solo los administradores pueden "
                    + "reintentar notificaciones."
            );
        }

        if (limite < 1 || limite > 1000) {

            throw new IllegalArgumentException(
                    "El límite debe estar entre 1 y 1000."
            );
        }

        List<NotificacionPendiente> pendientes =
                repositorioNotificaciones.listarPendientes(
                        limite
                );

        int enviadas = 0;
        int fallidas = 0;

        for (NotificacionPendiente pendiente : pendientes) {

            try {

                Pregunta pregunta = repositorioPreguntas
                        .buscarPorId(pendiente.preguntaId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No se encontró la pregunta."
                                )
                        );

                Revisor revisor = repositorioAsignaciones
                        .buscarRevisorPorId(pendiente.revisorId())
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "No se encontró el revisor."
                                )
                        );

                /*
                 * Se debe inyectar un notificador con registro,
                 * como NotificadorRevisionConRegistro.
                 *
                 * Ese decorador marca ENVIADA cuando SMTP
                 * acepta el mensaje y registra los fallos.
                 */
                notificador.notificarAsignacion(
                        revisor,
                        pregunta
                );

                enviadas++;

            } catch (RuntimeException e) {

                // El fallo de un envío no detiene los siguientes.
                fallidas++;
            }
        }

        return new ResultadoReintentos(
                pendientes.size(),
                enviadas,
                fallidas
        );
    }
}
