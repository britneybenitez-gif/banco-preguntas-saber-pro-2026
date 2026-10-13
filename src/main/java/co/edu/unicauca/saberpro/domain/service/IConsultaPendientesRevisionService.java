
package co.edu.unicauca.saberpro.domain.service;

/**
 * Define la consulta administrativa de preguntas
 * pendientes de revisión para HU-04.
 */
public interface IConsultaPendientesRevisionService {

    /**
     * Consulta las preguntas pendientes de revisión
     * mediante paginación.
     *
     * @param pagina número de página, comenzando en 1
     * @param tamanoPagina cantidad de preguntas por página
     * @return resultado paginado de preguntas pendientes
     */
    PaginaPreguntas listarPendientes(
            int pagina,
            int tamanoPagina
    );
}
