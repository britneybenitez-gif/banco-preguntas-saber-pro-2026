
package co.edu.unicauca.saberpro.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     * Crea una pregunta con identificador único y estado BORRADOR.
     *
     * @param autorId identificador del autor
     * @param contexto contexto de la pregunta
     * @param preguntaDirecta enunciado
     * @param distractores opciones incorrectas
     * @param respuestaCorrecta opción correcta
     * @param justificacion explicación de la respuesta
     * @param bibliografia fuentes consultadas
     * @param competencia competencia evaluada
     * @param tema tema principal
     * @param subtema subtema
     * @param dificultad nivel de dificultad
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

        this.id = UUID.randomUUID().toString();
        this.autorId = autorId;
        this.contexto = contexto;
        this.preguntaDirecta = preguntaDirecta;

        this.distractores = distractores == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(
                        new ArrayList<>(distractores));

        this.respuestaCorrecta = respuestaCorrecta;
        this.justificacion = justificacion;
        this.bibliografia = bibliografia;
        this.competencia = competencia;
        this.tema = tema;
        this.subtema = subtema;
        this.dificultad = dificultad;
        this.estado = EstadoPregunta.BORRADOR;
    }

    /**
     * Cambia una pregunta en borrador a pendiente de revisión.
     *
     * @throws IllegalStateException si no está en borrador
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
