
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas del control de autorización de preguntas.
 */
class ServicioRegistroAutorizadoTest {

    @Test
    void autorPuedeRegistrarSuPropiaPregunta() {

        AtomicInteger llamadas = new AtomicInteger();

        IPreguntaService servicioBase = pregunta -> {
            llamadas.incrementAndGet();
            return List.of();
        };

        SesionUsuario sesion = new SesionUsuario(
                "AUTOR01",
                RolUsuario.AUTOR
        );

        ServicioRegistroAutorizado servicio =
                new ServicioRegistroAutorizado(
                        servicioBase,
                        sesion
                );

        List<String> errores = servicio.registrarPregunta(
                crearPregunta("AUTOR01")
        );

        assertTrue(errores.isEmpty());
        assertEquals(1, llamadas.get());
    }

    @Test
    void administradorNoPuedeRegistrarPregunta() {

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN01",
                RolUsuario.ADMINISTRADOR
        );

        ServicioRegistroAutorizado servicio =
                new ServicioRegistroAutorizado(
                        servicioQueNoDebeEjecutarse(),
                        sesion
                );

        List<String> errores = servicio.registrarPregunta(
                crearPregunta("ADMIN01")
        );

        assertEquals(
                List.of("Solo los autores pueden registrar preguntas."),
                errores
        );
    }

    @Test
    void revisorNoPuedeRegistrarPregunta() {

        SesionUsuario sesion = new SesionUsuario(
                "REVISOR01",
                RolUsuario.REVISOR
        );

        ServicioRegistroAutorizado servicio =
                new ServicioRegistroAutorizado(
                        servicioQueNoDebeEjecutarse(),
                        sesion
                );

        List<String> errores = servicio.registrarPregunta(
                crearPregunta("REVISOR01")
        );

        assertEquals(
                List.of("Solo los autores pueden registrar preguntas."),
                errores
        );
    }

    @Test
    void autorNoPuedeRegistrarPreguntaDeOtroAutor() {

        SesionUsuario sesion = new SesionUsuario(
                "AUTOR01",
                RolUsuario.AUTOR
        );

        ServicioRegistroAutorizado servicio =
                new ServicioRegistroAutorizado(
                        servicioQueNoDebeEjecutarse(),
                        sesion
                );

        List<String> errores = servicio.registrarPregunta(
                crearPregunta("AUTOR02")
        );

        assertEquals(
                List.of(
                        "No puede registrar preguntas a nombre de otro autor."
                ),
                errores
        );
    }

    @Test
    void preguntaNulaDebeDevolverError() {

        IPreguntaService servicioBase = pregunta -> {

            if (pregunta == null) {
                return List.of("La pregunta no puede ser nula.");
            }

            return List.of();
        };

        SesionUsuario sesion = new SesionUsuario(
                "AUTOR01",
                RolUsuario.AUTOR
        );

        ServicioRegistroAutorizado servicio =
                new ServicioRegistroAutorizado(
                        servicioBase,
                        sesion
                );

        List<String> errores = servicio.registrarPregunta(null);

        assertEquals(
                List.of("La pregunta no puede ser nula."),
                errores
        );
    }

    /**
     * Simula un servicio que nunca debe ser invocado.
     */
    private IPreguntaService servicioQueNoDebeEjecutarse() {

        return pregunta -> {
            fail("El registro no debía llegar al servicio base.");
            return List.of();
        };
    }

    /**
     * Construye una pregunta válida de prueba.
     */
    private Pregunta crearPregunta(String autorId) {

        return new Pregunta(
                autorId,
                "Contexto de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación",
                "Bibliografía",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );
    }
}
