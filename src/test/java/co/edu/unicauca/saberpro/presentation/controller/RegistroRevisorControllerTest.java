
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.service.IRegistroRevisorService;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del controlador de registro
 * de docentes revisores de HU-04.
 */
class RegistroRevisorControllerTest {

    /**
     * Verifica que el controlador delegue
     * correctamente los datos del docente.
     */
    @Test
    void debeDelegarRegistroAlServicio() {

        ServicioFalso servicio = new ServicioFalso();

        RegistroRevisorController controlador =
                new RegistroRevisorController(servicio);

        Revisor resultado = controlador.registrarRevisor(
                "REV001",
                "María López",
                "maria@example.com"
        );

        assertEquals("REV001", servicio.idRecibido);

        assertEquals(
                "María López",
                servicio.nombreRecibido
        );

        assertEquals(
                "maria@example.com",
                servicio.correoRecibido
        );

        assertEquals(1, servicio.llamadas);

        assertEquals("REV001", resultado.id());
    }

    /**
     * Verifica que el controlador devuelva
     * el mismo objeto generado por el servicio.
     */
    @Test
    void debeDevolverRevisorDelServicio() {

        ServicioFalso servicio = new ServicioFalso();

        Revisor esperado = new Revisor(
                "REV002",
                "Juan Perez",
                "juan@example.com"
        );

        servicio.respuesta = esperado;

        RegistroRevisorController controlador =
                new RegistroRevisorController(servicio);

        Revisor resultado = controlador.registrarRevisor(
                "REV002",
                "Juan Perez",
                "juan@example.com"
        );

        assertSame(esperado, resultado);
    }

    /**
     * Verifica que los errores de autorización
     * se propaguen hasta la interfaz.
     */
    @Test
    void debePropagarErrorDeAutorizacion() {

        ServicioFalso servicio = new ServicioFalso();

        servicio.error = new SecurityException(
                "Solo los administradores pueden registrar."
        );

        RegistroRevisorController controlador =
                new RegistroRevisorController(servicio);

        assertThrows(
                SecurityException.class,
                () -> controlador.registrarRevisor(
                        "REV001",
                        "María López",
                        "maria@example.com"
                )
        );
    }

    /**
     * Verifica que el controlador requiera
     * un servicio no nulo.
     */
    @Test
    void debeRechazarServicioNulo() {

        assertThrows(
                NullPointerException.class,
                () -> new RegistroRevisorController(null)
        );
    }

    /**
     * Simula el servicio de registro de revisores.
     */
    private static class ServicioFalso
            implements IRegistroRevisorService {

        private String idRecibido;
        private String nombreRecibido;
        private String correoRecibido;

        private int llamadas;

        private RuntimeException error;

        private Revisor respuesta;

        /**
         * Registra los parámetros y devuelve
         * un revisor simulado.
         */
        @Override
        public Revisor registrarRevisor(
                String id,
                String nombre,
                String correo) {

            llamadas++;

            idRecibido = id;
            nombreRecibido = nombre;
            correoRecibido = correo;

            if (error != null) {
                throw error;
            }

            if (respuesta != null) {
                return respuesta;
            }

            return new Revisor(
                    id,
                    nombre,
                    correo
            );
        }
    }
}
