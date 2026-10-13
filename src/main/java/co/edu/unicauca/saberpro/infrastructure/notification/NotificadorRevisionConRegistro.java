
package co.edu.unicauca.saberpro.infrastructure.notification;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.repository.INotificacionRevisionRepository;
import co.edu.unicauca.saberpro.domain.service.INotificadorRevision;

import java.util.Objects;

/**
 * Decora un notificador de revisión para registrar
 * los resultados de envío en el repositorio.
 */
public class NotificadorRevisionConRegistro
        implements INotificadorRevision {

    private final INotificadorRevision notificador;
    private final INotificacionRevisionRepository repositorio;

    /**
     * Inicializa la notificación con seguimiento persistente.
     *
     * @param notificador componente que realiza el envío
     * @param repositorio repositorio de estados de notificación
     */
    public NotificadorRevisionConRegistro(
            INotificadorRevision notificador,
            INotificacionRevisionRepository repositorio) {

        this.notificador = Objects.requireNonNull(
                notificador,
                "El notificador es obligatorio."
        );

        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio es obligatorio."
        );
    }

    /**
     * Envía una notificación y registra su resultado.
     *
     * @param revisor destinatario
     * @param pregunta pregunta asignada
     */
    @Override
    public void notificarAsignacion(
            Revisor revisor,
            Pregunta pregunta) {

        Objects.requireNonNull(
                revisor,
                "El revisor es obligatorio."
        );

        Objects.requireNonNull(
                pregunta,
                "La pregunta es obligatoria."
        );

        String preguntaId = pregunta.getId();
        String revisorId = revisor.id();

        try {

            notificador.notificarAsignacion(
                    revisor,
                    pregunta
            );

        } catch (RuntimeException errorEnvio) {

            // Se registra el fallo sin almacenar
            // contraseñas ni detalles sensibles de SMTP.
            try {

                boolean registrado =
                        repositorio.registrarFallo(
                                preguntaId,
                                revisorId,
                                "Error durante el envío SMTP."
                        );

                if (!registrado) {

                    errorEnvio.addSuppressed(
                            new IllegalStateException(
                                    "No se encontró una "
                                    + "notificación pendiente."
                            )
                    );
                }

            } catch (RuntimeException errorPersistencia) {

                errorEnvio.addSuppressed(
                        errorPersistencia
                );
            }

            throw errorEnvio;
        }

        // El correo fue aceptado por el componente
        // de envío. Actualizamos el estado persistente.
        boolean actualizada = repositorio.marcarEnviada(
                preguntaId,
                revisorId
        );

        if (!actualizada) {

            throw new IllegalStateException(
                    "El correo se envió, pero no se pudo "
                    + "confirmar su estado ENVIADA en SQLite."
            );
        }
    }
}
