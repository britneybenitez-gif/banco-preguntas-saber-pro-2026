
package co.edu.unicauca.saberpro.presentation.controller;

import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.service.IRegistroRevisorService;

import java.util.Objects;

/**
 * Controlador MVC para el registro administrativo
 * de docentes revisores en HU-04.
 */
public class RegistroRevisorController {

    private final IRegistroRevisorService servicio;

    /**
     * Inicializa el controlador de registro.
     *
     * @param servicio servicio de registro de revisores
     */
    public RegistroRevisorController(
            IRegistroRevisorService servicio) {

        this.servicio = Objects.requireNonNull(
                servicio,
                "El servicio de registro es obligatorio."
        );
    }

    /**
     * Solicita el registro de un docente revisor.
     *
     * @param id identificador del docente
     * @param nombre nombre completo
     * @param correo correo electrónico
     * @return docente registrado
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
