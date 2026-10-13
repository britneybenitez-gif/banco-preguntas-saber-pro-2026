
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.Revisor;

import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones de persistencia de revisores
 * y asignaciones de preguntas.
 */
public interface IAsignacionRevisionRepository {

    /**
     * Registra un docente revisor.
     *
     * @param revisor docente que se desea registrar
     */
    void guardarRevisor(Revisor revisor);

    /**
     * Busca un revisor mediante su identificador.
     *
     * @param revisorId identificador del revisor
     * @return revisor encontrado o resultado vacío
     */
    Optional<Revisor> buscarRevisorPorId(String revisorId);

    /**
     * Consulta los revisores registrados.
     *
     * @return lista de revisores
     */
    List<Revisor> listarRevisores();

    /**
     * Guarda la asignación de una pregunta pendiente.
     *
     * @param asignacion asignación que se desea registrar
     * @return true si se registró correctamente
     */
    boolean guardarAsignacion(AsignacionRevision asignacion);

    /**
     * Consulta los revisores asignados a una pregunta.
     *
     * @param preguntaId identificador de la pregunta
     * @return asignaciones registradas
     */
    List<AsignacionRevision> listarAsignaciones(
            String preguntaId
    );
}
