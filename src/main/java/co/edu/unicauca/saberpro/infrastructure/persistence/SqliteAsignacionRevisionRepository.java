
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.repository.IAsignacionRevisionRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementa la persistencia de revisores y asignaciones
 * mediante SQLite.
 */
public class SqliteAsignacionRevisionRepository
        implements IAsignacionRevisionRepository {

    private final String url;

    /**
     * Inicializa el repositorio de asignaciones.
     *
     * @param ruta ubicación de la base de datos SQLite
     */
    public SqliteAsignacionRevisionRepository(Path ruta) {

        Objects.requireNonNull(
                ruta,
                "La ruta de la base de datos es obligatoria."
        );

        Path archivo = ruta.toAbsolutePath();

        try {

            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "No se pudo crear el directorio de datos.",
                    e
            );
        }

        this.url = "jdbc:sqlite:" + archivo;

        crearTablas();
    }

    /**
     * Abre una conexión con las claves foráneas habilitadas.
     *
     * @return conexión SQLite
     * @throws SQLException si falla la conexión
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
     * Crea las tablas de revisores y asignaciones.
     */
    private void crearTablas() {

        String tablaRevisores = """
                CREATE TABLE IF NOT EXISTS revisores (
                    id TEXT PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    correo TEXT NOT NULL UNIQUE COLLATE NOCASE
                )
                """;

        String tablaAsignaciones = """
                CREATE TABLE IF NOT EXISTS asignaciones_revision (
                    pregunta_id TEXT NOT NULL,
                    revisor_id TEXT NOT NULL,
                    administrador_id TEXT NOT NULL,
                    fecha_asignacion TEXT NOT NULL,

                    PRIMARY KEY (pregunta_id, revisor_id),

                    FOREIGN KEY (pregunta_id)
                        REFERENCES preguntas(id),

                    FOREIGN KEY (revisor_id)
                        REFERENCES revisores(id)
                )
                """;

        String indice = """
                CREATE INDEX IF NOT EXISTS
                    idx_asignaciones_revisor
                ON asignaciones_revision(revisor_id)
                """;

        try (Connection conexion = conectar();
             Statement sentencia = conexion.createStatement()) {

            sentencia.execute(tablaRevisores);
            sentencia.execute(tablaAsignaciones);
            sentencia.execute(indice);

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al crear las tablas de asignaciones.",
                    e
            );
        }
    }

    /**
     * Registra un docente revisor en SQLite.
     *
     * @param revisor docente que se desea registrar
     */
    @Override
    public void guardarRevisor(Revisor revisor) {

        Objects.requireNonNull(
                revisor,
                "El revisor es obligatorio."
        );

        String sql = """
                INSERT INTO revisores (
                    id, nombre, correo
                ) VALUES (?, ?, ?)
                """;

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, revisor.id());
            ps.setString(2, revisor.nombre());
            ps.setString(3, revisor.correo());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al registrar el revisor.",
                    e
            );
        }
    }

    /**
     * Busca un revisor mediante su identificador.
     *
     * @param revisorId identificador del revisor
     * @return revisor encontrado o resultado vacío
     */
    @Override
    public Optional<Revisor> buscarRevisorPorId(
            String revisorId) {

        if (revisorId == null || revisorId.isBlank()) {
            return Optional.empty();
        }

        String sql = """
                SELECT id, nombre, correo
                FROM revisores
                WHERE id = ?
                """;

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, revisorId);

            try (ResultSet resultado = ps.executeQuery()) {

                if (!resultado.next()) {
                    return Optional.empty();
                }

                Revisor revisor = new Revisor(
                        resultado.getString("id"),
                        resultado.getString("nombre"),
                        resultado.getString("correo")
                );

                return Optional.of(revisor);
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al buscar el revisor.",
                    e
            );
        }
    }

    /**
     * Recupera los revisores registrados.
     *
     * @return lista de docentes revisores
     */
    @Override
    public List<Revisor> listarRevisores() {

        String sql = """
                SELECT id, nombre, correo
                FROM revisores
                ORDER BY nombre COLLATE NOCASE, id
                """;

        List<Revisor> revisores = new ArrayList<>();

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet resultado = ps.executeQuery()) {

            while (resultado.next()) {

                revisores.add(
                        new Revisor(
                                resultado.getString("id"),
                                resultado.getString("nombre"),
                                resultado.getString("correo")
                        )
                );
            }

            return revisores;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al listar los revisores.",
                    e
            );
        }
    }

    /**
     * Registra la asignación de un revisor a una pregunta.
     *
     * Solo permite preguntas pendientes y evita duplicados.
     *
     * @param asignacion asignación que se desea guardar
     * @return true si se guardó una asignación nueva
     */
    @Override
    public boolean guardarAsignacion(
            AsignacionRevision asignacion) {

        Objects.requireNonNull(
                asignacion,
                "La asignación es obligatoria."
        );

        String sql = """
                INSERT OR IGNORE INTO asignaciones_revision (
                    pregunta_id,
                    revisor_id,
                    administrador_id,
                    fecha_asignacion
                )
                SELECT ?, ?, ?, ?
                FROM preguntas
                WHERE id = ?
                  AND estado = ?
                  AND EXISTS (
                      SELECT 1
                      FROM revisores
                      WHERE id = ?
                  )
                """;

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, asignacion.preguntaId());
            ps.setString(2, asignacion.revisorId());
            ps.setString(3, asignacion.administradorId());

            ps.setString(
                    4,
                    asignacion.fechaAsignacion().toString()
            );

            ps.setString(5, asignacion.preguntaId());

            ps.setString(
                    6,
                    EstadoPregunta.PENDIENTE_REVISION.name()
            );

            ps.setString(7, asignacion.revisorId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al asignar el revisor.",
                    e
            );
        }
    }

    /**
     * Consulta las asignaciones de una pregunta.
     *
     * @param preguntaId identificador de la pregunta
     * @return asignaciones encontradas
     */
    @Override
    public List<AsignacionRevision> listarAsignaciones(
            String preguntaId) {

        if (preguntaId == null || preguntaId.isBlank()) {
            return List.of();
        }

        String sql = """
                SELECT
                    pregunta_id,
                    revisor_id,
                    administrador_id,
                    fecha_asignacion
                FROM asignaciones_revision
                WHERE pregunta_id = ?
                ORDER BY fecha_asignacion, revisor_id
                """;

        List<AsignacionRevision> asignaciones =
                new ArrayList<>();

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, preguntaId);

            try (ResultSet resultado = ps.executeQuery()) {

                while (resultado.next()) {

                    AsignacionRevision asignacion =
                            new AsignacionRevision(
                                    resultado.getString("pregunta_id"),
                                    resultado.getString("revisor_id"),
                                    resultado.getString("administrador_id"),
                                    Instant.parse(
                                            resultado.getString(
                                                    "fecha_asignacion"
                                            )
                                    )
                            );

                    asignaciones.add(asignacion);
                }
            }

            return asignaciones;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al consultar las asignaciones.",
                    e
            );
        }
    }
}
