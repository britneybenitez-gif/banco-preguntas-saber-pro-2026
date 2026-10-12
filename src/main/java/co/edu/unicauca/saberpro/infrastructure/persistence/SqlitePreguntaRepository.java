
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.repository.IEstadoPreguntaRepository;
import co.edu.unicauca.saberpro.domain.repository.IPreguntaRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementa la persistencia de preguntas mediante SQLite.
 */
public class SqlitePreguntaRepository
        implements IPreguntaRepository, IEstadoPreguntaRepository {

    private final String url;

    /**
     * Inicializa la base de datos.
     *
     * @param ruta ruta del archivo SQLite
     */
    public SqlitePreguntaRepository(Path ruta) {

        Objects.requireNonNull(ruta, "La ruta es obligatoria");

        Path archivo = ruta.toAbsolutePath();

        try {
            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo crear el directorio de datos", e
            );
        }

        this.url = "jdbc:sqlite:" + archivo;
        crearTablas();
    }

    /**
     * Crea las tablas de preguntas y distractores.
     */
    private void crearTablas() {

        String tablaPreguntas = """
                CREATE TABLE IF NOT EXISTS preguntas (
                    id TEXT PRIMARY KEY,
                    autor_id TEXT NOT NULL,
                    contexto TEXT NOT NULL,
                    pregunta_directa TEXT NOT NULL,
                    respuesta_correcta TEXT NOT NULL,
                    justificacion TEXT NOT NULL,
                    bibliografia TEXT NOT NULL,
                    competencia TEXT NOT NULL,
                    tema TEXT NOT NULL,
                    subtema TEXT NOT NULL,
                    dificultad TEXT NOT NULL,
                    estado TEXT NOT NULL
                )
                """;

        String tablaDistractores = """
                CREATE TABLE IF NOT EXISTS distractores (
                    pregunta_id TEXT NOT NULL,
                    posicion INTEGER NOT NULL,
                    texto TEXT NOT NULL,
                    PRIMARY KEY (pregunta_id, posicion),
                    FOREIGN KEY (pregunta_id)
                        REFERENCES preguntas(id)
                )
                """;

        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement()) {

            sentencia.execute(tablaPreguntas);
            sentencia.execute(tablaDistractores);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al crear las tablas", e
            );
        }
    }

    /**
     * Abre una conexión con SQLite.
     */
    private Connection conectar() throws SQLException {

        Connection conexion = DriverManager.getConnection(url);

        try (Statement sentencia = conexion.createStatement()) {
            sentencia.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            conexion.close();
            throw e;
        }

        return conexion;
    }

    /**
     * Guarda una pregunta y sus distractores en una transacción.
     */
    @Override
    public void guardar(Pregunta pregunta) {

        Objects.requireNonNull(
                pregunta,
                "La pregunta es obligatoria"
        );

        String sqlPregunta = """
                INSERT INTO preguntas (
                    id, autor_id, contexto, pregunta_directa,
                    respuesta_correcta, justificacion,
                    bibliografia, competencia, tema, subtema,
                    dificultad, estado
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlDistractor = """
                INSERT INTO distractores (
                    pregunta_id, posicion, texto
                ) VALUES (?, ?, ?)
                """;

        try (Connection conexion = conectar()) {

            conexion.setAutoCommit(false);

            try {

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlPregunta)) {

                    ps.setString(1, pregunta.getId());
                    ps.setString(2, pregunta.getAutorId());
                    ps.setString(3, pregunta.getContexto());
                    ps.setString(4, pregunta.getPreguntaDirecta());
                    ps.setString(5, pregunta.getRespuestaCorrecta());
                    ps.setString(6, pregunta.getJustificacion());
                    ps.setString(7, pregunta.getBibliografia());
                    ps.setString(8, pregunta.getCompetencia());
                    ps.setString(9, pregunta.getTema());
                    ps.setString(10, pregunta.getSubtema());
                    ps.setString(11, pregunta.getDificultad());
                    ps.setString(12, pregunta.getEstado().name());

                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             conexion.prepareStatement(sqlDistractor)) {

                    for (int i = 0;
                         i < pregunta.getDistractores().size();
                         i++) {

                        ps.setString(1, pregunta.getId());
                        ps.setInt(2, i);
                        ps.setString(
                                3,
                                pregunta.getDistractores().get(i)
                        );

                        ps.executeUpdate();
                    }
                }

                conexion.commit();

            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar la pregunta", e
            );
        }
    }

    /**
     * Busca una pregunta por su identificador.
     */
    @Override
    public Optional<Pregunta> buscarPorId(String preguntaId) {

        if (preguntaId == null || preguntaId.isBlank()) {
            return Optional.empty();
        }

        String sqlPregunta = """
                SELECT *
                FROM preguntas
                WHERE id = ?
                """;

        String sqlDistractores = """
                SELECT texto
                FROM distractores
                WHERE pregunta_id = ?
                ORDER BY posicion
                """;

        try (Connection conexion = conectar();
             PreparedStatement psPregunta =
                     conexion.prepareStatement(sqlPregunta)) {

            psPregunta.setString(1, preguntaId);

            try (ResultSet resultado =
                         psPregunta.executeQuery()) {

                if (!resultado.next()) {
                    return Optional.empty();
                }

                List<String> distractores = new ArrayList<>();

                try (PreparedStatement psDistractores =
                             conexion.prepareStatement(sqlDistractores)) {

                    psDistractores.setString(1, preguntaId);

                    try (ResultSet opciones =
                                 psDistractores.executeQuery()) {

                        while (opciones.next()) {
                            distractores.add(
                                    opciones.getString("texto")
                            );
                        }
                    }
                }

                Pregunta pregunta = Pregunta.reconstruir(
                        resultado.getString("id"),
                        resultado.getString("autor_id"),
                        resultado.getString("contexto"),
                        resultado.getString("pregunta_directa"),
                        distractores,
                        resultado.getString("respuesta_correcta"),
                        resultado.getString("justificacion"),
                        resultado.getString("bibliografia"),
                        resultado.getString("competencia"),
                        resultado.getString("tema"),
                        resultado.getString("subtema"),
                        resultado.getString("dificultad"),
                        EstadoPregunta.valueOf(
                                resultado.getString("estado")
                        )
                );

                return Optional.of(pregunta);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar la pregunta", e
            );
        }
    }

    /**
     * Actualiza el estado si coinciden el autor y el estado anterior.
     */
    @Override
    public boolean actualizarEstado(
            String preguntaId,
            String autorId,
            EstadoPregunta estadoAnterior,
            EstadoPregunta estadoNuevo) {

        if (preguntaId == null || preguntaId.isBlank()
                || autorId == null || autorId.isBlank()) {
            return false;
        }

        Objects.requireNonNull(
                estadoAnterior,
                "El estado anterior es obligatorio"
        );

        Objects.requireNonNull(
                estadoNuevo,
                "El estado nuevo es obligatorio"
        );

        String sql = """
                UPDATE preguntas
                SET estado = ?
                WHERE id = ?
                  AND autor_id = ?
                  AND estado = ?
                """;

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estadoNuevo.name());
            ps.setString(2, preguntaId);
            ps.setString(3, autorId);
            ps.setString(4, estadoAnterior.name());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar el estado", e
            );
        }
    }
}
