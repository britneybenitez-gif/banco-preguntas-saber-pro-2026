
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;

/**
 * Define las operaciones de consulta de preguntas del autor.
 */
public interface IConsultaPreguntaService {

    /**
     * Consulta las preguntas del autor en sesión.
     *
     * @param estado estado solicitado o null para todos
     * @param dificultad dificultad o null para todas
     * @param pagina número de página, comenzando en 1
     * @param tamanoPagina cantidad de preguntas por página
     * @return resultados y datos de paginación
     */
    PaginaPreguntas listarMisPreguntas(
            EstadoPregunta estado,
            String dificultad,
            int pagina,
            int tamanoPagina
    );
}
