
package co.edu.unicauca.saberpro.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del modelo Revisor.
 */
class RevisorTest {

    /**
     * Verifica la creación de un revisor con datos válidos.
     */
    @Test
    void debeCrearRevisorValido() {

        Revisor revisor = new Revisor(
                "REV001",
                "Juan Perez",
                "juan@unicauca.edu.co"
        );

        assertEquals("REV001", revisor.id());
        assertEquals("Juan Perez", revisor.nombre());
        assertEquals(
                "juan@unicauca.edu.co",
                revisor.correo()
        );
    }

    /**
     * Verifica que el identificador sea obligatorio.
     */
    @Test
    void debeRechazarIdentificadorInvalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Revisor(
                        null,
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Revisor(
                        " ",
                        "Juan Perez",
                        "juan@unicauca.edu.co"
                )
        );
    }

    /**
     * Verifica que el nombre del revisor sea obligatorio.
     */
    @Test
    void debeRechazarNombreInvalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Revisor(
                        "REV001",
                        null,
                        "juan@unicauca.edu.co"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Revisor(
                        "REV001",
                        "",
                        "juan@unicauca.edu.co"
                )
        );
    }

    /**
     * Verifica que el correo tenga un formato válido.
     */
    @Test
    void debeRechazarCorreoInvalido() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Revisor(
                        "REV001",
                        "Juan Perez",
                        null
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Revisor(
                        "REV001",
                        "Juan Perez",
                        "correo-invalido"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Revisor(
                        "REV001",
                        "Juan Perez",
                        "juan@"
                )
        );
    }

    /**
     * Verifica la igualdad de revisores con los mismos datos.
     */
    @Test
    void debeCompararRevisoresPorSusDatos() {

        Revisor primero = new Revisor(
                "REV001",
                "Juan Perez",
                "juan@unicauca.edu.co"
        );

        Revisor segundo = new Revisor(
                "REV001",
                "Juan Perez",
                "juan@unicauca.edu.co"
        );

        assertEquals(primero, segundo);
        assertEquals(
                primero.hashCode(),
                segundo.hashCode()
        );
    }
}
