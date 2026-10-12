
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
 * Implementa las reglas de negocio para asignar revisores
 * a preguntas pendientes de revisión.
 */
public class AsignacionRevisionServiceImpl
        implements IAsignacionRevisionService {

    private final IAsignacionRevisionRepository repositorioAsignaciones;
    private final IEstadoPreguntaRepository repositorioPreguntas;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorioAsignaciones repositorio de revisores
     * @param repositorioPreguntas repositorio de preguntas
     * @param sesion usuario que realiza la operación
     */
    public AsignacionRevisionServiceImpl(
            IAsignacionRevisionRepository repositorioAsignaciones,
            IEstadoPreguntaRepository repositorioPreguntas,
            SesionUsuario sesion) {

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
                "La sesión del usuario es obligatoria."
        );
    }

    /**
     * Asigna un revisor a una pregunta pendiente.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del revisor
     * @return true si la asignación fue registrada
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

        Pregunta pregunta = repositorioPreguntas
                .buscarPorId(preguntaId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La pregunta no existe."
                        )
                );

        if (pregunta.getEstado()
                != EstadoPregunta.PENDIENTE_REVISION) {

            throw new IllegalStateException(
                    "Solo se pueden asignar revisores a "
                    + "preguntas pendientes de revisión."
            );
        }

        Revisor revisor = repositorioAsignaciones
                .buscarRevisorPorId(revisorId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El revisor no está registrado."
                        )
                );

        AsignacionRevision asignacion =
                new AsignacionRevision(
                        pregunta.getId(),
                        revisor.id(),
                        sesion.usuarioId(),
                        Instant.now()
                );

        return repositorioAsignaciones
                .guardarAsignacion(asignacion);
    }

    /**
     * Consulta los revisores disponibles para asignación.
     *
     * @return revisores registrados
     */
    @Override
    public List<Revisor> listarRevisores() {

        verificarAdministrador();

        return repositorioAsignaciones.listarRevisores();
    }

    /**
     * Consulta las asignaciones de una pregunta.
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
     * Impide que usuarios distintos al administrador
     * realicen operaciones administrativas.
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
     * Verifica que un identificador tenga contenido.
     *
     * @param identificador valor que se desea validar
     * @param mensaje error que se mostrará si es inválido
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
