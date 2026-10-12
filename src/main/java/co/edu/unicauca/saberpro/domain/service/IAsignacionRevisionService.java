
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.Revisor;

import java.util.List;

/**
 * Define las operaciones de negocio para asignar revisores.
 */
public interface IAsignacionRevisionService {

    /**
     * Asigna un revisor a una pregunta pendiente de revisión.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del docente revisor
     * @return true si se registró una nueva asignación
     */
    boolean asignarRevisor(String preguntaId, String revisorId);

    /**
     * Consulta los docentes revisores disponibles.
     *
     * @return lista de revisores registrados
     */
    List<Revisor> listarRevisores();

    /**
     * Consulta las asignaciones de una pregunta.
     *
     * @param preguntaId identificador de la pregunta
     * @return lista de asignaciones registradas
     */
    List<AsignacionRevision> listarAsignaciones(
            String preguntaId
    );
}
