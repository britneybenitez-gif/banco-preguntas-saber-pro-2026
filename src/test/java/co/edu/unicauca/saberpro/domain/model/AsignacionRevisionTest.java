
package co.edu.unicauca.saberpro.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del modelo AsignacionRevision.
 */
class AsignacionRevisionTest {

    /**
     * Verifica la creación de una asignación válida.
     */
    @Test
    void debeCrearAsignacionValida() {

        Instant fecha = Instant.parse(
                "2026-10-12T15:00:00Z"
        );

        AsignacionRevision asignacion =
                new AsignacionRevision(
                        "PREG001",
                        "REV001",
                        "ADMIN001",
                        fecha
                );

        assertEquals(
                "PREG001",
                asignacion.preguntaId()
        );

        assertEquals(
                "REV001",
                asignacion.revisorId()
        );

        assertEquals(
                "ADMIN001",
                asignacion.administradorId()
        );

        assertEquals(
                fecha,
                asignacion.fechaAsignacion()
        );
    }

    /**
     * Verifica que el identificador de pregunta sea obligatorio.
     */
    @Test
    void debeRechazarPreguntaInvalida() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionRevision(
                        null,
                        "REV001",
                        "ADMIN001",
                        Instant.now()
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionRevision(
                        " ",
                        "REV001",
                        "ADMIN001",
                        Instant.now()
                )
        );
    }

    /**
     * Verifica que el revisor esté identificado.
     */
    @Test
    void debeRechazarRevisorInvalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionRevision(
                        "PREG001",
                        null,
                        "ADMIN001",
                        Instant.now()
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionRevision(
                        "PREG001",
                        "",
                        "ADMIN001",
                        Instant.now()
                )
        );
    }

    /**
     * Verifica que el administrador esté identificado.
     */
    @Test
    void debeRechazarAdministradorInvalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionRevision(
                        "PREG001",
                        "REV001",
                        null,
                        Instant.now()
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new AsignacionRevision(
                        "PREG001",
                        "REV001",
                        " ",
                        Instant.now()
                )
        );
    }

    /**
     * Verifica que la fecha de asignación sea obligatoria.
     */
    @Test
    void debeRechazarFechaNula() {

        assertThrows(
                NullPointerException.class,
                () -> new AsignacionRevision(
                        "PREG001",
                        "REV001",
                        "ADMIN001",
                        null
                )
        );
    }
}
