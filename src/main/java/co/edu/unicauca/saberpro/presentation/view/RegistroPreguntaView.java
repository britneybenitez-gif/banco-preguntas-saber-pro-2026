
package co.edu.unicauca.saberpro.presentation.view;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.presentation.controller.RegistroPreguntaController;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.Border;

/**
 * Interfaz de registro de preguntas Saber Pro.
 */
public class RegistroPreguntaView extends JFrame {

    private static final Color AZUL = new Color(23, 54, 93);
    private static final Color FONDO = new Color(244, 247, 251);
    private static final Color BLANCO = Color.WHITE;
    private static final Color TEXTO = new Color(40, 49, 61);
    private static final Color BORDE = new Color(220, 226, 233);

    private final RegistroPreguntaController controlador;
    private final String autorId;

    private final JTextArea contexto = crearArea(4);
    private final JTextArea preguntaDirecta = crearArea(3);
    private final JTextField[] distractores = new JTextField[4];
    private final JTextField respuestaCorrecta = crearCampo();
    private final JTextArea justificacion = crearArea(3);
    private final JTextArea bibliografia = crearArea(2);

    private final JTextField competencia = crearCampo();
    private final JTextField tema = crearCampo();
    private final JTextField subtema = crearCampo();

    private final JComboBox<String> dificultad =
            new JComboBox<>(new String[]{"Baja", "Media", "Alta"});

    private final JButton botonGuardar =
            new JButton("Guardar pregunta");

    private final JButton botonLimpiar =
            new JButton("Limpiar formulario");

    /**
     * Inicializa la interfaz y sus dependencias.
     */
    public RegistroPreguntaView(
            RegistroPreguntaController controlador,
            String autorId) {

        this.controlador = Objects.requireNonNull(controlador);

        if (autorId == null || autorId.isBlank()) {
            throw new IllegalArgumentException(
                    "El autor es obligatorio."
            );
        }

        this.autorId = autorId;

        configurarVentana();
        construirInterfaz();
        configurarEventos();
    }

    private void configurarVentana() {

        setTitle("Banco de Preguntas Saber Pro - HU-01");
        setSize(1050, 740);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout());
    }

    /**
     * Organiza las secciones del formulario.
     */
    private void construirInterfaz() {

        add(crearEncabezado(), BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(
                contenido, BoxLayout.Y_AXIS
        ));

        contenido.setBackground(FONDO);
        contenido.setBorder(
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );

        JPanel informacion = crearSeccion(
                "01  |  INFORMACIÓN DE LA PREGUNTA"
        );

        agregarCampo(informacion, "Contexto:",
                new JScrollPane(contexto), 1);

        agregarCampo(informacion, "Pregunta directa:",
                new JScrollPane(preguntaDirecta), 2);

        agregarSeccion(contenido, informacion);

        JPanel opciones = crearSeccion(
                "02  |  OPCIONES DE RESPUESTA"
        );

        for (int i = 0; i < distractores.length; i++) {

            distractores[i] = crearCampo();

            agregarCampo(
                    opciones,
                    "Distractor " + (i + 1) + ":",
                    distractores[i],
                    i + 1
            );
        }

        agregarCampo(
                opciones,
                "Respuesta correcta:",
                respuestaCorrecta,
                5
        );

        agregarSeccion(contenido, opciones);

        JPanel soporte = crearSeccion(
                "03  |  JUSTIFICACIÓN Y BIBLIOGRAFÍA"
        );

        agregarCampo(soporte, "Justificación:",
                new JScrollPane(justificacion), 1);

        agregarCampo(soporte, "Bibliografía:",
                new JScrollPane(bibliografia), 2);

        agregarSeccion(contenido, soporte);

        JPanel clasificacion = crearSeccion(
                "04  |  CLASIFICACIÓN ACADÉMICA"
        );

        agregarCampo(clasificacion, "Competencia:",
                competencia, 1);

        agregarCampo(clasificacion, "Tema:",
                tema, 2);

        agregarCampo(clasificacion, "Subtema:",
                subtema, 3);

        agregarCampo(clasificacion, "Dificultad:",
                dificultad, 4);

        agregarSeccion(contenido, clasificacion);

        JScrollPane desplazamiento =
                new JScrollPane(contenido);

        desplazamiento.setBorder(null);
        desplazamiento.getVerticalScrollBar()
                .setUnitIncrement(18);

        add(desplazamiento, BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {

        JPanel encabezado = new JPanel(
                new BorderLayout(10, 4)
        );

        encabezado.setBackground(AZUL);
        encabezado.setBorder(
                BorderFactory.createEmptyBorder(20, 28, 20, 28)
        );

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(
                textos, BoxLayout.Y_AXIS
        ));

        textos.setOpaque(false);

        JLabel titulo = new JLabel(
                "BANCO DE PREGUNTAS SABER PRO"
        );

        titulo.setFont(new Font(
                "SansSerif", Font.BOLD, 23
        ));

        titulo.setForeground(BLANCO);

        JLabel subtitulo = new JLabel(
                "HU-01  |  Registro de preguntas  |  Autor"
        );

        subtitulo.setFont(new Font(
                "SansSerif", Font.PLAIN, 13
        ));

        subtitulo.setForeground(
                new Color(216, 231, 248)
        );

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(7));
        textos.add(subtitulo);

        encabezado.add(textos, BorderLayout.WEST);

        JLabel estado = new JLabel("BORRADOR");
        estado.setForeground(BLANCO);
        estado.setFont(new Font(
                "SansSerif", Font.BOLD, 12
        ));

        encabezado.add(estado, BorderLayout.EAST);

        return encabezado;
    }

    /**
     * Construye una sección visual del formulario.
     */
    private JPanel crearSeccion(String titulo) {

        JPanel seccion = new JPanel(new GridBagLayout());
        seccion.setBackground(BLANCO);

        Border bordeExterno = BorderFactory.createLineBorder(
                BORDE, 1
        );

        Border bordeInterno =
                BorderFactory.createEmptyBorder(15, 20, 18, 20);

        seccion.setBorder(BorderFactory.createCompoundBorder(
                bordeExterno, bordeInterno
        ));

        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setForeground(AZUL);
        etiqueta.setFont(new Font(
                "SansSerif", Font.BOLD, 15
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);

        seccion.add(etiqueta, gbc);

        return seccion;
    }

    private void agregarSeccion(
            JPanel contenido,
            JPanel seccion) {

        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        seccion.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                seccion.getPreferredSize().height
        ));

        contenido.add(seccion);
        contenido.add(Box.createVerticalStrut(16));
    }

    private void agregarCampo(
            JPanel panel,
            String nombre,
            JComponent componente,
            int fila) {

        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setForeground(TEXTO);
        etiqueta.setFont(new Font(
                "SansSerif", Font.PLAIN, 13
        ));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = fila;
        gbc.gridx = 0;
        gbc.insets = new Insets(7, 0, 7, 16);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        panel.add(etiqueta, gbc);

        gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = fila;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 0, 7, 0);

        panel.add(componente, gbc);
    }

    private JPanel crearPie() {

        JPanel pie = new JPanel(
                new BorderLayout(15, 0)
        );

        pie.setBackground(BLANCO);
        pie.setBorder(
                BorderFactory.createEmptyBorder(14, 25, 14, 25)
        );

        JLabel aviso = new JLabel(
                "Las preguntas se guardan en estado Borrador."
        );

        aviso.setForeground(TEXTO);

        JPanel botones = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0)
        );

        botones.setOpaque(false);

        configurarBoton(botonLimpiar, BLANCO, AZUL);
        configurarBoton(botonGuardar, AZUL, BLANCO);

        botonLimpiar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AZUL),
                        BorderFactory.createEmptyBorder(9, 15, 9, 15)
                )
        );

        botones.add(botonLimpiar);
        botones.add(botonGuardar);

        pie.add(aviso, BorderLayout.WEST);
        pie.add(botones, BorderLayout.EAST);

        return pie;
    }

    private void configurarBoton(
            JButton boton,
            Color fondo,
            Color texto) {

        boton.setBackground(fondo);
        boton.setForeground(texto);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setFont(new Font(
                "SansSerif", Font.BOLD, 13
        ));

        boton.setBorder(
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        );
    }

    private static JTextField crearCampo() {

        JTextField campo = new JTextField(40);
        campo.setFont(new Font(
                "SansSerif", Font.PLAIN, 13
        ));

        return campo;
    }

    private static JTextArea crearArea(int filas) {

        JTextArea area = new JTextArea(filas, 40);

        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(
                "SansSerif", Font.PLAIN, 13
        ));

        area.setMargin(new Insets(7, 7, 7, 7));

        return area;
    }

    private void configurarEventos() {

        botonGuardar.addActionListener(
                e -> guardarPregunta()
        );

        botonLimpiar.addActionListener(
                e -> limpiarFormulario()
        );
    }

    /**
     * Registra la pregunta mediante el controlador.
     */
    private void guardarPregunta() {

        List<String> opciones = new ArrayList<>();

        for (JTextField campo : distractores) {
            opciones.add(campo.getText());
        }

        Pregunta pregunta = new Pregunta(
                autorId,
                contexto.getText(),
                preguntaDirecta.getText(),
                opciones,
                respuestaCorrecta.getText(),
                justificacion.getText(),
                bibliografia.getText(),
                competencia.getText(),
                tema.getText(),
                subtema.getText(),
                (String) dificultad.getSelectedItem()
        );

        botonGuardar.setEnabled(false);
        botonLimpiar.setEnabled(false);

        new SwingWorker<List<String>, Void>() {

            @Override
            protected List<String> doInBackground() {
                return controlador.registrarPregunta(pregunta);
            }

            @Override
            protected void done() {

                try {

                    List<String> errores = get();

                    if (errores.isEmpty()) {

                        JOptionPane.showMessageDialog(
                                RegistroPreguntaView.this,
                                "Pregunta registrada correctamente.",
                                "Registro exitoso",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        limpiarFormulario();

                    } else {

                        JOptionPane.showMessageDialog(
                                RegistroPreguntaView.this,
                                String.join("\n", errores),
                                "Errores de validación",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();
                    mostrarErrorGuardado();

                } catch (ExecutionException ex) {

                    mostrarErrorGuardado();

                } finally {

                    botonGuardar.setEnabled(true);
                    botonLimpiar.setEnabled(true);
                }
            }
        }.execute();
    }

    private void mostrarErrorGuardado() {

        JOptionPane.showMessageDialog(
                this,
                "No se pudo guardar la pregunta.",
                "Error de registro",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Restablece los campos del formulario.
     */
    private void limpiarFormulario() {

        contexto.setText("");
        preguntaDirecta.setText("");

        for (JTextField campo : distractores) {
            campo.setText("");
        }

        respuestaCorrecta.setText("");
        justificacion.setText("");
        bibliografia.setText("");
        competencia.setText("");
        tema.setText("");
        subtema.setText("");

        dificultad.setSelectedIndex(0);
        contexto.requestFocusInWindow();
    }
}
