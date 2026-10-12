
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.service.IPreguntaService;

import java.util.List;
import java.util.Objects;

/**
 * Controla las solicitudes de registro de preguntas.
 */
public class RegistroPreguntaController {

    private final IPreguntaService servicio;

    /**
     * Inicializa el controlador con el servicio de preguntas.
     */
    public RegistroPreguntaController(IPreguntaService servicio) {
        this.servicio = Objects.requireNonNull(servicio);
    }

    /**
     * Envía una pregunta al servicio para su registro.
     *
     * @param pregunta pregunta que se desea registrar
     * @return errores encontrados o lista vacía si se registró
     */
    public List<String> registrarPregunta(Pregunta pregunta) {
        return servicio.registrarPregunta(pregunta);
    }
}
