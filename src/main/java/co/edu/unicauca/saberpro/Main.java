
package co.edu.unicauca.saberpro;

import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.domain.service.IPreguntaService;
import co.edu.unicauca.saberpro.domain.service.IPreguntaEstadoService;
import co.edu.unicauca.saberpro.domain.service.IConsultaPreguntaService;
import co.edu.unicauca.saberpro.domain.service.IAsignacionRevisionService;
import co.edu.unicauca.saberpro.domain.service.IConsultaPendientesRevisionService;
import co.edu.unicauca.saberpro.domain.service.IReintentoNotificacionesService;
import co.edu.unicauca.saberpro.domain.service.IRegistroRevisorService;
import co.edu.unicauca.saberpro.domain.service.INotificadorRevision;

import co.edu.unicauca.saberpro.domain.service.PreguntaServiceImpl;
import co.edu.unicauca.saberpro.domain.service.PreguntaEstadoServiceImpl;
import co.edu.unicauca.saberpro.domain.service.ConsultaPreguntaServiceImpl;
import co.edu.unicauca.saberpro.domain.service.ServicioRegistroAutorizado;
import co.edu.unicauca.saberpro.domain.service.AsignacionRevisionServiceImpl;
import co.edu.unicauca.saberpro.domain.service.ConsultaPendientesRevisionServiceImpl;
import co.edu.unicauca.saberpro.domain.service.ReintentoNotificacionesServiceImpl;
import co.edu.unicauca.saberpro.domain.service.RegistroRevisorServiceImpl;

import co.edu.unicauca.saberpro.domain.validation.ValidadorCamposObligatorios;
import co.edu.unicauca.saberpro.domain.validation.ValidadorOpciones;

import co.edu.unicauca.saberpro.infrastructure.persistence.SqlitePreguntaRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteAsignacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteNotificacionRevisionRepository;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqliteConsultaPendientesRevisionRepository;

import co.edu.unicauca.saberpro.infrastructure.notification.NotificadorRevisionEmail;
import co.edu.unicauca.saberpro.infrastructure.notification.NotificadorRevisionConRegistro;

import co.edu.unicauca.saberpro.presentation.controller.RegistroPreguntaController;
import co.edu.unicauca.saberpro.presentation.controller.PreguntaEstadoController;
import co.edu.unicauca.saberpro.presentation.controller.ConsultaPreguntaController;
import co.edu.unicauca.saberpro.presentation.controller.AsignacionRevisionController;
import co.edu.unicauca.saberpro.presentation.controller.RegistroRevisorController;

import co.edu.unicauca.saberpro.presentation.view.RegistroPreguntaView;
import co.edu.unicauca.saberpro.presentation.view.PreguntaEstadoView;
import co.edu.unicauca.saberpro.presentation.view.ConsultaPreguntaView;
import co.edu.unicauca.saberpro.presentation.view.AsignacionRevisionView;
import co.edu.unicauca.saberpro.presentation.view.RegistroRevisorView;

import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada del Banco de Preguntas Saber Pro.
 *
 * Integra las funcionalidades HU-01, HU-02, HU-03
 * y HU-04 mediante una interfaz gráfica Swing.
 */
public class Main {

    /**
     * Inicia la aplicación.
     *
     * @param args argumentos de ejecución
     */
    public static void main(String[] args) {

        if (GraphicsEnvironment.isHeadless()) {

            System.out.println(
                    "No hay un entorno gráfico disponible."
            );

            System.out.println(
                    "La interfaz Swing requiere un escritorio gráfico."
            );

            return;
        }

        SwingUtilities.invokeLater(
                Main::iniciarAplicacion
        );
    }

    /**
     * Presenta los perfiles demostrativos del sistema.
     *
     * La selección de perfiles es únicamente académica.
     * No reemplaza un mecanismo real de autenticación.
     */
    private static void iniciarAplicacion() {

        Object[] opciones = {
                "Autor (HU-01 a HU-03)",
                "Administrador (HU-04)",
                "Salir"
        };

        int seleccion = JOptionPane.showOptionDialog(
                null,
                "Seleccione el perfil para probar el sistema.\n\n"
                        + "Los perfiles son demostrativos.",
                "Banco de Preguntas Saber Pro",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion != 0 && seleccion != 1) {
            return;
        }

        try {

            // Base de datos compartida por todos los módulos.
            Path rutaBaseDatos = Path.of(
                    "data",
                    "banco_preguntas.db"
            );

            SqlitePreguntaRepository repositorioPreguntas =
                    new SqlitePreguntaRepository(
                            rutaBaseDatos
                    );

            if (seleccion == 0) {

                iniciarModuloAutor(
                        repositorioPreguntas
                );

            } else {

                iniciarModuloAdministrador(
                        repositorioPreguntas,
                        rutaBaseDatos
                );
            }

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo iniciar la aplicación:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Configura las funcionalidades HU-01, HU-02
     * y HU-03 correspondientes al autor.
     *
     * @param repositorio repositorio de preguntas
     */
    private static void iniciarModuloAutor(
            SqlitePreguntaRepository repositorio) {

        SesionUsuario sesionAutor =
                new SesionUsuario(
                        "AUTOR_DEMO",
                        RolUsuario.AUTOR
                );

        // HU-01: registro de preguntas.
        IPreguntaService servicioBase =
                new PreguntaServiceImpl(
                        repositorio,
                        List.of(
                                new ValidadorCamposObligatorios(),
                                new ValidadorOpciones()
                        )
                );

        IPreguntaService servicioRegistro =
                new ServicioRegistroAutorizado(
                        servicioBase,
                        sesionAutor
                );

        RegistroPreguntaController controladorRegistro =
                new RegistroPreguntaController(
                        servicioRegistro
                );

        // HU-02: enviar preguntas a revisión.
        IPreguntaEstadoService servicioEstado =
                new PreguntaEstadoServiceImpl(
                        repositorio,
                        sesionAutor
                );

        PreguntaEstadoController controladorEstado =
                new PreguntaEstadoController(
                        servicioEstado
                );

        // HU-03: consultar preguntas del autor.
        IConsultaPreguntaService servicioConsulta =
                new ConsultaPreguntaServiceImpl(
                        repositorio,
                        sesionAutor
                );

        ConsultaPreguntaController controladorConsulta =
                new ConsultaPreguntaController(
                        servicioConsulta
                );

        // Ventana principal del autor.
        RegistroPreguntaView vistaRegistro =
                new RegistroPreguntaView(
                        controladorRegistro,
                        sesionAutor.usuarioId()
                );

        JMenuBar barraMenu = new JMenuBar();

        JMenu menuPreguntas =
                new JMenu("Preguntas");

        JMenuItem opcionRevision = new JMenuItem(
                "Enviar a revisión (HU-02)"
        );

        opcionRevision.addActionListener(evento -> {

            PreguntaEstadoView vistaEstado =
                    new PreguntaEstadoView(
                            controladorEstado
                    );

            vistaEstado.setVisible(true);
        });

        JMenuItem opcionMisPreguntas =
                new JMenuItem(
                        "Mis preguntas (HU-03)"
                );

        opcionMisPreguntas.addActionListener(evento -> {

            ConsultaPreguntaView vistaConsulta =
                    new ConsultaPreguntaView(
                            controladorConsulta
                    );

            vistaConsulta.setVisible(true);
        });

        menuPreguntas.add(opcionRevision);
        menuPreguntas.add(opcionMisPreguntas);

        barraMenu.add(menuPreguntas);

        vistaRegistro.setJMenuBar(barraMenu);

        vistaRegistro.setVisible(true);
    }

    /**
     * Configura HU-04 para la gestión de revisores.
     *
     * @param repositorioPreguntas repositorio de preguntas
     * @param rutaBaseDatos base de datos compartida
     */
    private static void iniciarModuloAdministrador(
            SqlitePreguntaRepository repositorioPreguntas,
            Path rutaBaseDatos) {

        // Perfil demostrativo del administrador.
        SesionUsuario sesionAdministrador =
                new SesionUsuario(
                        "ADMIN_DEMO",
                        RolUsuario.ADMINISTRADOR
                );

        // Persistencia de revisores y asignaciones.
        SqliteAsignacionRevisionRepository
                repositorioAsignaciones =
                new SqliteAsignacionRevisionRepository(
                        rutaBaseDatos
                );

        // Persistencia del estado de los correos.
        SqliteNotificacionRevisionRepository
                repositorioNotificaciones =
                new SqliteNotificacionRevisionRepository(
                        rutaBaseDatos
                );

        // Consulta administrativa de preguntas pendientes.
        SqliteConsultaPendientesRevisionRepository
                repositorioPendientes =
                new SqliteConsultaPendientesRevisionRepository(
                        rutaBaseDatos,
                        repositorioPreguntas
                );

        // Notificador SMTP.
        INotificadorRevision notificadorBase =
                crearNotificadorCorreo();

        // Registra el resultado de los envíos en SQLite.
        INotificadorRevision notificadorConRegistro =
                new NotificadorRevisionConRegistro(
                        notificadorBase,
                        repositorioNotificaciones
                );

        // Servicio para asignar docentes revisores.
        IAsignacionRevisionService servicioAsignaciones =
                new AsignacionRevisionServiceImpl(
                        repositorioAsignaciones,
                        repositorioPreguntas,
                        sesionAdministrador,
                        notificadorConRegistro
                );

        // Servicio para consultar preguntas pendientes.
        IConsultaPendientesRevisionService servicioPendientes =
                new ConsultaPendientesRevisionServiceImpl(
                        repositorioPendientes,
                        sesionAdministrador
                );

        // Servicio para reintentar notificaciones.
        IReintentoNotificacionesService servicioReintentos =
                new ReintentoNotificacionesServiceImpl(
                        repositorioNotificaciones,
                        repositorioAsignaciones,
                        repositorioPreguntas,
                        notificadorConRegistro,
                        sesionAdministrador
                );

        // Servicio para registrar nuevos revisores.
        IRegistroRevisorService servicioRegistroRevisor =
                new RegistroRevisorServiceImpl(
                        repositorioAsignaciones,
                        sesionAdministrador
                );

        // Controlador principal de HU-04.
        AsignacionRevisionController controladorAsignaciones =
                new AsignacionRevisionController(
                        servicioAsignaciones,
                        servicioReintentos,
                        servicioPendientes
                );

        // Controlador de registro de docentes revisores.
        RegistroRevisorController controladorRegistroRevisor =
                new RegistroRevisorController(
                        servicioRegistroRevisor
                );

        // Ventana administrativa.
        AsignacionRevisionView vistaAdministrador =
                new AsignacionRevisionView(
                        controladorAsignaciones
                );

        // Menú para el administrador.
        JMenuBar barraMenu = new JMenuBar();

        JMenu menuRevisores =
                new JMenu("Gestión de revisores");

        JMenuItem opcionRegistrarRevisor =
                new JMenuItem(
                        "Registrar docente revisor (HU-04)"
                );

        // Abrir el formulario de registro de docentes.
        opcionRegistrarRevisor.addActionListener(evento -> {

            RegistroRevisorView vistaRegistroRevisor =
                    new RegistroRevisorView(
                            vistaAdministrador,
                            controladorRegistroRevisor
                    );

            // El formulario es modal.
            vistaRegistroRevisor.setVisible(true);

            // Actualizar los revisores disponibles.
            vistaAdministrador.recargarDatos();
        });

        menuRevisores.add(opcionRegistrarRevisor);

        barraMenu.add(menuRevisores);

        vistaAdministrador.setJMenuBar(barraMenu);

        // Mostrar la pantalla principal de HU-04.
        vistaAdministrador.setVisible(true);

        // Informar cuando no se ha configurado SMTP.
        if (!smtpConfigurado()) {

            JOptionPane.showMessageDialog(
                    vistaAdministrador,
                    "SMTP todavía no está configurado.\n\n"
                            + "Puede registrar revisores, consultar "
                            + "preguntas y guardar asignaciones.\n"
                            + "Los correos quedarán pendientes "
                            + "hasta configurar el servidor SMTP.",
                    "Correo no configurado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Crea el componente que envía notificaciones.
     *
     * Si SMTP no está configurado, el componente
     * informa el fallo sin simular envíos exitosos.
     *
     * @return notificador configurado
     */
    private static INotificadorRevision crearNotificadorCorreo() {

        if (smtpConfigurado()) {

            return NotificadorRevisionEmail.desdeEntorno();
        }

        return (revisor, pregunta) -> {

            throw new IllegalStateException(
                    "SMTP no está configurado. "
                            + "La notificación permanece pendiente."
            );
        };
    }

    /**
     * Verifica si están presentes las variables SMTP.
     *
     * @return true si existe la configuración mínima
     */
    private static boolean smtpConfigurado() {

        return variableConfigurada("SMTP_HOST")
                && variableConfigurada("SMTP_USER")
                && variableConfigurada("SMTP_PASSWORD");
    }

    /**
     * Comprueba una variable de entorno.
     *
     * @param nombre nombre de la variable
     * @return true si tiene un valor no vacío
     */
    private static boolean variableConfigurada(
            String nombre) {

        String valor = System.getenv(nombre);

        return valor != null && !valor.isBlank();
    }
}
