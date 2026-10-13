
package co.edu.unicauca.saberpro.presentation.view;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.service.NotificacionRevisionException;
import co.edu.unicauca.saberpro.domain.service.PaginaPreguntas;
import co.edu.unicauca.saberpro.presentation.controller.AsignacionRevisionController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Vista administrativa de HU-04.
 *
 * Permite consultar preguntas pendientes, asignar
 * revisores y reintentar notificaciones fallidas.
 */
public class AsignacionRevisionView extends JFrame {

    private static final Color AZUL =
            new Color(23, 54, 93);

    private static final Color FONDO =
            new Color(244, 247, 251);

    private static final int TAMANO_PAGINA = 10;

    private static final int LIMITE_REINTENTOS = 20;

    private final AsignacionRevisionController controlador;

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(
                    new String[]{
                            "Pregunta",
                            "Autor",
                            "Tema",
                            "Dificultad",
                            "Estado"
                    },
                    0
            ) {

                /**
                 * Impide editar las celdas directamente.
                 */
                @Override
                public boolean isCellEditable(
                        int fila,
                        int columna) {

                    return false;
                }
            };

    private final JTable tabla =
            new JTable(modeloTabla);

    private final JComboBox<Revisor> comboRevisores =
            new JComboBox<>();

    private final JButton botonActualizar =
            new JButton("Actualizar");

    private final JButton botonAnterior =
            new JButton("Anterior");

    private final JButton botonSiguiente =
            new JButton("Siguiente");

    private final JButton botonAsignar =
            new JButton("Asignar revisor");

    private final JButton botonVerAsignados =
            new JButton("Ver asignados");

    private final JButton botonVerDetalles =
            new JButton("Ver detalles");

    private final JButton botonReintentar =
            new JButton("Reintentar notificaciones");

    private final JLabel etiquetaPagina =
            new JLabel("Página 0 de 0");

    private final JLabel etiquetaTotal =
            new JLabel("Total: 0 preguntas");

    private final JLabel etiquetaEstado =
            new JLabel("Preparando consulta...");

    private List<Pregunta> preguntasActuales =
            List.of();

    private List<Revisor> revisoresActuales =
            List.of();

    private int paginaActual = 1;
    private int totalPaginas = 0;

    private boolean ocupado = false;

    /**
     * Contiene los datos recuperados para actualizar la vista.
     *
     * @param pagina página de preguntas pendientes
     * @param revisores docentes revisores registrados
     */
    private record DatosPantalla(
            PaginaPreguntas pagina,
            List<Revisor> revisores) {
    }

    /**
     * Inicializa la ventana administrativa.
     *
     * @param controlador controlador MVC de HU-04
     */
    public AsignacionRevisionView(
            AsignacionRevisionController controlador) {

        this.controlador = Objects.requireNonNull(
                controlador,
                "El controlador es obligatorio."
        );

        configurarVentana();
        construirInterfaz();
        configurarEventos();

        actualizarControles();
        cargarPagina(1);
    }

/**
 * Actualiza las preguntas pendientes y los docentes
 * revisores disponibles desde la base de datos.
 *
 * Se utiliza después de registrar un nuevo revisor.
 */
public void recargarDatos() {

    cargarPagina(paginaActual);
}


    /**
     * Configura las propiedades generales de la ventana.
     */
    private void configurarVentana() {

        setTitle(
                "Banco de Preguntas Saber Pro - HU-04"
        );

        setSize(1100, 680);

        setMinimumSize(
                new Dimension(850, 550)
        );

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        getContentPane().setBackground(FONDO);

        setLayout(
                new BorderLayout(12, 12)
        );
    }

    /**
     * Construye los componentes visuales de HU-04.
     */
    private void construirInterfaz() {

        // Encabezado.
        JPanel encabezado =
                new JPanel(new BorderLayout());

        encabezado.setBackground(AZUL);

        encabezado.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 25, 20, 25
                )
        );

        JLabel titulo = new JLabel(
                "HU-04 | Asignación de revisores"
        );

        titulo.setForeground(Color.WHITE);

        titulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        encabezado.add(
                titulo,
                BorderLayout.WEST
        );

        add(encabezado, BorderLayout.NORTH);

        // Panel principal.
        JPanel contenido =
                new JPanel(new BorderLayout(12, 12));

        contenido.setBackground(FONDO);

        contenido.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 20, 8, 20
                )
        );

        JPanel panelSuperior =
                new JPanel(new BorderLayout());

        panelSuperior.setBackground(Color.WHITE);

        panelSuperior.setBorder(
                BorderFactory.createEmptyBorder(
                        10, 12, 10, 12
                )
        );

        JLabel subtitulo = new JLabel(
                "Preguntas pendientes de revisión"
        );

        subtitulo.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        15
                )
        );

        panelSuperior.add(
                subtitulo,
                BorderLayout.WEST
        );

        panelSuperior.add(
                botonActualizar,
                BorderLayout.EAST
        );

        contenido.add(
                panelSuperior,
                BorderLayout.NORTH
        );

        // Tabla de preguntas.
        tabla.setRowHeight(32);

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader()
                .setReorderingAllowed(false);

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(390);

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(120);

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(170);

        JScrollPane desplazamiento =
                new JScrollPane(tabla);

        contenido.add(
                desplazamiento,
                BorderLayout.CENTER
        );

        // Paginación.
        JPanel panelPaginacion =
                new JPanel(new BorderLayout());

        panelPaginacion.setOpaque(false);

        panelPaginacion.add(
                etiquetaTotal,
                BorderLayout.WEST
        );

        JPanel navegacion =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        navegacion.setOpaque(false);

        navegacion.add(botonAnterior);
        navegacion.add(etiquetaPagina);
        navegacion.add(botonSiguiente);

        panelPaginacion.add(
                navegacion,
                BorderLayout.EAST
        );

        contenido.add(
                panelPaginacion,
                BorderLayout.SOUTH
        );

        add(
                contenido,
                BorderLayout.CENTER
        );

        // Panel inferior.
        JPanel panelInferior =
                new JPanel(new BorderLayout(8, 8));

        panelInferior.setBackground(Color.WHITE);

        panelInferior.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 20, 12, 20
                )
        );

        JPanel panelGestion =
                new JPanel(new GridLayout(2, 1, 0, 8));

        panelGestion.setOpaque(false);

        JPanel filaRevisor =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        filaRevisor.setOpaque(false);

        filaRevisor.add(
                new JLabel("Docente revisor:")
        );

        comboRevisores.setPreferredSize(
                new Dimension(350, 30)
        );

        comboRevisores.setRenderer(
                new DefaultListCellRenderer() {

                    /**
                     * Presenta el nombre y correo del revisor.
                     */
                    @Override
                    public Component getListCellRendererComponent(
                            JList<?> lista,
                            Object valor,
                            int indice,
                            boolean seleccionado,
                            boolean tieneFoco) {

                        super.getListCellRendererComponent(
                                lista,
                                valor,
                                indice,
                                seleccionado,
                                tieneFoco
                        );

                        if (valor instanceof Revisor revisor) {

                            setText(
                                    revisor.nombre()
                                            + " - "
                                            + revisor.correo()
                            );
                        }

                        return this;
                    }
                }
        );

        filaRevisor.add(comboRevisores);

        panelGestion.add(filaRevisor);

        JPanel filaBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        filaBotones.setOpaque(false);

        filaBotones.add(botonAsignar);
        filaBotones.add(botonVerAsignados);
        filaBotones.add(botonVerDetalles);

        panelGestion.add(filaBotones);

        panelInferior.add(
                panelGestion,
                BorderLayout.NORTH
        );

        JPanel panelNotificaciones =
                new JPanel(new BorderLayout());

        panelNotificaciones.setOpaque(false);

        panelNotificaciones.add(
                botonReintentar,
                BorderLayout.WEST
        );

        panelNotificaciones.add(
                etiquetaEstado,
                BorderLayout.CENTER
        );

        panelInferior.add(
                panelNotificaciones,
                BorderLayout.SOUTH
        );

        add(
                panelInferior,
                BorderLayout.SOUTH
        );
    }

    /**
     * Conecta los eventos de los botones y la tabla.
     */
    private void configurarEventos() {

        botonActualizar.addActionListener(
                evento -> cargarPagina(paginaActual)
        );

        botonAnterior.addActionListener(
                evento -> cargarPagina(paginaActual - 1)
        );

        botonSiguiente.addActionListener(
                evento -> cargarPagina(paginaActual + 1)
        );

        botonAsignar.addActionListener(
                evento -> asignarRevisorSeleccionado()
        );

        botonVerAsignados.addActionListener(
                evento -> mostrarAsignaciones()
        );

        botonVerDetalles.addActionListener(
                evento -> mostrarDetalles()
        );

        botonReintentar.addActionListener(
                evento -> reintentarNotificaciones()
        );

        tabla.getSelectionModel()
                .addListSelectionListener(evento -> {

                    if (!evento.getValueIsAdjusting()) {
                        actualizarControles();
                    }
                });

        comboRevisores.addActionListener(
                evento -> actualizarControles()
        );
    }

    /**
     * Recupera una página de preguntas y los revisores.
     *
     * Las consultas se ejecutan en segundo plano
     * para no bloquear la interfaz Swing.
     *
     * @param paginaSolicitada página que se desea consultar
     */
    private void cargarPagina(int paginaSolicitada) {

        ejecutarTarea(
                "Consultando preguntas pendientes...",
                () -> new DatosPantalla(
                        controlador.listarPreguntasPendientes(
                                paginaSolicitada,
                                TAMANO_PAGINA
                        ),
                        controlador.listarRevisores()
                ),
                this::mostrarDatos
        );
    }

    /**
     * Actualiza la tabla y la lista de revisores.
     *
     * @param datos información recibida del controlador
     */
    private void mostrarDatos(DatosPantalla datos) {

        PaginaPreguntas pagina = datos.pagina();

        preguntasActuales = pagina.preguntas();

        revisoresActuales = datos.revisores();

        paginaActual = pagina.paginaActual();
        totalPaginas = pagina.totalPaginas();

        modeloTabla.setRowCount(0);

        for (Pregunta pregunta : preguntasActuales) {

            modeloTabla.addRow(
                    new Object[]{
                            pregunta.getPreguntaDirecta(),
                            pregunta.getAutorId(),
                            pregunta.getTema(),
                            pregunta.getDificultad(),
                            pregunta.getEstado().name()
                    }
            );
        }

        comboRevisores.removeAllItems();

        for (Revisor revisor : revisoresActuales) {
            comboRevisores.addItem(revisor);
        }

        etiquetaTotal.setText(
                "Total: "
                        + pagina.totalRegistros()
                        + " preguntas"
        );

        etiquetaPagina.setText(
                "Página "
                        + paginaActual
                        + " de "
                        + totalPaginas
        );

        if (preguntasActuales.isEmpty()) {

            etiquetaEstado.setText(
                    "No hay preguntas en esta página."
            );

        } else if (revisoresActuales.isEmpty()) {

            etiquetaEstado.setText(
                    "No hay revisores registrados."
            );

        } else {

            etiquetaEstado.setText(
                    "Seleccione una pregunta y un revisor."
            );
        }

        actualizarControles();
    }

    /**
     * Obtiene la pregunta seleccionada en la tabla.
     *
     * @return pregunta seleccionada o null
     */
    private Pregunta obtenerPreguntaSeleccionada() {

        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            return null;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(fila);

        if (filaModelo < 0
                || filaModelo >= preguntasActuales.size()) {

            return null;
        }

        return preguntasActuales.get(filaModelo);
    }

    /**
     * Solicita la asignación del revisor seleccionado.
     */
    private void asignarRevisorSeleccionado() {

        Pregunta pregunta = obtenerPreguntaSeleccionada();

        Revisor revisor =
                (Revisor) comboRevisores.getSelectedItem();

        if (pregunta == null || revisor == null) {

            mostrarAviso(
                    "Seleccione una pregunta y un revisor."
            );

            return;
        }

        ejecutarTarea(
                "Registrando asignación...",
                () -> controlador.asignarRevisor(
                        pregunta.getId(),
                        revisor.id()
                ),
                guardada -> {

                    if (guardada) {

                        JOptionPane.showMessageDialog(
                                this,
                                "El revisor fue asignado "
                                        + "correctamente.\n"
                                        + "La notificación fue procesada.",
                                "Asignación registrada",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } else {

                        mostrarAviso(
                                "Este revisor ya estaba "
                                        + "asignado a la pregunta."
                        );
                    }
                }
        );
    }

    /**
     * Muestra los revisores asignados a la pregunta actual.
     */
    private void mostrarAsignaciones() {

        Pregunta pregunta = obtenerPreguntaSeleccionada();

        if (pregunta == null) {

            mostrarAviso(
                    "Seleccione primero una pregunta."
            );

            return;
        }

        ejecutarTarea(
                "Consultando asignaciones...",
                () -> controlador.listarAsignaciones(
                        pregunta.getId()
                ),
                asignaciones -> {

                    if (asignaciones.isEmpty()) {

                        mostrarAviso(
                                "Esta pregunta todavía "
                                        + "no tiene revisores asignados."
                        );

                        return;
                    }

                    StringBuilder mensaje =
                            new StringBuilder(
                                    "Revisores asignados:\n\n"
                            );

                    for (AsignacionRevision asignacion
                            : asignaciones) {

                        String nombre = revisoresActuales
                                .stream()
                                .filter(r -> r.id().equals(
                                        asignacion.revisorId()
                                ))
                                .map(Revisor::nombre)
                                .findFirst()
                                .orElse(
                                        asignacion.revisorId()
                                );

                        mensaje.append("• ")
                                .append(nombre)
                                .append("\n")
                                .append("   ID: ")
                                .append(asignacion.revisorId())
                                .append("\n")
                                .append("   Fecha: ")
                                .append(asignacion.fechaAsignacion())
                                .append("\n\n");
                    }

                    JOptionPane.showMessageDialog(
                            this,
                            mensaje.toString(),
                            "Asignaciones de la pregunta",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
        );
    }

    /**
     * Presenta los detalles de la pregunta seleccionada.
     */
    private void mostrarDetalles() {

        Pregunta pregunta = obtenerPreguntaSeleccionada();

        if (pregunta == null) {

            mostrarAviso(
                    "Seleccione primero una pregunta."
            );

            return;
        }

        StringBuilder detalle = new StringBuilder();

        detalle.append("Identificador:\n")
                .append(pregunta.getId())
                .append("\n\n");

        detalle.append("Autor:\n")
                .append(pregunta.getAutorId())
                .append("\n\n");

        detalle.append("Contexto:\n")
                .append(pregunta.getContexto())
                .append("\n\n");

        detalle.append("Pregunta:\n")
                .append(pregunta.getPreguntaDirecta())
                .append("\n\n");

        detalle.append("Distractores:\n");

        int numero = 1;

        for (String distractor
                : pregunta.getDistractores()) {

            detalle.append(numero++)
                    .append(". ")
                    .append(distractor)
                    .append("\n");
        }

        detalle.append("\nRespuesta correcta:\n")
                .append(pregunta.getRespuestaCorrecta())
                .append("\n\n");

        detalle.append("Justificación:\n")
                .append(pregunta.getJustificacion())
                .append("\n\n");

        detalle.append("Bibliografía:\n")
                .append(pregunta.getBibliografia())
                .append("\n\n");

        detalle.append("Competencia: ")
                .append(pregunta.getCompetencia())
                .append("\n");

        detalle.append("Tema: ")
                .append(pregunta.getTema())
                .append("\n");

        detalle.append("Subtema: ")
                .append(pregunta.getSubtema())
                .append("\n");

        detalle.append("Dificultad: ")
                .append(pregunta.getDificultad())
                .append("\n");

        detalle.append("Estado: ")
                .append(pregunta.getEstado());

        JTextArea area = new JTextArea(
                detalle.toString()
        );

        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);

        JScrollPane desplazamiento =
                new JScrollPane(area);

        desplazamiento.setPreferredSize(
                new Dimension(650, 400)
        );

        JOptionPane.showMessageDialog(
                this,
                desplazamiento,
                "Detalle de la pregunta",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Solicita reintentar las notificaciones pendientes.
     */
    private void reintentarNotificaciones() {

        int confirmacion =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea reintentar hasta "
                                + LIMITE_REINTENTOS
                                + " notificaciones pendientes?",
                        "Confirmar reintentos",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        ejecutarTarea(
                "Reintentando notificaciones...",
                () -> controlador.reintentarNotificaciones(
                        LIMITE_REINTENTOS
                ),
                resultado -> {

                    String resumen = """
                            Resultado de los reintentos:

                            Procesadas: %d
                            Enviadas: %d
                            Fallidas: %d
                            """.formatted(
                            resultado.procesadas(),
                            resultado.enviadas(),
                            resultado.fallidas()
                    );

                    JOptionPane.showMessageDialog(
                            this,
                            resumen,
                            "Notificaciones de HU-04",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
        );
    }

    /**
     * Ejecuta una operación en segundo plano.
     *
     * Evita bloquear la ventana durante consultas
     * SQLite o conexiones de correo SMTP.
     *
     * @param mensaje texto mostrado durante la operación
     * @param tarea operación que debe ejecutarse
     * @param alTerminar acción al recibir el resultado
     * @param <T> tipo del resultado
     */
    private <T> void ejecutarTarea(
            String mensaje,
            Callable<T> tarea,
            Consumer<T> alTerminar) {

        if (ocupado) {
            return;
        }

        ocupado = true;

        etiquetaEstado.setText(mensaje);

        actualizarControles();

        SwingWorker<T, Void> trabajador =
                new SwingWorker<>() {

                    /**
                     * Ejecuta la operación fuera del hilo gráfico.
                     */
                    @Override
                    protected T doInBackground()
                            throws Exception {

                        return tarea.call();
                    }

                    /**
                     * Presenta el resultado en el hilo de Swing.
                     */
                    @Override
                    protected void done() {

                        ocupado = false;
                        actualizarControles();

                        try {

                            T resultado = get();

                            etiquetaEstado.setText(
                                    "Operación finalizada."
                            );

                            alTerminar.accept(resultado);

                        } catch (InterruptedException e) {

                            Thread.currentThread().interrupt();

                            mostrarError(e);

                        } catch (ExecutionException e) {

                            mostrarError(e.getCause());
                        }
                    }
                };

        trabajador.execute();
    }

    /**
     * Habilita o deshabilita controles según el estado.
     */
    private void actualizarControles() {

        boolean preguntaSeleccionada =
                obtenerPreguntaSeleccionada() != null;

        boolean revisorSeleccionado =
                comboRevisores.getSelectedItem()
                        instanceof Revisor;

        botonActualizar.setEnabled(!ocupado);

        botonAnterior.setEnabled(
                !ocupado && paginaActual > 1
        );

        botonSiguiente.setEnabled(
                !ocupado && paginaActual < totalPaginas
        );

        botonAsignar.setEnabled(
                !ocupado
                        && preguntaSeleccionada
                        && revisorSeleccionado
        );

        botonVerAsignados.setEnabled(
                !ocupado && preguntaSeleccionada
        );

        botonVerDetalles.setEnabled(
                !ocupado && preguntaSeleccionada
        );

        botonReintentar.setEnabled(!ocupado);

        comboRevisores.setEnabled(!ocupado);

        tabla.setEnabled(!ocupado);
    }

    /**
     * Muestra un aviso informativo.
     *
     * @param mensaje descripción del aviso
     */
    private void mostrarAviso(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Información",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Presenta errores de operaciones administrativas.
     *
     * Distingue cuando una asignación sí quedó guardada,
     * aunque no haya sido posible notificar al revisor.
     *
     * @param error excepción producida
     */
    private void mostrarError(Throwable error) {

        String mensaje = error == null
                ? "Ocurrió un error desconocido."
                : error.getMessage();

        if (mensaje == null || mensaje.isBlank()) {
            mensaje = "No se pudo completar la operación.";
        }

        if (error instanceof NotificacionRevisionException) {

            JOptionPane.showMessageDialog(
                    this,
                    mensaje
                            + "\n\nLa asignación permanece "
                            + "guardada en SQLite."
                            + "\nPuede utilizar el botón "
                            + "'Reintentar notificaciones'.",
                    "Advertencia de correo",
                    JOptionPane.WARNING_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        etiquetaEstado.setText(
                "No se pudo completar la operación."
        );
    }
}
