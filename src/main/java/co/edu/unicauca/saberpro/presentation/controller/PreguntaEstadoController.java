
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.service.IPreguntaEstadoService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controla las solicitudes de consulta y cambio de estado.
 */
public class PreguntaEstadoController {

    private final IPreguntaEstadoService servicio;

    /**
     * Inicializa el controlador con el servicio de estados.
     *
     * @param servicio servicio de gestión de estados
     */
    public PreguntaEstadoController(
            IPreguntaEstadoService servicio) {

        this.servicio = Objects.requireNonNull(
                servicio,
                "El servicio es obligatorio."
        );
    }

    /**
     * Consulta una pregunta del autor en sesión.
     *
     * @param preguntaId identificador de la pregunta
     * @return pregunta encontrada o resultado vacío
     */
    public Optional<Pregunta> consultarPregunta(
            String preguntaId) {

        return servicio.consultarPregunta(preguntaId);
    }

    /**
     * Solicita enviar una pregunta a revisión.
     *
     * @param preguntaId identificador de la pregunta
     * @return errores encontrados o lista vacía si tuvo éxito
     */
    public List<String> enviarARevision(
            String preguntaId) {

        return servicio.enviarARevision(preguntaId);
    }
}
