
package co.edu.unicauca.saberpro;

import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;
import co.edu.unicauca.saberpro.domain.repository.IPreguntaRepository;
import co.edu.unicauca.saberpro.domain.service.IPreguntaService;
import co.edu.unicauca.saberpro.domain.service.PreguntaServiceImpl;
import co.edu.unicauca.saberpro.domain.service.ServicioRegistroAutorizado;
import co.edu.unicauca.saberpro.domain.validation.ValidadorCamposObligatorios;
import co.edu.unicauca.saberpro.domain.validation.ValidadorOpciones;
import co.edu.unicauca.saberpro.infrastructure.persistence.SqlitePreguntaRepository;
import co.edu.unicauca.saberpro.presentation.controller.RegistroPreguntaController;
import co.edu.unicauca.saberpro.presentation.view.RegistroPreguntaView;

import java.awt.GraphicsEnvironment;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada del Banco de Preguntas Saber Pro.
 */
public class Main {

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
     * Configura las dependencias e inicia la aplicación.
     */
    private static void iniciarAplicacion() {

        try {

            IPreguntaRepository repositorio =
                    new SqlitePreguntaRepository(
                            Path.of("data", "banco_preguntas.db")
                    );

            IPreguntaService servicioBase =
                    new PreguntaServiceImpl(
                            repositorio,
                            List.of(
                                    new ValidadorCamposObligatorios(),
                                    new ValidadorOpciones()
                            )
                    );

            SesionUsuario sesion = new SesionUsuario(
                    "AUTOR_DEMO",
                    RolUsuario.AUTOR
            );

            IPreguntaService servicioAutorizado =
                    new ServicioRegistroAutorizado(
                            servicioBase,
                            sesion
                    );

            RegistroPreguntaController controlador =
                    new RegistroPreguntaController(
                            servicioAutorizado
                    );

            RegistroPreguntaView vista =
                    new RegistroPreguntaView(
                            controlador,
                            sesion.usuarioId()
                    );

            vista.setVisible(true);

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo iniciar la aplicacion: "
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
