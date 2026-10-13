
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.NotificacionPendiente;
import co.edu.unicauca.saberpro.domain.repository.INotificacionRevisionRepository;

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

/**
 * Gestiona las notificaciones de revisión almacenadas en SQLite.
 *
 * Utiliza la tabla notificaciones_revision creada por
 * SqliteAsignacionRevisionRepository.
 */
public class SqliteNotificacionRevisionRepository
        implements INotificacionRevisionRepository {

    private final String url;

    /**
     * Inicializa el repositorio usando la base de datos compartida.
     *
     * @param ruta ruta del archivo SQLite
     */
    public SqliteNotificacionRevisionRepository(Path ruta) {

        Objects.requireNonNull(
                ruta,
                "La ruta de la base de datos es obligatoria."
        );

        this.url = "jdbc:sqlite:" + ruta.toAbsolutePath();
    }

    /**
     * Abre una conexión SQLite con claves foráneas habilitadas.
     *
     * @return conexión a la base de datos
     * @throws SQLException si ocurre un error de conexión
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
     * Recupera las notificaciones pendientes de envío.
     *
     * @param limite cantidad máxima de notificaciones
     * @return notificaciones pendientes
     */
    @Override
    public List<NotificacionPendiente> listarPendientes(
            int limite) {

        if (limite < 1 || limite > 1000) {

            throw new IllegalArgumentException(
                    "El límite debe estar entre 1 y 1000."
            );
        }

        String sql = """
                SELECT
                    n.pregunta_id,
                    n.revisor_id,
                    n.intentos
                FROM notificaciones_revision n
                INNER JOIN asignaciones_revision a
                    ON a.pregunta_id = n.pregunta_id
                   AND a.revisor_id = n.revisor_id
                WHERE n.estado = 'PENDIENTE'
                ORDER BY
                    a.fecha_asignacion,
                    n.pregunta_id,
                    n.revisor_id
                LIMIT ?
                """;

        List<NotificacionPendiente> pendientes =
                new ArrayList<>();

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try (ResultSet resultado = ps.executeQuery()) {

                while (resultado.next()) {

                    NotificacionPendiente notificacion =
                            new NotificacionPendiente(
                                    resultado.getString(
                                            "pregunta_id"
                                    ),
                                    resultado.getString(
                                            "revisor_id"
                                    ),
                                    resultado.getInt("intentos")
                            );

                    pendientes.add(notificacion);
                }
            }

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al consultar las notificaciones pendientes.",
                    e
            );
        }

        return List.copyOf(pendientes);
    }

    /**
     * Registra que una notificación fue enviada correctamente.
     *
     * Incrementa el número de intentos y elimina
     * cualquier error registrado anteriormente.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del destinatario
     * @return true si se actualizó una notificación pendiente
     */
    @Override
    public boolean marcarEnviada(
            String preguntaId,
            String revisorId) {

        validarIdentificadores(preguntaId, revisorId);

        String sql = """
                UPDATE notificaciones_revision
                SET estado = 'ENVIADA',
                    intentos = intentos + 1,
                    ultimo_error = NULL
                WHERE pregunta_id = ?
                  AND revisor_id = ?
                  AND estado = 'PENDIENTE'
                """;

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, preguntaId);
            ps.setString(2, revisorId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al marcar la notificación como enviada.",
                    e
            );
        }
    }

    /**
     * Registra un intento fallido y conserva
     * la notificación en estado PENDIENTE.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del destinatario
     * @param descripcion descripción segura del error
     * @return true si se actualizó correctamente
     */
    @Override
    public boolean registrarFallo(
            String preguntaId,
            String revisorId,
            String descripcion) {

        validarIdentificadores(preguntaId, revisorId);

        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException(
                    "La descripción del fallo es obligatoria."
            );
        }

        // Limita el texto almacenado para evitar errores extensos.
        String errorSeguro = descripcion.strip();

        if (errorSeguro.length() > 250) {
            errorSeguro = errorSeguro.substring(0, 250);
        }

        String sql = """
                UPDATE notificaciones_revision
                SET intentos = intentos + 1,
                    ultimo_error = ?
                WHERE pregunta_id = ?
                  AND revisor_id = ?
                  AND estado = 'PENDIENTE'
                """;

        try (Connection conexion = conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, errorSeguro);
            ps.setString(2, preguntaId);
            ps.setString(3, revisorId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {

            throw new IllegalStateException(
                    "Error al registrar el fallo de notificación.",
                    e
            );
        }
    }

    /**
     * Valida los identificadores usados en las operaciones.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del revisor
     */
    private void validarIdentificadores(
            String preguntaId,
            String revisorId) {

        if (preguntaId == null || preguntaId.isBlank()) {

            throw new IllegalArgumentException(
                    "El identificador de la pregunta es obligatorio."
            );
        }

        if (revisorId == null || revisorId.isBlank()) {

            throw new IllegalArgumentException(
                    "El identificador del revisor es obligatorio."
            );
        }
    }
}
