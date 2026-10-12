
package co.edu.unicauca.saberpro.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa una pregunta de selección múltiple con única respuesta.
 */
public class Pregunta {

    private final String id;
    private final String autorId;
    private final String contexto;
    private final String preguntaDirecta;
    private final List<String> distractores;
    private final String respuestaCorrecta;
    private final String justificacion;
    private final String bibliografia;
    private final String competencia;
    private final String tema;
    private final String subtema;
    private final String dificultad;
    private EstadoPregunta estado;

    /**
     * Crea una pregunta nueva en estado BORRADOR.
     */
    public Pregunta(
            String autorId,
            String contexto,
            String preguntaDirecta,
            List<String> distractores,
            String respuestaCorrecta,
            String justificacion,
            String bibliografia,
            String competencia,
            String tema,
            String subtema,
            String dificultad) {

        this(
                UUID.randomUUID().toString(),
                autorId,
                contexto,
                preguntaDirecta,
                distractores,
                respuestaCorrecta,
                justificacion,
                bibliografia,
                competencia,
                tema,
                subtema,
                dificultad,
                EstadoPregunta.BORRADOR
        );
    }

    /**
     * Inicializa una pregunta con identificador y estado definidos.
     */
    private Pregunta(
            String id,
            String autorId,
            String contexto,
            String preguntaDirecta,
            List<String> distractores,
            String respuestaCorrecta,
            String justificacion,
            String bibliografia,
            String competencia,
            String tema,
            String subtema,
            String dificultad,
            EstadoPregunta estado) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador es obligatorio."
            );
        }

        this.id = id;
        this.autorId = autorId;
        this.contexto = contexto;
        this.preguntaDirecta = preguntaDirecta;

        this.distractores = distractores == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(
                        new ArrayList<>(distractores)
                );

        this.respuestaCorrecta = respuestaCorrecta;
        this.justificacion = justificacion;
        this.bibliografia = bibliografia;
        this.competencia = competencia;
        this.tema = tema;
        this.subtema = subtema;
        this.dificultad = dificultad;
        this.estado = Objects.requireNonNull(
                estado,
                "El estado es obligatorio."
        );
    }

    /**
     * Reconstruye una pregunta almacenada en la base de datos.
     */
    public static Pregunta reconstruir(
            String id,
            String autorId,
            String contexto,
            String preguntaDirecta,
            List<String> distractores,
            String respuestaCorrecta,
            String justificacion,
            String bibliografia,
            String competencia,
            String tema,
            String subtema,
            String dificultad,
            EstadoPregunta estado) {

        return new Pregunta(
                id,
                autorId,
                contexto,
                preguntaDirecta,
                distractores,
                respuestaCorrecta,
                justificacion,
                bibliografia,
                competencia,
                tema,
                subtema,
                dificultad,
                estado
        );
    }

    /**
     * Envía una pregunta en borrador a revisión.
     */
    public void enviarARevision() {

        if (estado != EstadoPregunta.BORRADOR) {
            throw new IllegalStateException(
                    "Solo las preguntas en BORRADOR pueden enviarse a revisión."
            );
        }

        this.estado = EstadoPregunta.PENDIENTE_REVISION;
    }

    public String getId() {
        return id;
    }

    public String getAutorId() {
        return autorId;
    }

    public String getContexto() {
        return contexto;
    }

    public String getPreguntaDirecta() {
        return preguntaDirecta;
    }

    public List<String> getDistractores() {
        return distractores;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public String getBibliografia() {
        return bibliografia;
    }

    public String getCompetencia() {
        return competencia;
    }

    public String getTema() {
        return tema;
    }

    public String getSubtema() {
        return subtema;
    }

    public String getDificultad() {
        return dificultad;
    }

    public EstadoPregunta getEstado() {
        return estado;
    }
}
