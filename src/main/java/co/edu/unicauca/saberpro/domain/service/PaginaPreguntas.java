
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import java.util.List;

/**
 * Representa el resultado paginado de una consulta.
 *
 * @param preguntas preguntas de la página actual
 * @param totalRegistros cantidad total de resultados
 * @param paginaActual número de página consultada
 * @param totalPaginas cantidad total de páginas
 */
public record PaginaPreguntas(
        List<Pregunta> preguntas,
        long totalRegistros,
        int paginaActual,
        int totalPaginas) {

    /**
     * Protege la lista de resultados contra modificaciones.
     */
    public PaginaPreguntas {
        preguntas = List.copyOf(preguntas);
    }
}
