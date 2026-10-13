
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Revisor;

/**
 * Define las operaciones de registro
 * de docentes revisores para HU-04.
 */
public interface IRegistroRevisorService {

    /**
     * Registra un nuevo docente revisor.
     *
     * @param id identificador del revisor
     * @param nombre nombre completo del docente
     * @param correo dirección de correo electrónico
     * @return revisor registrado
     */
    Revisor registrarRevisor(
            String id,
            String nombre,
            String correo
    );
}
