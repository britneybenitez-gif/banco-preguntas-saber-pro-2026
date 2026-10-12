
package co.edu.unicauca.saberpro;

import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.domain.service.IPreguntaService;
import co.edu.unicauca.saberpro.domain.service.IPreguntaEstadoService;
import co.edu.unicauca.saberpro.domain.service.IConsultaPreguntaService;
import co.edu.unicauca.saberpro.domain.service.PreguntaServiceImpl;
import co.edu.unicauca.saberpro.domain.service.PreguntaEstadoServiceImpl;
import co.edu.unicauca.saberpro.domain.service.ConsultaPreguntaServiceImpl;
import co.edu.unicauca.saberpro.domain.service.ServicioRegistroAutorizado;

import co.edu.unicauca.saberpro.domain.validation.ValidadorCamposObligatorios;
import co.edu.unicauca.saberpro.domain.validation.ValidadorOpciones;

import co.edu.unicauca.saberpro.infrastructure.persistence.SqlitePreguntaRepository;

import co.edu.unicauca.saberpro.presentation.controller.RegistroPreguntaController;
import co.edu.unicauca.saberpro.presentation.controller.PreguntaEstadoController;
import co.edu.unicauca.saberpro.presentation.controller.ConsultaPreguntaController;

import co.edu.unicauca.saberpro.presentation.view.RegistroPreguntaView;
import co.edu.unicauca.saberpro.presentation.view.PreguntaEstadoView;
import co.edu.unicauca.saberpro.presentation.view.ConsultaPreguntaView;

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
 */
public class Main {

    /**
     * Inicia la aplicación en un entorno gráfico.
     *
     * @param args argumentos de ejecución
     */
    public static void main(String[] args) {

        if (GraphicsEnvironment.isHeadless()) {

            System.out.println(
                    "No hay un entorno grafico disponible."
            );

            System.out.println(
                    "La interfaz Swing requiere un escritorio grafico."
            );

            return;
        }

        SwingUtilities.invokeLater(Main::iniciarAplicacion);
    }

    /**
     * Configura las dependencias y las vistas de la aplicación.
     */
    private static void iniciarAplicacion() {

        try {

            // Repositorio compartido por las tres historias de usuario.
            SqlitePreguntaRepository repositorio =
                    new SqlitePreguntaRepository(
                            Path.of("data", "banco_preguntas.db")
                    );

            // Sesión utilizada por HU-01, HU-02 y HU-03.
            SesionUsuario sesion = new SesionUsuario(
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
                            sesion
                    );

            RegistroPreguntaController controladorRegistro =
                    new RegistroPreguntaController(
                            servicioRegistro
                    );

            // HU-02: envío de preguntas a revisión.
            IPreguntaEstadoService servicioEstado =
                    new PreguntaEstadoServiceImpl(
                            repositorio,
                            sesion
                    );

            PreguntaEstadoController controladorEstado =
                    new PreguntaEstadoController(
                            servicioEstado
                    );

            // HU-03: consulta de preguntas del autor.
            IConsultaPreguntaService servicioConsulta =
                    new ConsultaPreguntaServiceImpl(
                            repositorio,
                            sesion
                    );

            ConsultaPreguntaController controladorConsulta =
                    new ConsultaPreguntaController(
                            servicioConsulta
                    );

            // Ventana principal de HU-01.
            RegistroPreguntaView vistaRegistro =
                    new RegistroPreguntaView(
                            controladorRegistro,
                            sesion.usuarioId()
                    );

            // Menú principal de preguntas.
            JMenuBar barraMenu = new JMenuBar();
            JMenu menuPreguntas = new JMenu("Preguntas");

            // Opción de HU-02.
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

            // Opción de HU-03.
            JMenuItem opcionMisPreguntas = new JMenuItem(
                    "Mis preguntas (HU-03)"
            );

            opcionMisPreguntas.addActionListener(evento -> {

                ConsultaPreguntaView vistaConsulta =
                        new ConsultaPreguntaView(
                                controladorConsulta
                        );

                vistaConsulta.setVisible(true);
            });

            // Agregar las opciones al menú.
            menuPreguntas.add(opcionRevision);
            menuPreguntas.add(opcionMisPreguntas);

            barraMenu.add(menuPreguntas);

            vistaRegistro.setJMenuBar(barraMenu);
            vistaRegistro.setVisible(true);

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo iniciar la aplicación: "
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
