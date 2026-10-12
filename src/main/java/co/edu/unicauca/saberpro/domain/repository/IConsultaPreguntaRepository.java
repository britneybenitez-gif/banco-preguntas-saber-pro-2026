
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.List;

/**
 * Define las operaciones de consulta de preguntas del autor.
 */
public interface IConsultaPreguntaRepository {

    /**
     * Lista las preguntas de un autor con filtros y paginación.
     *
     * @param autorId identificador del autor
     * @param estado estado por filtrar o null para todos
     * @param dificultad dificultad o null para todas
     * @param limite cantidad máxima de resultados
     * @param desplazamiento cantidad de registros que se omiten
     * @return preguntas que cumplen los criterios
     */
    List<Pregunta> listarPorAutor(
            String autorId,
            EstadoPregunta estado,
            String dificultad,
            int limite,
            int desplazamiento
    );

    /**
     * Cuenta las preguntas que cumplen los filtros.
     *
     * @param autorId identificador del autor
     * @param estado estado por filtrar o null para todos
     * @param dificultad dificultad o null para todas
     * @return cantidad total de preguntas encontradas
     */
    long contarPorAutor(
            String autorId,
            EstadoPregunta estado,
            String dificultad
    );
}
