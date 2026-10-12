
package co.edu.unicauca.saberpro.presentation.view;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.service.PaginaPreguntas;
import co.edu.unicauca.saberpro.presentation.controller.ConsultaPreguntaController;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Interfaz para listar y consultar las preguntas del autor.
 */
public class ConsultaPreguntaView extends JFrame {

    private static final Color AZUL = new Color(23, 54, 93);
    private static final Color FONDO = new Color(244, 247, 251);
    private static final Color VERDE = new Color(0, 110, 80);
    private static final Color NARANJA = new Color(166, 99, 0);

    private static final int TAMANO_PAGINA = 10;

    private final ConsultaPreguntaController controlador;

    private final JComboBox<String> filtroEstado =
            new JComboBox<>(new String[]{
                    "Todos",
                    "BORRADOR",
                    "PENDIENTE_REVISION"
            });

    private final JComboBox<String> filtroDificultad =
            new JComboBox<>(new String[]{
                    "Todas",
                    "Baja",
                    "Media",
                    "Alta"
            });

    private final JButton botonBuscar =
            new JButton("Aplicar filtros");

    private final JButton botonAnterior =
            new JButton("Anterior");

    private final JButton botonSiguiente =
            new JButton("Siguiente");

    private final JButton botonDetalle =
            new JButton("Ver detalles");

    private final JLabel etiquetaPagina =
            new JLabel("Página 0 de 0");

    private final JLabel etiquetaTotal =
            new JLabel("Total: 0 preguntas");

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(
                    new String[]{
                            "Pregunta",
                            "Competencia",
                            "Tema",
                            "Dificultad",
                            "Estado"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int fila, int columna) {
                    return false;
                }
            };

    private final JTable tabla = new JTable(modeloTabla);

    private List<Pregunta> preguntas = List.of();

    private int paginaActual = 1;
    private int totalPaginas = 0;
    private boolean consultando = false;

    /**
     * Inicializa la ventana con el controlador de consulta.
     *
     * @param controlador controlador de HU-03
     */
    public ConsultaPreguntaView(
            ConsultaPreguntaController controlador) {

        this.controlador = Objects.requireNonNull(
                controlador,
                "El controlador es obligatorio."
        );

        configurarVentana();
        construirInterfaz();
        configurarEventos();

        cargarPagina(1);
    }

    /**
     * Configura las propiedades de la ventana.
     */
    private void configurarVentana() {

        setTitle("Banco de Preguntas Saber Pro - HU-03");
        setSize(1050, 620);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout(15, 15));
    }

    /**
     * Construye los componentes de la interfaz.
     */
    private void construirInterfaz() {

        JPanel encabezado = new JPanel(
                new BorderLayout()
        );

        encabezado.setBackground(AZUL);
        encabezado.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel titulo = new JLabel(
                "HU-03 | Mis preguntas"
        );

        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font(
                "SansSerif", Font.BOLD, 22
        ));

        encabezado.add(titulo, BorderLayout.WEST);
        add(encabezado, BorderLayout.NORTH);

        JPanel contenido = new JPanel(
                new BorderLayout(12, 15)
        );

        contenido.setBackground(FONDO);
        contenido.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 20, 10, 20
                )
        );

        JPanel panelFiltros = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 10)
        );

        panelFiltros.setBackground(Color.WHITE);

        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(filtroEstado);

        panelFiltros.add(new JLabel("Dificultad:"));
        panelFiltros.add(filtroDificultad);

        configurarBoton(botonBuscar);
        panelFiltros.add(botonBuscar);

        contenido.add(
                panelFiltros, BorderLayout.NORTH
        );

        configurarTabla();

        JScrollPane desplazamiento =
                new JScrollPane(tabla);

        desplazamiento.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 226, 233)
                )
        );

        contenido.add(
                desplazamiento, BorderLayout.CENTER
        );

        JPanel panelPaginacion = new JPanel(
                new BorderLayout(10, 10)
        );

        panelPaginacion.setOpaque(false);

        panelPaginacion.add(
                etiquetaTotal, BorderLayout.WEST
        );

        JPanel navegacion = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 12, 0)
        );

        navegacion.setOpaque(false);

        navegacion.add(botonAnterior);
        navegacion.add(etiquetaPagina);
        navegacion.add(botonSiguiente);

        panelPaginacion.add(
                navegacion, BorderLayout.EAST
        );

        contenido.add(
                panelPaginacion, BorderLayout.SOUTH
        );

        add(contenido, BorderLayout.CENTER);

        JPanel pie = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 20, 12)
        );

        pie.setBackground(Color.WHITE);

        configurarBoton(botonDetalle);
        pie.add(botonDetalle);

        add(pie, BorderLayout.SOUTH);
    }

    /**
     * Configura el listado y los colores de estado.
     */
    private void configurarTabla() {

        tabla.setRowHeight(34);
        tabla.setFont(new Font(
                "SansSerif", Font.PLAIN, 13
        ));

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setFillsViewportHeight(true);
        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getColumnModel()
                .getColumn(0).setPreferredWidth(350);

        tabla.getColumnModel()
                .getColumn(4).setPreferredWidth(180);

        tabla.getColumnModel()
                .getColumn(4).setCellRenderer(
                        new DefaultTableCellRenderer() {

                            @Override
                            public Component getTableCellRendererComponent(
                                    JTable tabla,
                                    Object valor,
                                    boolean seleccionado,
                                    boolean enfocado,
                                    int fila,
                                    int columna) {

                                super.getTableCellRendererComponent(
                                        tabla,
                                        valor,
                                        seleccionado,
                                        enfocado,
                                        fila,
                                        columna
                                );

                                String estado = String.valueOf(valor);

                                if (!seleccionado) {

                                    setForeground(
                                            "BORRADOR".equals(estado)
                                                    ? NARANJA
                                                    : VERDE
                                    );

                                } else {

                                    setForeground(
                                            tabla.getSelectionForeground()
                                    );
                                }

                                setFont(getFont().deriveFont(Font.BOLD));

                                return this;
                            }
                        }
                );
    }

    /**
     * Configura los eventos de búsqueda y navegación.
     */
    private void configurarEventos() {

        botonBuscar.addActionListener(
                evento -> cargarPagina(1)
        );

        botonAnterior.addActionListener(
                evento -> cargarPagina(paginaActual - 1)
        );

        botonSiguiente.addActionListener(
                evento -> cargarPagina(paginaActual + 1)
        );

        botonDetalle.addActionListener(
                evento -> mostrarDetalles()
        );

        tabla.getSelectionModel().addListSelectionListener(
                evento -> actualizarBotones()
        );
    }

    /**
     * Consulta una página sin bloquear la interfaz Swing.
     *
     * @param pagina número de página solicitado
     */
    private void cargarPagina(int pagina) {

        if (consultando || pagina < 1) {
            return;
        }

        String estadoSeleccionado =
                (String) filtroEstado.getSelectedItem();

        EstadoPregunta estado =
                "Todos".equals(estadoSeleccionado)
                        ? null
                        : EstadoPregunta.valueOf(
                                estadoSeleccionado
                        );

        String dificultad =
                (String) filtroDificultad.getSelectedItem();

        if ("Todas".equals(dificultad)) {
            dificultad = null;
        }

        final String dificultadFinal = dificultad;

        consultando = true;
        actualizarBotones();

        new SwingWorker<PaginaPreguntas, Void>() {

            @Override
            protected PaginaPreguntas doInBackground() {

                return controlador.listarMisPreguntas(
                        estado,
                        dificultadFinal,
                        pagina,
                        TAMANO_PAGINA
                );
            }

            @Override
            protected void done() {

                try {

                    PaginaPreguntas resultado = get();

                    mostrarResultados(resultado);

                } catch (InterruptedException ex) {

                    Thread.currentThread().interrupt();
                    mostrarError();

                } catch (ExecutionException ex) {

                    mostrarError();

                } finally {

                    consultando = false;
                    actualizarBotones();
                }
            }

        }.execute();
    }

    /**
     * Actualiza la tabla y los controles de paginación.
     *
     * @param resultado resultado obtenido del servicio
     */
    private void mostrarResultados(PaginaPreguntas resultado) {

        preguntas = resultado.preguntas();

        paginaActual = resultado.paginaActual();
        totalPaginas = resultado.totalPaginas();

        modeloTabla.setRowCount(0);

        for (Pregunta pregunta : preguntas) {

            modeloTabla.addRow(new Object[]{
                    pregunta.getPreguntaDirecta(),
                    pregunta.getCompetencia(),
                    pregunta.getTema(),
                    pregunta.getDificultad(),
                    pregunta.getEstado().name()
            });
        }

        etiquetaTotal.setText(
                "Total: " + resultado.totalRegistros()
                        + " preguntas"
        );

        if (totalPaginas == 0) {

            etiquetaPagina.setText("Página 0 de 0");

        } else {

            etiquetaPagina.setText(
                    "Página " + paginaActual
                            + " de " + totalPaginas
            );
        }

        tabla.clearSelection();
    }

    /**
     * Muestra la información completa de la pregunta seleccionada.
     */
    private void mostrarDetalles() {

        int fila = tabla.getSelectedRow();

        if (fila < 0 || fila >= preguntas.size()) {
            return;
        }

        Pregunta pregunta = preguntas.get(fila);

        String detalle =
                "ID: " + pregunta.getId()
                + "\n\nEstado: " + pregunta.getEstado()
                + "\nDificultad: " + pregunta.getDificultad()
                + "\nCompetencia: " + pregunta.getCompetencia()
                + "\nTema: " + pregunta.getTema()
                + "\nSubtema: " + pregunta.getSubtema()
                + "\n\nContexto:\n" + pregunta.getContexto()
                + "\n\nPregunta:\n" + pregunta.getPreguntaDirecta()
                + "\n\nDistractores:\n"
                + String.join("\n", pregunta.getDistractores())
                + "\n\nRespuesta correcta:\n"
                + pregunta.getRespuestaCorrecta()
                + "\n\nJustificación:\n"
                + pregunta.getJustificacion()
                + "\n\nBibliografía:\n"
                + pregunta.getBibliografia();

        JTextArea area = new JTextArea(detalle, 20, 55);

        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);

        JOptionPane.showMessageDialog(
                this,
                new JScrollPane(area),
                "Detalle de la pregunta",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Habilita los botones según la página y la selección.
     */
    private void actualizarBotones() {

        filtroEstado.setEnabled(!consultando);
        filtroDificultad.setEnabled(!consultando);
        botonBuscar.setEnabled(!consultando);

        botonAnterior.setEnabled(
                !consultando && paginaActual > 1
        );

        botonSiguiente.setEnabled(
                !consultando && paginaActual < totalPaginas
        );

        botonDetalle.setEnabled(
                !consultando && tabla.getSelectedRow() >= 0
        );
    }

    /**
     * Configura el estilo principal de un botón.
     */
    private void configurarBoton(JButton boton) {

        boton.setBackground(AZUL);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
    }

    /**
     * Informa un error durante la consulta.
     */
    private void mostrarError() {

        JOptionPane.showMessageDialog(
                this,
                "No fue posible consultar las preguntas.",
                "Error de consulta",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
