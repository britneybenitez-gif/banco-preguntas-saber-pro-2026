
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import java.util.List;
import java.util.Objects;

/**
 * Agrega autorización al registro de preguntas.
 */
public class ServicioRegistroAutorizado implements IPreguntaService {

    private final IPreguntaService servicio;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con la sesión del usuario.
     */
    public ServicioRegistroAutorizado(
            IPreguntaService servicio,
            SesionUsuario sesion) {

        this.servicio = Objects.requireNonNull(servicio);
        this.sesion = Objects.requireNonNull(sesion);
    }

    /**
     * Autoriza y delega el registro de una pregunta.
     */
    @Override
    public List<String> registrarPregunta(Pregunta pregunta) {

        if (sesion.rol() != RolUsuario.AUTOR) {
            return List.of(
                    "Solo los autores pueden registrar preguntas."
            );
        }

        if (pregunta == null) {
            return servicio.registrarPregunta(null);
        }

        if (!sesion.usuarioId().equals(pregunta.getAutorId())) {
            return List.of(
                    "No puede registrar preguntas a nombre de otro autor."
            );
        }

        return servicio.registrarPregunta(pregunta);
    }
}
