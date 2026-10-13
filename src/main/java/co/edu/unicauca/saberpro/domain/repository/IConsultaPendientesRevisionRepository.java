
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.List;

/**
 * Define las consultas de preguntas pendientes
 * de revisión para el módulo administrativo.
 */
public interface IConsultaPendientesRevisionRepository {

    /**
     * Recupera preguntas pendientes con paginación.
     *
     * @param limite cantidad máxima de resultados
     * @param desplazamiento registros que se deben omitir
     * @return preguntas pendientes encontradas
     */
    List<Pregunta> listarPendientes(
            int limite,
            int desplazamiento
    );

    /**
     * Cuenta todas las preguntas pendientes de revisión.
     *
     * @return cantidad total de preguntas pendientes
     */
    long contarPendientes();
}
