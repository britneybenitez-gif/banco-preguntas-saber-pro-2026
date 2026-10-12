
package co.edu.unicauca.saberpro.domain.repository;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

/**
 * Define las operaciones de persistencia de preguntas.
 */
public interface IPreguntaRepository {

    void guardar(Pregunta pregunta);

}
