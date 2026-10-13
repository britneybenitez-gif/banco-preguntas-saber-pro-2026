
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.service.IRegistroRevisorService;

import java.util.Objects;

/**
 * Controlador MVC para el registro administrativo
 * de docentes revisores en HU-04.
 *
 * Recibe las solicitudes de la interfaz gráfica
 * y delega el registro al servicio de negocio.
 *
 * No realiza consultas SQL ni guarda datos
 * directamente en la base de datos.
 */
public class RegistroRevisorController {

    private final IRegistroRevisorService servicio;

    /**
     * Inicializa el controlador con el servicio
     * encargado de registrar docentes revisores.
     *
     * @param servicio servicio de registro de revisores
     * @throws NullPointerException si el servicio es nulo
     */
    public RegistroRevisorController(
            IRegistroRevisorService servicio) {

        this.servicio = Objects.requireNonNull(
                servicio,
                "El servicio de registro es obligatorio."
        );
    }

    /**
     * Solicita el registro de un nuevo docente revisor.
     *
     * Las validaciones de autorización, datos
     * y duplicados corresponden al servicio.
     *
     * @param id identificador único del revisor
     * @param nombre nombre completo del docente
     * @param correo correo electrónico del docente
     * @return revisor registrado correctamente
     */
    public Revisor registrarRevisor(
            String id,
            String nombre,
            String correo) {

        return servicio.registrarRevisor(
                id,
                nombre,
                correo
        );
    }
}
