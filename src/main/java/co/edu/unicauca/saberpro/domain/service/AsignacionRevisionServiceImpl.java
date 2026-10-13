
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;
import co.edu.unicauca.saberpro.domain.repository.IAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.domain.repository.IEstadoPreguntaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Implementa la asignación de revisores y solicita
 * las notificaciones de correo correspondientes.
 */
public class AsignacionRevisionServiceImpl
        implements IAsignacionRevisionService {

    private final IAsignacionRevisionRepository repositorioAsignaciones;
    private final IEstadoPreguntaRepository repositorioPreguntas;
    private final INotificadorRevision notificador;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorioAsignaciones persistencia de asignaciones
     * @param repositorioPreguntas consulta de preguntas
     * @param sesion usuario que realiza la operación
     * @param notificador componente de notificaciones
     */
    public AsignacionRevisionServiceImpl(
            IAsignacionRevisionRepository repositorioAsignaciones,
            IEstadoPreguntaRepository repositorioPreguntas,
            SesionUsuario sesion,
            INotificadorRevision notificador) {

        this.repositorioAsignaciones = Objects.requireNonNull(
                repositorioAsignaciones,
                "El repositorio de asignaciones es obligatorio."
        );

        this.repositorioPreguntas = Objects.requireNonNull(
                repositorioPreguntas,
                "El repositorio de preguntas es obligatorio."
        );

        this.sesion = Objects.requireNonNull(
                sesion,
                "La sesión es obligatoria."
        );

        this.notificador = Objects.requireNonNull(
                notificador,
                "El notificador es obligatorio."
        );
    }

    /**
     * Asigna un revisor a una pregunta pendiente y,
     * cuando la asignación es nueva, solicita su notificación.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del revisor
     * @return true si se registró una nueva asignación
     */
    @Override
    public boolean asignarRevisor(
            String preguntaId,
            String revisorId) {

        verificarAdministrador();

        validarIdentificador(
                preguntaId,
                "El identificador de la pregunta es obligatorio."
        );

        validarIdentificador(
                revisorId,
                "El identificador del revisor es obligatorio."
        );

        // 1. Consultar la pregunta.
        Pregunta pregunta = repositorioPreguntas
                .buscarPorId(preguntaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La pregunta no existe."
                        )
                );

        // 2. Verificar su estado.
        if (pregunta.getEstado()
                != EstadoPregunta.PENDIENTE_REVISION) {

            throw new IllegalStateException(
                    "Solo se pueden asignar revisores "
                    + "a preguntas pendientes de revisión."
            );
        }

        // 3. Consultar el docente revisor.
        Revisor revisor = repositorioAsignaciones
                .buscarRevisorPorId(revisorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El revisor no está registrado."
                        )
                );

        // 4. Construir la asignación.
        AsignacionRevision asignacion =
                new AsignacionRevision(
                        pregunta.getId(),
                        revisor.id(),
                        sesion.usuarioId(),
                        Instant.now()
                );

        // 5. Guardar en SQLite.
        boolean guardada = repositorioAsignaciones
                .guardarAsignacion(asignacion);

        // No enviar otro correo si ya estaba asignado.
        if (!guardada) {
            return false;
        }

        // 6. Solicitar el envío de la notificación.
        try {

            notificador.notificarAsignacion(
                    revisor,
                    pregunta
            );

        } catch (RuntimeException e) {

            throw new NotificacionRevisionException(
                    "La asignación quedó guardada, "
                    + "pero falló el envío del correo "
                    + "al revisor.",
                    e
            );
        }

        return true;
    }

    /**
     * Consulta los revisores registrados.
     *
     * @return docentes disponibles
     */
    @Override
    public List<Revisor> listarRevisores() {

        verificarAdministrador();

        return repositorioAsignaciones.listarRevisores();
    }

    /**
     * Consulta los revisores asignados a una pregunta.
     *
     * @param preguntaId identificador de la pregunta
     * @return asignaciones encontradas
     */
    @Override
    public List<AsignacionRevision> listarAsignaciones(
            String preguntaId) {

        verificarAdministrador();

        validarIdentificador(
                preguntaId,
                "El identificador de la pregunta es obligatorio."
        );

        return repositorioAsignaciones
                .listarAsignaciones(preguntaId);
    }

    /**
     * Valida que el usuario tenga permisos administrativos.
     */
    private void verificarAdministrador() {

        if (sesion.rol() != RolUsuario.ADMINISTRADOR) {

            throw new SecurityException(
                    "Solo los administradores pueden "
                    + "gestionar asignaciones de revisores."
            );
        }
    }

    /**
     * Valida que un identificador no esté vacío.
     *
     * @param identificador dato que se desea comprobar
     * @param mensaje mensaje del error
     */
    private void validarIdentificador(
            String identificador,
            String mensaje) {

        if (identificador == null
                || identificador.isBlank()) {

            throw new IllegalArgumentException(mensaje);
        }
    }
}
