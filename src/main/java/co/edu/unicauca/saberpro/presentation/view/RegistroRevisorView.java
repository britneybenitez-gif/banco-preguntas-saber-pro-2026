
package co.edu.unicauca.saberpro.presentation.view;

import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.presentation.controller.RegistroRevisorController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * Vista Swing para registrar docentes revisores.
 *
 * Corresponde a HU-04 del Banco de Preguntas Saber Pro.
 *
 * Permite al administrador registrar un docente
 * mediante su identificador, nombre y correo.
 */
public class RegistroRevisorView extends JDialog {

    private static final Color AZUL =
            new Color(23, 54, 93);

    private static final Color FONDO =
            new Color(244, 247, 251);

    private final RegistroRevisorController controlador;

    private final JTextField campoId =
            new JTextField(22);

    private final JTextField campoNombre =
            new JTextField(22);

    private final JTextField campoCorreo =
            new JTextField(22);

    private final JButton botonRegistrar =
            new JButton("Registrar docente");

    private final JButton botonCancelar =
            new JButton("Cancelar");

    private final JLabel etiquetaEstado =
            new JLabel("Complete los datos del docente.");

    private boolean registrando = false;

    /**
     * Construye la ventana para registrar docentes.
     *
     * @param ventanaPadre ventana administrativa de HU-04
     * @param controlador controlador del registro
     */
    public RegistroRevisorView(
            JFrame ventanaPadre,
            RegistroRevisorController controlador) {

        super(
                ventanaPadre,
                "Registrar docente revisor - HU-04",
                true
        );

        this.controlador = Objects.requireNonNull(
                controlador,
                "El controlador es obligatorio."
        );

        configurarVentana();
        construirInterfaz();
        configurarEventos();
    }

    /**
     * Configura el tamaño y comportamiento de la ventana.
     */
    private void configurarVentana() {

        setSize(540, 345);

        setMinimumSize(
                new Dimension(500, 330)
        );

        setLocationRelativeTo(getOwner());

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        getContentPane().setBackground(FONDO);

        setLayout(
                new BorderLayout(12, 12)
        );
    }

    /**
     * Construye los componentes del formulario.
     */
    private void construirInterfaz() {

        // ENCABEZADO
        JPanel encabezado =
                new JPanel(new BorderLayout());

        encabezado.setBackground(AZUL);

        encabezado.setBorder(
                BorderFactory.createEmptyBorder(
                        18, 22, 18, 22
                )
        );

        JLabel titulo = new JLabel(
                "HU-04 | Registrar docente revisor"
        );

        titulo.setForeground(Color.WHITE);

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        19
                )
        );

        encabezado.add(
                titulo,
                BorderLayout.CENTER
        );

        add(
                encabezado,
                BorderLayout.NORTH
        );

        // FORMULARIO
        JPanel formulario =
                new JPanel(
                        new GridLayout(3, 2, 12, 15)
                );

        formulario.setBackground(Color.WHITE);

        formulario.setBorder(
                BorderFactory.createEmptyBorder(
                        25, 25, 20, 25
                )
        );

        formulario.add(
                new JLabel("Identificador:")
        );

        formulario.add(campoId);

        formulario.add(
                new JLabel("Nombre completo:")
        );

        formulario.add(campoNombre);

        formulario.add(
                new JLabel("Correo electrónico:")
        );

        formulario.add(campoCorreo);

        add(
                formulario,
                BorderLayout.CENTER
        );

        // PANEL INFERIOR
        JPanel pie =
                new JPanel(new BorderLayout(8, 8));

        pie.setBackground(Color.WHITE);

        pie.setBorder(
                BorderFactory.createEmptyBorder(
                        8, 22, 18, 22
                )
        );

        pie.add(
                etiquetaEstado,
                BorderLayout.NORTH
        );

        JPanel panelBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        panelBotones.setOpaque(false);

        panelBotones.add(botonCancelar);
        panelBotones.add(botonRegistrar);

        pie.add(
                panelBotones,
                BorderLayout.SOUTH
        );

        add(
                pie,
                BorderLayout.SOUTH
        );

        getRootPane().setDefaultButton(
                botonRegistrar
        );
    }

    /**
     * Configura los eventos de los botones.
     */
    private void configurarEventos() {

        botonRegistrar.addActionListener(
                evento -> registrarDocente()
        );

        botonCancelar.addActionListener(
                evento -> dispose()
        );
    }

    /**
     * Obtiene los datos y solicita el registro
     * mediante el controlador MVC.
     *
     * Usa SwingWorker para no bloquear
     * la interfaz gráfica durante el registro.
     */
    private void registrarDocente() {

        if (registrando) {
            return;
        }

        // Obtener los datos del formulario.
        String id = campoId.getText().strip();

        String nombre = campoNombre.getText().strip();

        String correo = campoCorreo.getText().strip();

        // Validar campos obligatorios.
        if (id.isEmpty()
                || nombre.isEmpty()
                || correo.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Debe completar todos los campos.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        registrando = true;

        actualizarControles();

        etiquetaEstado.setText(
                "Guardando docente revisor..."
        );

        // Registrar en segundo plano.
        SwingWorker<Revisor, Void> trabajador =
                new SwingWorker<>() {

                    /**
                     * Ejecuta la operación de registro.
                     *
                     * @return docente registrado
                     */
                    @Override
                    protected Revisor doInBackground() {

                        return controlador.registrarRevisor(
                                id,
                                nombre,
                                correo
                        );
                    }

                    /**
                     * Presenta el resultado del registro.
                     */
                    @Override
                    protected void done() {

                        registrando = false;

                        actualizarControles();

                        try {

                            Revisor registrado = get();

                            JOptionPane.showMessageDialog(
                                    RegistroRevisorView.this,
                                    "Docente registrado correctamente.\n\n"
                                            + "ID: "
                                            + registrado.id()
                                            + "\nNombre: "
                                            + registrado.nombre()
                                            + "\nCorreo: "
                                            + registrado.correo(),
                                    "Registro exitoso",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            // Cerrar la ventana al registrar.
                            dispose();

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();

                            mostrarError(
                                    "El registro fue interrumpido."
                            );

                        } catch (ExecutionException e) {

                            Throwable causa = e.getCause();

                            String mensaje = causa == null
                                    ? "No se pudo registrar el docente."
                                    : causa.getMessage();

                            if (mensaje == null
                                    || mensaje.isBlank()) {

                                mensaje =
                                        "No se pudo registrar el docente.";
                            }

                            mostrarError(mensaje);
                        }
                    }
                };

        trabajador.execute();
    }

    /**
     * Habilita o deshabilita los controles
     * mientras se realiza el registro.
     */
    private void actualizarControles() {

        campoId.setEnabled(!registrando);

        campoNombre.setEnabled(!registrando);

        campoCorreo.setEnabled(!registrando);

        botonRegistrar.setEnabled(!registrando);

        botonCancelar.setEnabled(!registrando);

        setDefaultCloseOperation(
                registrando
                        ? JDialog.DO_NOTHING_ON_CLOSE
                        : JDialog.DISPOSE_ON_CLOSE
        );
    }

    /**
     * Muestra un mensaje de error al administrador.
     *
     * @param mensaje descripción del error
     */
    private void mostrarError(String mensaje) {

        etiquetaEstado.setText(
                "No se pudo registrar el docente."
        );

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error de registro",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
