
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqlitePreguntaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración del registro de revisores.
 *
 * Utiliza SQLite temporal para evitar modificar
 * la base de datos real de la aplicación.
 */
class RegistroRevisorServiceImplTest {

    @TempDir
    Path directorioTemporal;

    private Path rutaBaseDatos;

    private SqliteAsignacionRevisionRepository repositorio;

    private IRegistroRevisorService servicio;

    /**
     * Prepara la base de datos para cada prueba.
     */
    @BeforeEach
    void preparar() {

        rutaBaseDatos = directorioTemporal.resolve(
                "hu04-registro-revisores.db"
        );

        // Crear primero la estructura de preguntas.
        new SqlitePreguntaRepository(rutaBaseDatos);

        repositorio =
                new SqliteAsignacionRevisionRepository(
                        rutaBaseDatos
                );

        servicio = crearServicio(
                RolUsuario.ADMINISTRADOR
        );
    }

    /**
     * Prueba 1: el administrador puede registrar
     * un docente revisor válido.
     */
    @Test
    void administradorPuedeRegistrarRevisor() {

        Revisor registrado = servicio.registrarRevisor(
                "REV001",
                "María López",
                "maria@example.com"
        );

        assertEquals("REV001", registrado.id());
        assertEquals("María López", registrado.nombre());
        assertEquals("maria@example.com", registrado.correo());

        Revisor recuperado = repositorio
                .buscarRevisorPorId("REV001")
                .orElseThrow();

        assertEquals(registrado, recuperado);
    }

    /**
     * Prueba 2: autores y revisores no pueden
     * registrar docentes.
     */
    @Test
    void soloAdministradorPuedeRegistrar() {

        for (RolUsuario rol : List.of(
                RolUsuario.AUTOR,
                RolUsuario.REVISOR)) {

            IRegistroRevisorService otroServicio =
                    crearServicio(rol);

            assertThrows(
                    SecurityException.class,
                    () -> otroServicio.registrarRevisor(
                            "REV001",
                            "Juan Perez",
                            "juan@example.com"
                    )
            );
        }

        assertTrue(repositorio.listarRevisores().isEmpty());
    }

    /**
     * Prueba 3: el modelo debe rechazar
     * identificadores, nombres o correos inválidos.
     */
    @Test
    void rechazaDatosInvalidos() {

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarRevisor(
                        " ",
                        "Juan Perez",
                        "juan@example.com"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarRevisor(
                        "REV001",
                        "",
                        "juan@example.com"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarRevisor(
                        "REV001",
                        "Juan Perez",
                        "correo-invalido"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarRevisor(
                        null,
                        "Juan Perez",
                        "juan@example.com"
                )
        );

        assertTrue(repositorio.listarRevisores().isEmpty());
    }

    /**
     * Prueba 4: impide registrar dos revisores
     * con el mismo identificador.
     */
    @Test
    void noPermiteIdentificadorDuplicado() {

        servicio.registrarRevisor(
                "REV001",
                "Juan Perez",
                "juan@example.com"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarRevisor(
                        "REV001",
                        "Ana Lopez",
                        "ana@example.com"
                )
        );

        assertEquals(
                1,
                repositorio.listarRevisores().size()
        );
    }

    /**
     * Prueba 5: impide registrar correos duplicados,
     * independientemente de las mayúsculas.
     */
    @Test
    void noPermiteCorreoDuplicado() {

        servicio.registrarRevisor(
                "REV001",
                "Juan Perez",
                "juan@example.com"
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> servicio.registrarRevisor(
                        "REV002",
                        "Otro Docente",
                        "JUAN@EXAMPLE.COM"
                )
        );

        assertEquals(
                1,
                repositorio.listarRevisores().size()
        );
    }

    /**
     * Prueba 6: las dependencias del servicio
     * deben estar correctamente definidas.
     */
    @Test
    void rechazaDependenciasNulas() {

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN001",
                RolUsuario.ADMINISTRADOR
        );

        assertThrows(
                NullPointerException.class,
                () -> new RegistroRevisorServiceImpl(
                        null,
                        sesion
                )
        );

        assertThrows(
                NullPointerException.class,
                () -> new RegistroRevisorServiceImpl(
                        repositorio,
                        null
                )
        );
    }

    /**
     * Prueba 7: verifica que el revisor permanezca
     * guardado después de reabrir SQLite.
     */
    @Test
    void conservaRevisorEnSQLite() {

        servicio.registrarRevisor(
                " REV001 ",
                " María López ",
                " maria@example.com "
        );

        SqliteAsignacionRevisionRepository nuevoRepositorio =
                new SqliteAsignacionRevisionRepository(
                        rutaBaseDatos
                );

        Revisor recuperado = nuevoRepositorio
                .buscarRevisorPorId("REV001")
                .orElseThrow();

        assertEquals("REV001", recuperado.id());
        assertEquals("María López", recuperado.nombre());
        assertEquals("maria@example.com", recuperado.correo());
    }

    /**
     * Construye un servicio con el rol indicado.
     *
     * @param rol rol del usuario
     * @return servicio configurado
     */
    private IRegistroRevisorService crearServicio(
            RolUsuario rol) {

        SesionUsuario sesion = new SesionUsuario(
                "ADMIN001",
                rol
        );

        return new RegistroRevisorServiceImpl(
                repositorio,
                sesion
        );
    }
}
