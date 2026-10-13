
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.repository.IConsultaPendientesRevisionRepository;
import co.edu.unicauca.saberpro.domain.repository.IEstadoPreguntaRepository;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Consulta preguntas pendientes de revisión en SQLite.
 *
 * Recupera preguntas de todos los autores sin modificar
 * las consultas privadas de HU-03.
 */
public class SqliteConsultaPendientesRevisionRepository
        implements IConsultaPendientesRevisionRepository {

    private final String url;
    private final IEstadoPreguntaRepository repositorioPreguntas;

    /**
     * Inicializa la consulta administrativa.
     *
     * Debe usar la misma base de datos que las preguntas.
     *
     * @param ruta ubicación del archivo SQLite
     * @param repositorioPreguntas repositorio para recuperar preguntas
     */
    public SqliteConsultaPendientesRevisionRepository(
            Path ruta,
            IEstadoPreguntaRepository repositorioPreguntas) {

        Objects.requireNonNull(
                ruta,
                "La ruta es obligatoria."
        );

        this.repositorioPreguntas = Objects.requireNonNull(
                repositorioPreguntas,
                "El repositorio de preguntas es obligatorio."
        );

        this.url = "jdbc:sqlite:" + ruta.toAbsolutePath();
    }

    /**
     * Recupera preguntas pendientes de todos los autores.
     *
     * @param limite cantidad máxima de preguntas
     * @param desplazamiento registros que se deben omitir
     * @return preguntas pendientes encontradas
     */
    @Override
    public List<Pregunta> listarPendientes(
            int limite,
            int desplazamiento) {

        if (limite < 1 || desplazamiento < 0) {

            throw new IllegalArgumentException(
                    "Los parámetros de paginación son inválidos."
            );
        }

        String sql = """
                SELECT id
                FROM preguntas
                WHERE estado = ?
                ORDER BY rowid DESC
                LIMIT ? OFFSET ?
                """;

        List<String> identificadores = new ArrayList<>();

        try (Connection conexion =
                     DriverManager.getConnection(url);
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(
                    1,
                    EstadoPregunta.PENDIENTE_REVISION.name()
            );

            ps.setInt(2, limite);
            ps.setInt(3, desplazamiento);

            try (ResultSet resultado = ps.executeQuery()) {

                while (resultado.next()) {

                    identificadores.add(
                            resultado.getString("id")
                    );
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al consultar las preguntas pendientes.",
                    e
            );
        }

        List<Pregunta> preguntas = new ArrayList<>();

        for (String id : identificadores) {

            Pregunta pregunta = repositorioPreguntas
                    .buscarPorId(id)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "No se pudo recuperar la pregunta."
                            )
                    );

            preguntas.add(pregunta);
        }

        return List.copyOf(preguntas);
    }

    /**
     * Cuenta las preguntas pendientes de revisión.
     *
     * @return cantidad total de preguntas pendientes
     */
    @Override
    public long contarPendientes() {

        String sql = """
                SELECT COUNT(*) AS total
                FROM preguntas
                WHERE estado = ?
                """;

        try (Connection conexion =
                     DriverManager.getConnection(url);
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(
                    1,
                    EstadoPregunta.PENDIENTE_REVISION.name()
            );

            try (ResultSet resultado = ps.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getLong("total");
                }

                return 0;
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al contar las preguntas pendientes.",
                    e
            );
        }
    }
}
