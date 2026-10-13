
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.domain.repository.IAsignacionRevisionRepository;

import java.util.Objects;

/**
 * Implementa el registro administrativo
 * de docentes revisores de HU-04.
 *
 * Valida los permisos, los datos del docente
 * y la existencia de registros duplicados.
 */
public class RegistroRevisorServiceImpl
        implements IRegistroRevisorService {

    private final IAsignacionRevisionRepository repositorio;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorio persistencia de revisores
     * @param sesion usuario que realiza el registro
     */
    public RegistroRevisorServiceImpl(
            IAsignacionRevisionRepository repositorio,
            SesionUsuario sesion) {

        this.repositorio = Objects.requireNonNull(
                repositorio,
                "El repositorio es obligatorio."
        );

        this.sesion = Objects.requireNonNull(
                sesion,
                "La sesión es obligatoria."
        );
    }

    /**
     * Registra un revisor cuando el usuario
     * tiene permisos de administrador.
     *
     * @param id identificador del docente
     * @param nombre nombre del docente
     * @param correo correo electrónico
     * @return revisor registrado
     */
    @Override
    public Revisor registrarRevisor(
            String id,
            String nombre,
            String correo) {

        // 1. Verificar autorización.
        if (sesion.rol() != RolUsuario.ADMINISTRADOR) {

            throw new SecurityException(
                    "Solo los administradores pueden "
                    + "registrar revisores."
            );
        }

        // 2. Eliminar espacios al inicio y al final.
        String idLimpio = id == null
                ? null
                : id.strip();

        String nombreLimpio = nombre == null
                ? null
                : nombre.strip();

        String correoLimpio = correo == null
                ? null
                : correo.strip();

        // 3. Validar los datos mediante el modelo.
        Revisor revisor = new Revisor(
                idLimpio,
                nombreLimpio,
                correoLimpio
        );

        // 4. Evitar identificadores duplicados.
        if (repositorio.buscarRevisorPorId(
                revisor.id()
        ).isPresent()) {

            throw new IllegalArgumentException(
                    "Ya existe un revisor con ese identificador."
            );
        }

        // 5. Evitar correos duplicados.
        boolean correoRegistrado =
                repositorio.listarRevisores()
                        .stream()
                        .anyMatch(existente ->
                                existente.correo()
                                        .equalsIgnoreCase(
                                                revisor.correo()
                                        )
                        );

        if (correoRegistrado) {

            throw new IllegalArgumentException(
                    "Ya existe un revisor con ese correo."
            );
        }

        // 6. Guardar el nuevo docente en SQLite.
        repositorio.guardarRevisor(revisor);

        return revisor;
    }
}
