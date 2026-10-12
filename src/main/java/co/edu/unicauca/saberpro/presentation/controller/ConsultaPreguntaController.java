
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.service.IConsultaPreguntaService;
import co.edu.unicauca.saberpro.domain.service.PaginaPreguntas;

import java.util.Objects;

/**
 * Controla las consultas de preguntas del autor.
 */
public class ConsultaPreguntaController {

    private final IConsultaPreguntaService servicio;

    /**
     * Inicializa el controlador con el servicio de consulta.
     *
     * @param servicio servicio de consulta de preguntas
     */
    public ConsultaPreguntaController(
            IConsultaPreguntaService servicio) {

        this.servicio = Objects.requireNonNull(
                servicio,
                "El servicio es obligatorio."
        );
    }

    /**
     * Consulta las preguntas del autor con filtros y paginación.
     *
     * @param estado estado seleccionado o null para todos
     * @param dificultad dificultad o null para todas
     * @param pagina número de página solicitado
     * @param tamanoPagina cantidad de registros por página
     * @return página con los resultados de la consulta
     */
    public PaginaPreguntas listarMisPreguntas(
            EstadoPregunta estado,
            String dificultad,
            int pagina,
            int tamanoPagina) {

        return servicio.listarMisPreguntas(
                estado,
                dificultad,
                pagina,
                tamanoPagina
        );
    }
}
