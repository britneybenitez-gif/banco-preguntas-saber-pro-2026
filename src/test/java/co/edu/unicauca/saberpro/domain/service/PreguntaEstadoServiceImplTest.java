
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;
import co.edu.unicauca.saberpro.domain.repository.IEstadoPreguntaRepository;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del servicio de cambio de estado.
 */
class PreguntaEstadoServiceImplTest {

    @Test
    void autorPuedeEnviarSuPreguntaARevision() {

        Pregunta pregunta = crearPregunta("AUTOR01");
        RepositorioFalso repositorio = new RepositorioFalso(pregunta);

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "AUTOR01",
                RolUsuario.AUTOR
        );

        List<String> errores = servicio.enviarARevision(
                pregunta.getId()
        );

        assertTrue(errores.isEmpty());
        assertEquals(
                EstadoPregunta.PENDIENTE_REVISION,
                pregunta.getEstado()
        );
        assertEquals(1, repositorio.actualizaciones);
    }

    @Test
    void administradorNoPuedeEnviarPreguntasARevision() {

        Pregunta pregunta = crearPregunta("ADMIN01");
        RepositorioFalso repositorio = new RepositorioFalso(pregunta);

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "ADMIN01",
                RolUsuario.ADMINISTRADOR
        );

        List<String> errores = servicio.enviarARevision(
                pregunta.getId()
        );

        assertEquals(
                List.of("Solo los autores pueden enviar preguntas a revisión."),
                errores
        );

        assertEquals(0, repositorio.consultas);
        assertEquals(0, repositorio.actualizaciones);
    }

    @Test
    void revisorNoPuedeEnviarPreguntasARevision() {

        Pregunta pregunta = crearPregunta("REVISOR01");
        RepositorioFalso repositorio = new RepositorioFalso(pregunta);

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "REVISOR01",
                RolUsuario.REVISOR
        );

        List<String> errores = servicio.enviarARevision(
                pregunta.getId()
        );

        assertEquals(
                List.of("Solo los autores pueden enviar preguntas a revisión."),
                errores
        );

        assertEquals(0, repositorio.actualizaciones);
    }

    @Test
    void noDebeAceptarIdentificadorVacioONulo() {

        RepositorioFalso repositorio =
                new RepositorioFalso(crearPregunta("AUTOR01"));

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "AUTOR01",
                RolUsuario.AUTOR
        );

        assertEquals(
                List.of("Debe seleccionar una pregunta."),
                servicio.enviarARevision("")
        );

        assertEquals(
                List.of("Debe seleccionar una pregunta."),
                servicio.enviarARevision(null)
        );

        assertEquals(0, repositorio.consultas);
        assertEquals(0, repositorio.actualizaciones);
    }

    @Test
    void noDebeEnviarPreguntaInexistente() {

        RepositorioFalso repositorio = new RepositorioFalso(null);

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "AUTOR01",
                RolUsuario.AUTOR
        );

        List<String> errores = servicio.enviarARevision("P999");

        assertEquals(
                List.of("La pregunta no existe."),
                errores
        );

        assertEquals(0, repositorio.actualizaciones);
    }

    @Test
    void autorNoPuedeEnviarPreguntaDeOtroAutor() {

        Pregunta pregunta = crearPregunta("AUTOR02");
        RepositorioFalso repositorio = new RepositorioFalso(pregunta);

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "AUTOR01",
                RolUsuario.AUTOR
        );

        List<String> errores = servicio.enviarARevision(
                pregunta.getId()
        );

        assertEquals(
                List.of(
                        "No puede enviar a revisión preguntas de otro autor."
                ),
                errores
        );

        assertEquals(EstadoPregunta.BORRADOR, pregunta.getEstado());
        assertEquals(0, repositorio.actualizaciones);
    }

    @Test
    void noDebeEnviarPreguntaQueYaEstaPendiente() {

        Pregunta pregunta = crearPregunta("AUTOR01");
        pregunta.enviarARevision();

        RepositorioFalso repositorio = new RepositorioFalso(pregunta);

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "AUTOR01",
                RolUsuario.AUTOR
        );

        List<String> errores = servicio.enviarARevision(
                pregunta.getId()
        );

        assertEquals(
                List.of(
                        "Solo las preguntas en BORRADOR pueden enviarse a revisión."
                ),
                errores
        );

        assertEquals(0, repositorio.actualizaciones);
    }

    @Test
    void noDebeConfirmarCuandoFallaLaActualizacion() {

        Pregunta pregunta = crearPregunta("AUTOR01");
        RepositorioFalso repositorio = new RepositorioFalso(pregunta);
        repositorio.permitirActualizacion = false;

        IPreguntaEstadoService servicio = crearServicio(
                repositorio,
                "AUTOR01",
                RolUsuario.AUTOR
        );

        List<String> errores = servicio.enviarARevision(
                pregunta.getId()
        );

        assertEquals(
                List.of(
                        "No fue posible actualizar el estado de la pregunta."
                ),
                errores
        );

        assertEquals(EstadoPregunta.BORRADOR, pregunta.getEstado());
        assertEquals(1, repositorio.actualizaciones);
    }

    /**
     * Construye el servicio con una sesión de prueba.
     */
    private IPreguntaEstadoService crearServicio(
            IEstadoPreguntaRepository repositorio,
            String usuarioId,
            RolUsuario rol) {

        return new PreguntaEstadoServiceImpl(
                repositorio,
                new SesionUsuario(usuarioId, rol)
        );
    }

    /**
     * Construye una pregunta válida.
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

    /**
     * Simula el repositorio para probar el servicio.
     */
    private static class RepositorioFalso
            implements IEstadoPreguntaRepository {

        private final Pregunta pregunta;
        private boolean permitirActualizacion = true;
        private int consultas;
        private int actualizaciones;

        RepositorioFalso(Pregunta pregunta) {
            this.pregunta = pregunta;
        }

        @Override
        public Optional<Pregunta> buscarPorId(String preguntaId) {

            consultas++;

            return Optional.ofNullable(pregunta)
                    .filter(p -> p.getId().equals(preguntaId));
        }

        @Override
        public boolean actualizarEstado(
                String preguntaId,
                String autorId,
                EstadoPregunta estadoAnterior,
                EstadoPregunta estadoNuevo) {

            actualizaciones++;

            return permitirActualizacion
                    && pregunta != null
                    && pregunta.getId().equals(preguntaId)
                    && pregunta.getAutorId().equals(autorId)
                    && pregunta.getEstado() == estadoAnterior
                    && estadoNuevo == EstadoPregunta.PENDIENTE_REVISION;
        }
    }
}
