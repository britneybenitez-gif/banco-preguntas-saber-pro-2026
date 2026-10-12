
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.repository.IPreguntaRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Implementa la persistencia de preguntas mediante SQLite.
 */
public class SqlitePreguntaRepository implements IPreguntaRepository {

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
                    "No se pudo crear el directorio de datos", e);
        }

        this.url = "jdbc:sqlite:" + archivo;
        crearTablas();
    }

    /**
     * Crea las tablas necesarias para registrar preguntas.
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
                    "Error al crear las tablas", e);
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

        Objects.requireNonNull(pregunta, "La pregunta es obligatoria");

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

                    for (int i = 0; i < pregunta.getDistractores().size(); i++) {
                        ps.setString(1, pregunta.getId());
                        ps.setInt(2, i);
                        ps.setString(3, pregunta.getDistractores().get(i));
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
                    "Error al guardar la pregunta", e);
        }
    }
}
