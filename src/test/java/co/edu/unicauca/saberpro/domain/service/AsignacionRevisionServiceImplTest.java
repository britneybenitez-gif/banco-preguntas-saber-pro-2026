
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;
import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.repository.IAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.domain.repository.IEstadoPreguntaRepository;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del servicio de asignación de revisores.
 */
class AsignacionRevisionServiceImplTest {

    /**
     * Verifica que un administrador pueda asignar un revisor
     * a una pregunta pendiente de revisión.
     */
    @Test
    void administradorPuedeAsignarRevisor() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        Pregunta pregunta = crearPregunta(true);
        preguntas.pregunta = pregunta;

        asignaciones.guardarRevisor(crearRevisor("REV001"));

        IAsignacionRevisionService servicio = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        boolean resultado = servicio.asignarRevisor(
                pregunta.getId(),
                "REV001"
        );

        assertTrue(resultado);
        assertEquals(1, asignaciones.asignaciones.size());

        AsignacionRevision guardada =
                asignaciones.asignaciones.get(0);

        assertEquals(pregunta.getId(), guardada.preguntaId());
        assertEquals("REV001", guardada.revisorId());
        assertEquals("ADMIN001", guardada.administradorId());
        assertNotNull(guardada.fechaAsignacion());
    }

    /**
     * Verifica que autores y revisores no puedan
     * realizar asignaciones administrativas.
     */
    @Test
    void autorYRevisorNoPuedenAsignar() {

        for (RolUsuario rol : List.of(
                RolUsuario.AUTOR,
                RolUsuario.REVISOR)) {

            RepositorioAsignacionesFalso asignaciones =
                    new RepositorioAsignacionesFalso();

            RepositorioPreguntasFalso preguntas =
                    new RepositorioPreguntasFalso();

            IAsignacionRevisionService servicio =
                    crearServicio(asignaciones, preguntas, rol);

            assertThrows(
                    SecurityException.class,
                    () -> servicio.asignarRevisor(
                            "PREG001",
                            "REV001"
                    )
            );

            assertEquals(0, asignaciones.intentosGuardar);
        }
    }

    /**
     * Verifica que no puedan asignarse preguntas
     * que todavía estén en borrador.
     */
    @Test
    void noPuedeAsignarPreguntaEnBorrador() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        Pregunta pregunta = crearPregunta(false);
        preguntas.pregunta = pregunta;

        asignaciones.guardarRevisor(crearRevisor("REV001"));

        IAsignacionRevisionService servicio = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        assertThrows(
                IllegalStateException.class,
                () -> servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertEquals(0, asignaciones.intentosGuardar);
        assertTrue(asignaciones.asignaciones.isEmpty());
    }

    /**
     * Verifica que el servicio rechace preguntas
     * que no existen en el repositorio.
     */
    @Test
    void noPuedeAsignarPreguntaInexistente() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        asignaciones.guardarRevisor(crearRevisor("REV001"));

        IAsignacionRevisionService servicio = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.asignarRevisor(
                        "PREGUNTA_INEXISTENTE",
                        "REV001"
                )
        );

        assertEquals(0, asignaciones.intentosGuardar);
    }

    /**
     * Verifica que el docente deba estar registrado
     * antes de recibir una asignación.
     */
    @Test
    void noPuedeAsignarRevisorInexistente() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        Pregunta pregunta = crearPregunta(true);
        preguntas.pregunta = pregunta;

        IAsignacionRevisionService servicio = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV999"
                )
        );

        assertEquals(0, asignaciones.intentosGuardar);
    }

    /**
     * Verifica que no se asigne dos veces el mismo
     * revisor a una pregunta.
     */
    @Test
    void noPermiteAsignacionDuplicada() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        Pregunta pregunta = crearPregunta(true);
        preguntas.pregunta = pregunta;

        asignaciones.guardarRevisor(crearRevisor("REV001"));

        IAsignacionRevisionService servicio = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        assertTrue(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertFalse(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertEquals(1, asignaciones.asignaciones.size());
        assertEquals(2, asignaciones.intentosGuardar);
    }

    /**
     * Verifica que una pregunta pueda tener
     * diferentes revisores asignados.
     */
    @Test
    void permiteAsignarVariosRevisores() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        Pregunta pregunta = crearPregunta(true);
        preguntas.pregunta = pregunta;

        asignaciones.guardarRevisor(crearRevisor("REV001"));
        asignaciones.guardarRevisor(crearRevisor("REV002"));

        IAsignacionRevisionService servicio = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        assertTrue(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV001"
                )
        );

        assertTrue(
                servicio.asignarRevisor(
                        pregunta.getId(),
                        "REV002"
                )
        );

        assertEquals(
                2,
                servicio.listarAsignaciones(pregunta.getId()).size()
        );
    }

    /**
     * Verifica que solo el administrador pueda
     * consultar la lista de revisores disponibles.
     */
    @Test
    void soloAdministradorPuedeListarRevisores() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        asignaciones.guardarRevisor(crearRevisor("REV001"));

        IAsignacionRevisionService administrador = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        assertEquals(
                1,
                administrador.listarRevisores().size()
        );

        for (RolUsuario rol : List.of(
                RolUsuario.AUTOR,
                RolUsuario.REVISOR)) {

            IAsignacionRevisionService otroUsuario = crearServicio(
                    asignaciones,
                    preguntas,
                    rol
            );

            assertThrows(
                    SecurityException.class,
                    otroUsuario::listarRevisores
            );
        }
    }

    /**
     * Verifica que las asignaciones solo puedan
     * ser consultadas por administradores.
     */
    @Test
    void soloAdministradorPuedeConsultarAsignaciones() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        Pregunta pregunta = crearPregunta(true);
        preguntas.pregunta = pregunta;

        asignaciones.guardarRevisor(crearRevisor("REV001"));

        IAsignacionRevisionService administrador = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        administrador.asignarRevisor(
                pregunta.getId(),
                "REV001"
        );

        assertEquals(
                1,
                administrador.listarAsignaciones(
                        pregunta.getId()
                ).size()
        );

        for (RolUsuario rol : List.of(
                RolUsuario.AUTOR,
                RolUsuario.REVISOR)) {

            IAsignacionRevisionService otroUsuario = crearServicio(
                    asignaciones,
                    preguntas,
                    rol
            );

            assertThrows(
                    SecurityException.class,
                    () -> otroUsuario.listarAsignaciones(
                            pregunta.getId()
                    )
            );
        }
    }

    /**
     * Verifica que los identificadores sean obligatorios
     * para las operaciones de asignación.
     */
    @Test
    void rechazaIdentificadoresInvalidos() {

        RepositorioAsignacionesFalso asignaciones =
                new RepositorioAsignacionesFalso();

        RepositorioPreguntasFalso preguntas =
                new RepositorioPreguntasFalso();

        IAsignacionRevisionService servicio = crearServicio(
                asignaciones,
                preguntas,
                RolUsuario.ADMINISTRADOR
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.asignarRevisor(null, "REV001")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.asignarRevisor(" ", "REV001")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.asignarRevisor("PREG001", null)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.asignarRevisor("PREG001", " ")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.listarAsignaciones(null)
        );

        assertEquals(0, asignaciones.intentosGuardar);
    }

    /**
     * Construye el servicio con un rol determinado.
     *
     * @param asignaciones repositorio simulado de asignaciones
     * @param preguntas repositorio simulado de preguntas
     * @param rol rol utilizado en la prueba
     * @return servicio configurado
     */
    private IAsignacionRevisionService crearServicio(
            IAsignacionRevisionRepository asignaciones,
            IEstadoPreguntaRepository preguntas,
            RolUsuario rol) {

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN001",
                rol
        );

        return new AsignacionRevisionServiceImpl(
                asignaciones,
                preguntas,
                sesion
        );
    }

    /**
     * Construye una pregunta de prueba.
     *
     * @param pendiente true para enviarla a revisión
     * @return pregunta en el estado solicitado
     */
    private Pregunta crearPregunta(boolean pendiente) {

        Pregunta pregunta = new Pregunta(
                "AUTOR001",
                "Contexto académico de prueba",
                "¿Cuál es la opción correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación de prueba",
                "Bibliografía de prueba",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );

        if (pendiente) {
            pregunta.enviarARevision();
        }

        return pregunta;
    }

    /**
     * Construye un docente revisor de prueba.
     *
     * @param id identificador del docente
     * @return revisor válido
     */
    private Revisor crearRevisor(String id) {

        return new Revisor(
                id,
                "Docente " + id,
                id.toLowerCase() + "@unicauca.edu.co"
        );
    }

    /**
     * Simula el repositorio de preguntas.
     */
    private static class RepositorioPreguntasFalso
            implements IEstadoPreguntaRepository {

        private Pregunta pregunta;

        /**
         * Busca una pregunta en memoria.
         *
         * @param preguntaId identificador solicitado
         * @return pregunta encontrada o resultado vacío
         */
        @Override
        public Optional<Pregunta> buscarPorId(String preguntaId) {

            if (pregunta != null
                    && pregunta.getId().equals(preguntaId)) {

                return Optional.of(pregunta);
            }

            return Optional.empty();
        }

        /**
         * Operación no utilizada por el servicio de asignación.
         */
        @Override
        public boolean actualizarEstado(
                String preguntaId,
                String autorId,
                EstadoPregunta estadoAnterior,
                EstadoPregunta estadoNuevo) {

            throw new UnsupportedOperationException(
                    "Esta operación no corresponde a la prueba."
            );
        }
    }

    /**
     * Simula el repositorio de revisores y asignaciones.
     */
    private static class RepositorioAsignacionesFalso
            implements IAsignacionRevisionRepository {

        private final Map<String, Revisor> revisores =
                new HashMap<>();

        private final List<AsignacionRevision> asignaciones =
                new ArrayList<>();

        private int intentosGuardar;

        /**
         * Registra un revisor en memoria.
         */
        @Override
        public void guardarRevisor(Revisor revisor) {

            revisores.put(revisor.id(), revisor);
        }

        /**
         * Busca un revisor previamente registrado.
         */
        @Override
        public Optional<Revisor> buscarRevisorPorId(
                String revisorId) {

            return Optional.ofNullable(
                    revisores.get(revisorId)
            );
        }

        /**
         * Devuelve los revisores de prueba.
         */
        @Override
        public List<Revisor> listarRevisores() {

            return List.copyOf(revisores.values());
        }

        /**
         * Simula el guardado e impide asignaciones duplicadas.
         */
        @Override
        public boolean guardarAsignacion(
                AsignacionRevision asignacion) {

            intentosGuardar++;

            boolean existe = asignaciones.stream()
                    .anyMatch(actual ->
                            actual.preguntaId().equals(
                                    asignacion.preguntaId()
                            )
                            && actual.revisorId().equals(
                                    asignacion.revisorId()
                            )
                    );

            if (existe) {
                return false;
            }

            asignaciones.add(asignacion);
            return true;
        }

        /**
         * Consulta las asignaciones de una pregunta.
         */
        @Override
        public List<AsignacionRevision> listarAsignaciones(
                String preguntaId) {

            return asignaciones.stream()
                    .filter(asignacion ->
                            asignacion.preguntaId().equals(
                                    preguntaId
                            )
                    )
                    .toList();
        }
    }
}
