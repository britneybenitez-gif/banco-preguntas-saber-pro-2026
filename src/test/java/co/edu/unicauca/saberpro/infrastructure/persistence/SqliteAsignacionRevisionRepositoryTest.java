
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de persistencia de revisores y asignaciones en SQLite.
 */
class SqliteAsignacionRevisionRepositoryTest {

    @TempDir
    Path directorioTemporal;

    private Path rutaBaseDatos;
    private SqlitePreguntaRepository repositorioPreguntas;
    private SqliteAsignacionRevisionRepository repositorioAsignaciones;

    /**
     * Prepara una base de datos independiente para cada prueba.
     */
    @BeforeEach
    void preparar() {

        rutaBaseDatos = directorioTemporal.resolve("hu04.db");

        repositorioPreguntas =
                new SqlitePreguntaRepository(rutaBaseDatos);

        repositorioAsignaciones =
                new SqliteAsignacionRevisionRepository(
                        rutaBaseDatos
                );
    }

    /**
     * Verifica el registro y recuperación de un revisor.
     */
    @Test
    void debeGuardarYBuscarRevisor() {

        Revisor original = crearRevisor(
                "REV001",
                "Juan Perez",
                "juan@unicauca.edu.co"
        );

        repositorioAsignaciones.guardarRevisor(original);

        Revisor recuperado = repositorioAsignaciones
                .buscarRevisorPorId("REV001")
                .orElseThrow();

        assertEquals(original, recuperado);
    }

    /**
     * Verifica que los revisores se listen por nombre.
     */
    @Test
    void debeListarRevisoresOrdenados() {

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV001",
                        "Zuleima Gomez",
                        "zuleima@unicauca.edu.co"
                )
        );

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV002",
                        "Ana Lopez",
                        "ana@unicauca.edu.co"
                )
        );

        List<Revisor> revisores =
                repositorioAsignaciones.listarRevisores();

        assertEquals(2, revisores.size());
        assertEquals("Ana Lopez", revisores.get(0).nombre());
        assertEquals("Zuleima Gomez", revisores.get(1).nombre());
    }

    /**
     * Verifica que un revisor inexistente no se recupere.
     */
    @Test
    void debeRetornarVacioSiRevisorNoExiste() {

        assertTrue(
                repositorioAsignaciones
                        .buscarRevisorPorId("REV999")
                        .isEmpty()
        );

        assertTrue(
                repositorioAsignaciones
                        .buscarRevisorPorId(null)
                        .isEmpty()
        );

        assertTrue(
                repositorioAsignaciones
                        .listarRevisores()
                        .isEmpty()
        );
    }

    /**
     * Verifica que no se registren correos duplicados.
     */
    @Test
    void debeRechazarCorreoDuplicado() {

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV001",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        assertThrows(
                IllegalStateException.class,
                () -> repositorioAsignaciones.guardarRevisor(
                        crearRevisor(
                                "REV002",
                                "Otro Docente",
                                "juan@unicauca.edu.co"
                        )
                )
        );

        assertEquals(
                1,
                repositorioAsignaciones.listarRevisores().size()
        );
    }

    /**
     * Verifica que una asignación válida permanezca en SQLite.
     */
    @Test
    void debeGuardarYRecuperarAsignacion() {

        Pregunta pregunta = crearPregunta(true);

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV001",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        AsignacionRevision asignacion = crearAsignacion(
                pregunta.getId(),
                "REV001"
        );

        boolean guardada =
                repositorioAsignaciones.guardarAsignacion(
                        asignacion
                );

        assertTrue(guardada);

        SqliteAsignacionRevisionRepository nuevoRepositorio =
                new SqliteAsignacionRevisionRepository(
                        rutaBaseDatos
                );

        List<AsignacionRevision> recuperadas =
                nuevoRepositorio.listarAsignaciones(
                        pregunta.getId()
                );

        assertEquals(1, recuperadas.size());
        assertEquals(asignacion, recuperadas.get(0));
    }

    /**
     * Verifica que un revisor no se asigne dos veces.
     */
    @Test
    void debeEvitarAsignacionDuplicada() {

        Pregunta pregunta = crearPregunta(true);

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV001",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        AsignacionRevision asignacion = crearAsignacion(
                pregunta.getId(),
                "REV001"
        );

        assertTrue(
                repositorioAsignaciones.guardarAsignacion(
                        asignacion
                )
        );

        assertFalse(
                repositorioAsignaciones.guardarAsignacion(
                        asignacion
                )
        );

        assertEquals(
                1,
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .size()
        );
    }

    /**
     * Verifica que no se asignen preguntas en borrador.
     */
    @Test
    void debeRechazarPreguntaEnBorrador() {

        Pregunta pregunta = crearPregunta(false);

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV001",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        boolean guardada =
                repositorioAsignaciones.guardarAsignacion(
                        crearAsignacion(
                                pregunta.getId(),
                                "REV001"
                        )
                );

        assertFalse(guardada);

        assertTrue(
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .isEmpty()
        );
    }

    /**
     * Verifica que el revisor deba estar registrado.
     */
    @Test
    void debeRechazarRevisorInexistente() {

        Pregunta pregunta = crearPregunta(true);

        boolean guardada =
                repositorioAsignaciones.guardarAsignacion(
                        crearAsignacion(
                                pregunta.getId(),
                                "REV999"
                        )
                );

        assertFalse(guardada);

        assertTrue(
                repositorioAsignaciones
                        .listarAsignaciones(pregunta.getId())
                        .isEmpty()
        );
    }

    /**
     * Verifica que la pregunta deba existir en SQLite.
     */
    @Test
    void debeRechazarPreguntaInexistente() {

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV001",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        boolean guardada =
                repositorioAsignaciones.guardarAsignacion(
                        crearAsignacion(
                                "PREGUNTA_INEXISTENTE",
                                "REV001"
                        )
                );

        assertFalse(guardada);

        assertTrue(
                repositorioAsignaciones
                        .listarAsignaciones("PREGUNTA_INEXISTENTE")
                        .isEmpty()
        );
    }

    /**
     * Verifica que una pregunta pueda tener varios revisores.
     */
    @Test
    void debePermitirVariosRevisores() {

        Pregunta pregunta = crearPregunta(true);

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV001",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        repositorioAsignaciones.guardarRevisor(
                crearRevisor(
                        "REV002",
                        "Ana Lopez",
                        "ana@unicauca.edu.co"
                )
        );

        assertTrue(
                repositorioAsignaciones.guardarAsignacion(
                        crearAsignacion(
                                pregunta.getId(),
                                "REV001"
                        )
                )
        );

        assertTrue(
                repositorioAsignaciones.guardarAsignacion(
                        crearAsignacion(
                                pregunta.getId(),
                                "REV002"
                        )
                )
        );

        List<AsignacionRevision> asignaciones =
                repositorioAsignaciones.listarAsignaciones(
                        pregunta.getId()
                );

        assertEquals(2, asignaciones.size());

        assertTrue(
                asignaciones.stream()
                        .anyMatch(a ->
                                a.revisorId().equals("REV001")
                        )
        );

        assertTrue(
                asignaciones.stream()
                        .anyMatch(a ->
                                a.revisorId().equals("REV002")
                        )
        );
    }

    /**
     * Construye un revisor con datos de prueba.
     */
    private Revisor crearRevisor(
            String id,
            String nombre,
            String correo) {

        return new Revisor(id, nombre, correo);
    }

    /**
     * Registra una pregunta y opcionalmente la envía a revisión.
     *
     * @param pendiente indica si debe quedar pendiente de revisión
     * @return pregunta registrada
     */
    private Pregunta crearPregunta(boolean pendiente) {

        Pregunta pregunta = new Pregunta(
                "AUTOR01",
                "Contexto de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación académica",
                "Bibliografía de prueba",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );

        repositorioPreguntas.guardar(pregunta);

        if (pendiente) {

            boolean actualizado =
                    repositorioPreguntas.actualizarEstado(
                            pregunta.getId(),
                            pregunta.getAutorId(),
                            EstadoPregunta.BORRADOR,
                            EstadoPregunta.PENDIENTE_REVISION
                    );

            assertTrue(actualizado);
        }

        return pregunta;
    }

    /**
     * Construye una asignación válida de prueba.
     */
    private AsignacionRevision crearAsignacion(
            String preguntaId,
            String revisorId) {

        return new AsignacionRevision(
                preguntaId,
                revisorId,
                "ADMIN001",
                Instant.parse("2026-10-12T15:00:00Z")
        );
    }
    
/**
 * Verifica que una asignación nueva cree automáticamente
 * una notificación pendiente mediante el trigger de SQLite.
 *
 * @throws SQLException si ocurre un error de consulta
 */
@Test
void debeCrearNotificacionPendienteAlAsignar()
        throws SQLException {

    Pregunta pregunta = crearPregunta(true);

    Revisor revisor = crearRevisor(
            "REV001",
            "Juan Perez",
            "juan@unicauca.edu.co"
    );

    repositorioAsignaciones.guardarRevisor(revisor);

    AsignacionRevision asignacion = crearAsignacion(
            pregunta.getId(),
            "REV001"
    );

    assertTrue(
            repositorioAsignaciones.guardarAsignacion(
                    asignacion
            )
    );

    String sql = """
            SELECT estado, intentos
            FROM notificaciones_revision
            WHERE pregunta_id = ?
              AND revisor_id = ?
            """;

    String url = "jdbc:sqlite:"
            + rutaBaseDatos.toAbsolutePath();

    try (Connection conexion =
                 DriverManager.getConnection(url);
         PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

        ps.setString(1, pregunta.getId());
        ps.setString(2, "REV001");

        try (ResultSet resultado = ps.executeQuery()) {

            assertTrue(resultado.next());

            assertEquals(
                    "PENDIENTE",
                    resultado.getString("estado")
            );

            assertEquals(
                    0,
                    resultado.getInt("intentos")
            );

            assertFalse(resultado.next());
        }
    }
}

/**
 * Verifica que intentar repetir una asignación
 * no produzca una segunda notificación.
 *
 * @throws SQLException si ocurre un error de consulta
 */
@Test
void asignacionDuplicadaNoDuplicaNotificacion()
        throws SQLException {

    Pregunta pregunta = crearPregunta(true);

    Revisor revisor = crearRevisor(
            "REV001",
            "Juan Perez",
            "juan@unicauca.edu.co"
    );

    repositorioAsignaciones.guardarRevisor(revisor);

    AsignacionRevision asignacion = crearAsignacion(
            pregunta.getId(),
            "REV001"
    );

    assertTrue(
            repositorioAsignaciones.guardarAsignacion(
                    asignacion
            )
    );

    assertFalse(
            repositorioAsignaciones.guardarAsignacion(
                    asignacion
            )
    );

    String sql = """
            SELECT COUNT(*) AS total
            FROM notificaciones_revision
            WHERE pregunta_id = ?
              AND revisor_id = ?
            """;

    String url = "jdbc:sqlite:"
            + rutaBaseDatos.toAbsolutePath();

    try (Connection conexion =
                 DriverManager.getConnection(url);
         PreparedStatement ps =
                 conexion.prepareStatement(sql)) {

        ps.setString(1, pregunta.getId());
        ps.setString(2, "REV001");

        try (ResultSet resultado = ps.executeQuery()) {

            assertTrue(resultado.next());

            assertEquals(
                    1,
                    resultado.getInt("total")
            );
        }
    }
}

}
