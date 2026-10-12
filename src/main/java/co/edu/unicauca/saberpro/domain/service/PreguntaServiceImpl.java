
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.repository.IPreguntaRepository;
import co.edu.unicauca.saberpro.domain.validation.IValidadorPregunta;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Implementa el registro de preguntas.
 */
public class PreguntaServiceImpl implements IPreguntaService {

    private final IPreguntaRepository repositorio;
    private final List<IValidadorPregunta> validadores;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorio repositorio de preguntas
     * @param validadores validadores estructurales
     */
    public PreguntaServiceImpl(
            IPreguntaRepository repositorio,
            List<IValidadorPregunta> validadores) {

        this.repositorio = Objects.requireNonNull(repositorio);
        this.validadores = List.copyOf(validadores);

        if (this.validadores.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe existir al menos un validador.");
        }
    }

    /**
     * Valida y registra una pregunta.
     *
     * @param pregunta pregunta que se desea registrar
     * @return errores de validación o lista vacía si se registró
     */
    @Override
    public List<String> registrarPregunta(Pregunta pregunta) {

        if (pregunta == null) {
            return List.of("La pregunta no puede ser nula.");
        }

        List<String> errores = new ArrayList<>();

        for (IValidadorPregunta validador : validadores) {
            errores.addAll(validador.validar(pregunta));
        }

        if (!errores.isEmpty()) {
            return new ArrayList<>(new LinkedHashSet<>(errores));
        }

        repositorio.guardar(pregunta);

        return List.of();
    }
}
