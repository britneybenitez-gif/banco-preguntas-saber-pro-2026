
package co.edu.unicauca.saberpro.presentation.view;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.presentation.controller.PreguntaEstadoController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Interfaz para consultar y enviar preguntas a revisión.
 */
public class PreguntaEstadoView extends JFrame {

    private static final Color AZUL = new Color(23, 54, 93);
    private static final Color FONDO = new Color(244, 247, 251);
    private static final Color TEXTO = new Color(40, 49, 61);

    private final PreguntaEstadoController controlador;

    private final JTextField campoId = new JTextField(32);

    private final JTextArea campoPregunta =
            new JTextArea(5, 40);

    private final JLabel etiquetaEstado =
            new JLabel("SIN CONSULTAR");

    private final JButton botonBuscar =
            new JButton("Buscar pregunta");

    private final JButton botonEnviar =
            new JButton("Enviar a revisión");

    private String idSeleccionado;
    private EstadoPregunta estadoActual;
    private boolean ocupado;

    /**
     * Inicializa la ventana con su controlador.
     *
     * @param controlador controlador de la HU-02
     */
    public PreguntaEstadoView(
            PreguntaEstadoController controlador) {

        this.controlador = Objects.requireNonNull(
                controlador,
                "El controlador es obligatorio."
        );

        configurarVentana();
        construirInterfaz();
        configurarEventos();
        actualizarBotones();
    }

    /**
     * Configura las propiedades generales de la ventana.
     */
    private void configurarVentana() {

        setTitle("Banco de Preguntas Saber Pro - HU-02");
        setSize(750, 470);
        setMinimumSize(new Dimension(620, 400));
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout(15, 15));
    }

    /**
     * Construye los componentes visuales.
     */
    private void construirInterfaz() {

        JPanel encabezado = new JPanel(
                new BorderLayout()
        );

        encabezado.setBackground(AZUL);
        encabezado.setBorder(
                BorderFactory.createEmptyBorder(22, 25, 22, 25)
        );

        JLabel titulo = new JLabel(
                "HU-02 | Enviar preguntas a revisión"
        );

        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font(
                "SansSerif", Font.BOLD, 20
        ));

        encabezado.add(titulo, BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        JPanel contenido = new JPanel(
                new BorderLayout(12, 18)
        );

        contenido.setBackground(FONDO);
        contenido.setBorder(
                BorderFactory.createEmptyBorder(22, 25, 10, 25)
        );

        JPanel busqueda = new JPanel(
                new BorderLayout(8, 10)
        );

        busqueda.setOpaque(false);

        JLabel instruccion = new JLabel(
                "Ingrese el identificador de la pregunta:"
        );

        instruccion.setForeground(TEXTO);

        busqueda.add(
                instruccion, BorderLayout.NORTH
        );

        JPanel filaBusqueda = new JPanel(
                new BorderLayout(10, 0)
        );

        filaBusqueda.setOpaque(false);

        filaBusqueda.add(
                campoId, BorderLayout.CENTER
        );

        filaBusqueda.add(
                botonBuscar, BorderLayout.EAST
        );

        busqueda.add(
                filaBusqueda, BorderLayout.CENTER
        );

        contenido.add(
                busqueda, BorderLayout.NORTH
        );

        JPanel detalle = new JPanel(
                new BorderLayout(10, 12)
        );

        detalle.setBackground(Color.WHITE);
        detalle.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 226, 233)
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 18, 15, 18
                        )
                )
        );

        JLabel tituloDetalle = new JLabel(
                "Pregunta seleccionada"
        );

        tituloDetalle.setFont(new Font(
                "SansSerif", Font.BOLD, 15
        ));

        tituloDetalle.setForeground(AZUL);

        detalle.add(
                tituloDetalle, BorderLayout.NORTH
        );

        campoPregunta.setEditable(false);
        campoPregunta.setLineWrap(true);
        campoPregunta.setWrapStyleWord(true);
        campoPregunta.setFont(new Font(
                "SansSerif", Font.PLAIN, 14
        ));

        campoPregunta.setText(
                "Busca una pregunta para consultar su estado."
        );

        detalle.add(
                new JScrollPane(campoPregunta),
                BorderLayout.CENTER
        );

        JPanel panelEstado = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );

        panelEstado.setOpaque(false);

        panelEstado.add(
                new JLabel("Estado actual:")
        );

        etiquetaEstado.setFont(new Font(
                "SansSerif", Font.BOLD, 13
        ));

        panelEstado.add(etiquetaEstado);

        detalle.add(
                panelEstado, BorderLayout.SOUTH
        );

        contenido.add(
                detalle, BorderLayout.CENTER
        );

        add(contenido, BorderLayout.CENTER);

        JPanel pie = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 20, 15)
        );

        pie.setBackground(Color.WHITE);

        botonEnviar.setBackground(AZUL);
        botonEnviar.setForeground(Color.WHITE);
        botonEnviar.setFocusPainted(false);

        pie.add(botonEnviar);

        add(pie, BorderLayout.SOUTH);
    }

    /**
     * Configura los eventos de los componentes.
     */
    private void configurarEventos() {

        botonBuscar.addActionListener(
                evento -> buscarPregunta()
        );

        campoId.addActionListener(
                evento -> buscarPregunta()
        );

        botonEnviar.addActionListener(
                evento -> enviarPregunta()
        );

        campoId.getDocument().addDocumentListener(
                new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        limpiarSeleccion();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        limpiarSeleccion();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        limpiarSeleccion();
                    }
                }
        );
    }

    /**
     * Consulta una pregunta mediante el controlador.
     */
    private void buscarPregunta() {

        String preguntaId = campoId.getText().trim();

        if (preguntaId.isEmpty()) {
            mostrarMensaje(
                    "Debe ingresar el identificador de una pregunta.",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        limpiarSeleccion();
        establecerOcupado(true);

        new SwingWorker<Optional<Pregunta>, Void>() {

            @Override
            protected Optional<Pregunta> doInBackground() {
                return controlador.consultarPregunta(preguntaId);
            }

            @Override
            protected void done() {

                try {

                    Optional<Pregunta> resultado = get();

                    if (resultado.isPresent()) {

                        mostrarPregunta(resultado.get());

                    } else {

                        mostrarMensaje(
                                "No se encontró la pregunta o "
                                + "no pertenece al autor en sesión.",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();
                    mostrarError();

                } catch (ExecutionException ex) {

                    mostrarError();

                } finally {

                    establecerOcupado(false);
                }
            }

        }.execute();
    }

    /**
     * Muestra la pregunta recuperada y su estado.
     */
    private void mostrarPregunta(Pregunta pregunta) {

        idSeleccionado = pregunta.getId();
        estadoActual = pregunta.getEstado();

        campoPregunta.setText(
                pregunta.getPreguntaDirecta()
        );

        campoPregunta.setCaretPosition(0);

        mostrarEstado(estadoActual);
        actualizarBotones();
    }

    /**
     * Solicita el cambio de estado a pendiente de revisión.
     */
    private void enviarPregunta() {

        if (idSeleccionado == null
                || estadoActual != EstadoPregunta.BORRADOR) {
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea enviar esta pregunta a revisión?",
                "Confirmar envío",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        String preguntaId = idSeleccionado;

        establecerOcupado(true);

        new SwingWorker<List<String>, Void>() {

            @Override
            protected List<String> doInBackground() {
                return controlador.enviarARevision(preguntaId);
            }

            @Override
            protected void done() {

                try {

                    List<String> errores = get();

                    if (errores.isEmpty()) {

                        estadoActual =
                                EstadoPregunta.PENDIENTE_REVISION;

                        mostrarEstado(estadoActual);

                        mostrarMensaje(
                                "Pregunta enviada a revisión correctamente.",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } else {

                        mostrarMensaje(
                                String.join("\n", errores),
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();
                    mostrarError();

                } catch (ExecutionException ex) {

                    mostrarError();

                } finally {

                    establecerOcupado(false);
                }
            }

        }.execute();
    }

    /**
     * Aplica colores según el estado de la pregunta.
     */
    private void mostrarEstado(EstadoPregunta estado) {

        if (estado == EstadoPregunta.BORRADOR) {

            etiquetaEstado.setText("BORRADOR");
            etiquetaEstado.setForeground(
                    new Color(166, 99, 0)
            );

        } else if (estado == EstadoPregunta.PENDIENTE_REVISION) {

            etiquetaEstado.setText("PENDIENTE DE REVISIÓN");
            etiquetaEstado.setForeground(
                    new Color(0, 110, 80)
            );
        }
    }

    /**
     * Restablece la selección actual.
     */
    private void limpiarSeleccion() {

        idSeleccionado = null;
        estadoActual = null;

        etiquetaEstado.setText("SIN CONSULTAR");
        etiquetaEstado.setForeground(TEXTO);

        campoPregunta.setText(
                "Busca una pregunta para consultar su estado."
        );

        actualizarBotones();
    }

    /**
     * Actualiza la disponibilidad de los controles.
     */
    private void actualizarBotones() {

        campoId.setEnabled(!ocupado);
        botonBuscar.setEnabled(!ocupado);

        botonEnviar.setEnabled(
                !ocupado
                && idSeleccionado != null
                && estadoActual == EstadoPregunta.BORRADOR
        );
    }

    /**
     * Bloquea o habilita controles durante las operaciones.
     */
    private void establecerOcupado(boolean valor) {

        ocupado = valor;
        actualizarBotones();
    }

    /**
     * Muestra un mensaje al usuario.
     */
    private void mostrarMensaje(String mensaje, int tipo) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Banco de Preguntas Saber Pro",
                tipo
        );
    }

    /**
     * Informa un error durante la operación.
     */
    private void mostrarError() {

        mostrarMensaje(
                "No fue posible completar la operación.",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
