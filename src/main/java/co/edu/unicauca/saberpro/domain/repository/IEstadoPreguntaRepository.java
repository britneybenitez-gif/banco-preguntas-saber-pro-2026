
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.Optional;

/**
 * Define las operaciones de consulta y cambio de estado.
 */
public interface IEstadoPreguntaRepository {

    /**
     * Busca una pregunta por su identificador.
     */
    Optional<Pregunta> buscarPorId(String preguntaId);

    /**
     * Actualiza el estado si coinciden el autor y el estado anterior.
     */
    boolean actualizarEstado(
            String preguntaId,
            String autorId,
            EstadoPregunta estadoAnterior,
            EstadoPregunta estadoNuevo
    );
}
