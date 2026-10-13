
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.AsignacionRevision;
import co.edu.unicauca.saberpro.domain.model.Revisor;

import co.edu.unicauca.saberpro.domain.service.IAsignacionRevisionService;
import co.edu.unicauca.saberpro.domain.service.IConsultaPendientesRevisionService;
import co.edu.unicauca.saberpro.domain.service.IReintentoNotificacionesService;
import co.edu.unicauca.saberpro.domain.service.PaginaPreguntas;
import co.edu.unicauca.saberpro.domain.service.ResultadoReintentos;

import java.util.List;
import java.util.Objects;

/**
 * Controlador MVC para la gestión administrativa
 * de asignaciones de revisores de HU-04.
 *
 * Coordina la consulta de preguntas pendientes,
 * las asignaciones y los reintentos de notificaciones.
 */
public class AsignacionRevisionController {

    private final IAsignacionRevisionService servicioAsignaciones;

    private final IReintentoNotificacionesService servicioReintentos;

    private final IConsultaPendientesRevisionService servicioPendientes;

    /**
     * Inicializa el controlador con los servicios de HU-04.
     *
     * @param servicioAsignaciones servicio de asignación
     * @param servicioReintentos servicio de notificaciones
     * @param servicioPendientes servicio de consulta administrativa
     */
    public AsignacionRevisionController(
            IAsignacionRevisionService servicioAsignaciones,
            IReintentoNotificacionesService servicioReintentos,
            IConsultaPendientesRevisionService servicioPendientes) {

        this.servicioAsignaciones = Objects.requireNonNull(
                servicioAsignaciones,
                "El servicio de asignaciones es obligatorio."
        );

        this.servicioReintentos = Objects.requireNonNull(
                servicioReintentos,
                "El servicio de reintentos es obligatorio."
        );

        this.servicioPendientes = Objects.requireNonNull(
                servicioPendientes,
                "El servicio de consulta de pendientes es obligatorio."
        );
    }

    /**
     * Consulta las preguntas pendientes de revisión.
     *
     * El servicio valida que el usuario tenga
     * el rol ADMINISTRADOR.
     *
     * @param pagina número de página, comenzando en 1
     * @param tamanoPagina cantidad de preguntas por página
     * @return resultado paginado de preguntas pendientes
     */
    public PaginaPreguntas listarPreguntasPendientes(
            int pagina,
            int tamanoPagina) {

        return servicioPendientes.listarPendientes(
                pagina,
                tamanoPagina
        );
    }

    /**
     * Solicita la asignación de un revisor a una pregunta.
     *
     * @param preguntaId identificador de la pregunta
     * @param revisorId identificador del revisor
     * @return true si se registró una asignación nueva
     */
    public boolean asignarRevisor(
            String preguntaId,
            String revisorId) {

        return servicioAsignaciones.asignarRevisor(
                preguntaId,
                revisorId
        );
    }

    /**
     * Consulta los docentes revisores disponibles.
     *
     * @return lista de revisores registrados
     */
    public List<Revisor> listarRevisores() {

        return servicioAsignaciones.listarRevisores();
    }

    /**
     * Consulta los revisores asignados a una pregunta.
     *
     * @param preguntaId identificador de la pregunta
     * @return asignaciones registradas
     */
    public List<AsignacionRevision> listarAsignaciones(
            String preguntaId) {

        return servicioAsignaciones.listarAsignaciones(
                preguntaId
        );
    }

    /**
     * Solicita el reintento de notificaciones pendientes.
     *
     * @param limite cantidad máxima de notificaciones
     * @return resumen de los reintentos realizados
     */
    public ResultadoReintentos reintentarNotificaciones(
            int limite) {

        return servicioReintentos.reintentarPendientes(
                limite
        );
    }
}
