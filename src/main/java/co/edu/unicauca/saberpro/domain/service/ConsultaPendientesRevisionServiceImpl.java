
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;

import co.edu.unicauca.saberpro.domain.repository.IConsultaPendientesRevisionRepository;

import java.util.List;
import java.util.Objects;

/**
 * Implementa la consulta administrativa de preguntas
 * pendientes de revisión para HU-04.
 */
public class ConsultaPendientesRevisionServiceImpl
        implements IConsultaPendientesRevisionService {

    private final IConsultaPendientesRevisionRepository repositorio;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorio repositorio de preguntas pendientes
     * @param sesion usuario que realiza la consulta
     */
    public ConsultaPendientesRevisionServiceImpl(
            IConsultaPendientesRevisionRepository repositorio,
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
     * Consulta las preguntas pendientes de todos los autores.
     *
     * Solo permite el acceso a administradores y valida
     * los parámetros de paginación.
     *
     * @param pagina número de página solicitado
     * @param tamanoPagina cantidad de preguntas por página
     * @return preguntas y datos de paginación
     */
    @Override
    public PaginaPreguntas listarPendientes(
            int pagina,
            int tamanoPagina) {

        // 1. Verificar permisos.
        if (sesion.rol() != RolUsuario.ADMINISTRADOR) {

            throw new SecurityException(
                    "Solo los administradores pueden "
                    + "consultar las preguntas pendientes."
            );
        }

        // 2. Validar número de página.
        if (pagina < 1) {

            throw new IllegalArgumentException(
                    "El número de página debe ser "
                    + "mayor o igual a 1."
            );
        }

        // 3. Validar tamaño de página.
        if (tamanoPagina < 1 || tamanoPagina > 100) {

            throw new IllegalArgumentException(
                    "El tamaño de página debe estar "
                    + "entre 1 y 100."
            );
        }

        // 4. Contar todas las preguntas pendientes.
        long totalRegistros =
                repositorio.contarPendientes();

        // 5. Calcular cuántas páginas existen.
        long cantidadPaginas = totalRegistros == 0
                ? 0
                : 1 + (totalRegistros - 1) / tamanoPagina;

        if (cantidadPaginas > Integer.MAX_VALUE) {

            throw new IllegalStateException(
                    "La cantidad de páginas supera "
                    + "el límite permitido."
            );
        }

        int totalPaginas = (int) cantidadPaginas;

        // 6. Calcular el desplazamiento.
        long inicio =
                ((long) pagina - 1) * tamanoPagina;

        if (inicio > Integer.MAX_VALUE) {

            throw new IllegalArgumentException(
                    "El desplazamiento solicitado "
                    + "es demasiado grande."
            );
        }

        int desplazamiento = (int) inicio;

        // 7. Consultar la página solicitada.
        List<Pregunta> preguntas =
                repositorio.listarPendientes(
                        tamanoPagina,
                        desplazamiento
                );

        // 8. Devolver los resultados.
        return new PaginaPreguntas(
                preguntas,
                totalRegistros,
                pagina,
                totalPaginas
        );
    }
}
