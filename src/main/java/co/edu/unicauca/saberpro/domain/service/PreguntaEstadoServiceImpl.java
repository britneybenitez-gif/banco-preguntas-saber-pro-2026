
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;
import co.edu.unicauca.saberpro.domain.repository.IEstadoPreguntaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementa el envío de preguntas a revisión.
 */
public class PreguntaEstadoServiceImpl
        implements IPreguntaEstadoService {

    private final IEstadoPreguntaRepository repositorio;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorio repositorio de estados
     * @param sesion usuario que realiza la operación
     */
    public PreguntaEstadoServiceImpl(
            IEstadoPreguntaRepository repositorio,
            SesionUsuario sesion) {

        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio es obligatorio."
        );

        this.sesion = Objects.requireNonNull(
                sesion,
                "La sesión es obligatoria."
        );
    }

    /**
     * Valida y actualiza el estado de una pregunta.
     */
    @Override
    public List<String> enviarARevision(String preguntaId) {

        if (sesion.rol() != RolUsuario.AUTOR) {
            return List.of(
                    "Solo los autores pueden enviar preguntas a revisión."
            );
        }

        if (preguntaId == null || preguntaId.isBlank()) {
            return List.of(
                    "Debe seleccionar una pregunta."
            );
        }

        Optional<Pregunta> resultado =
                repositorio.buscarPorId(preguntaId);

        if (resultado.isEmpty()) {
            return List.of(
                    "La pregunta no existe."
            );
        }

        Pregunta pregunta = resultado.get();

        if (!pregunta.getAutorId().equals(sesion.usuarioId())) {
            return List.of(
                    "No puede enviar a revisión preguntas de otro autor."
            );
        }

        if (pregunta.getEstado() != EstadoPregunta.BORRADOR) {
            return List.of(
                    "Solo las preguntas en BORRADOR pueden enviarse a revisión."
            );
        }

        boolean actualizado = repositorio.actualizarEstado(
                pregunta.getId(),
                sesion.usuarioId(),
                EstadoPregunta.BORRADOR,
                EstadoPregunta.PENDIENTE_REVISION
        );

        if (!actualizado) {
            return List.of(
                    "No fue posible actualizar el estado de la pregunta."
            );
        }

        pregunta.enviarARevision();

        return List.of();
    }
}
