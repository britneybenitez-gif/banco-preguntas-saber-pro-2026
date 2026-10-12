
package co.edu.unicauca.saberpro.infrastructure.persistence;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de persistencia del repositorio SQLite.
 */
class SqlitePreguntaRepositoryTest {

    @TempDir
    Path directorioTemporal;

    @Test
    void debeGuardarPreguntaConDistractores() throws SQLException {

        Path ruta = directorioTemporal.resolve("preguntas.db");
        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        repositorio.guardar(crearPregunta(List.of("A", "B", "C", "D")));

        assertEquals(1, contarRegistros(ruta, "preguntas"));
        assertEquals(4, contarRegistros(ruta, "distractores"));
    }

    @Test
    void debeConservarDatosGuardados() throws SQLException {

        Path ruta = directorioTemporal.resolve("datos.db");

        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        repositorio.guardar(crearPregunta(List.of("A", "B", "C", "D")));

        new SqlitePreguntaRepository(ruta);

        assertEquals(1, contarRegistros(ruta, "preguntas"));
        assertEquals(4, contarRegistros(ruta, "distractores"));
    }

    @Test
    void debeRevertirTransaccionSiOcurreError() throws SQLException {

        Path ruta = directorioTemporal.resolve("error.db");

        SqlitePreguntaRepository repositorio =
                new SqlitePreguntaRepository(ruta);

        Pregunta pregunta = crearPregunta(
                Arrays.asList("A", null, "C", "D")
        );

        assertThrows(
                IllegalStateException.class,
                () -> repositorio.guardar(pregunta)
        );

        assertEquals(0, contarRegistros(ruta, "preguntas"));
        assertEquals(0, contarRegistros(ruta, "distractores"));
    }

    private Pregunta crearPregunta(List<String> distractores) {

        return new Pregunta(
                "AUTOR01",
                "Contexto de prueba",
                "¿Cuál es la respuesta correcta?",
                distractores,
                "E",
                "Justificación",
                "Bibliografía",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );
    }

    private int contarRegistros(Path ruta, String tabla)
            throws SQLException {

        String url = "jdbc:sqlite:" + ruta.toAbsolutePath();

        try (Connection conexion = DriverManager.getConnection(url);
             Statement sentencia = conexion.createStatement();
             ResultSet resultado = sentencia.executeQuery(
                     "SELECT COUNT(*) FROM " + tabla)) {

            return resultado.getInt(1);
        }
    }
}
