
package co.edu.unicauca.saberpro.domain.service;

import co.edu.unicauca.saberpro.domain.model.EstadoPregunta;
import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.RolUsuario;
import co.edu.unicauca.saberpro.domain.model.SesionUsuario;
import co.edu.unicauca.saberpro.domain.repository.IConsultaPreguntaRepository;

import java.util.List;
import java.util.Objects;

/**
 * Implementa la consulta paginada de preguntas del autor.
 */
public class ConsultaPreguntaServiceImpl
        implements IConsultaPreguntaService {

    private final IConsultaPreguntaRepository repositorio;
    private final SesionUsuario sesion;

    /**
     * Inicializa el servicio con sus dependencias.
     *
     * @param repositorio repositorio de consulta de preguntas
     * @param sesion usuario que realiza la consulta
     */
    public ConsultaPreguntaServiceImpl(
            IConsultaPreguntaRepository repositorio,
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
     * Consulta las preguntas del autor con filtros y paginación.
     *
     * @param estado estado solicitado o null para todos
     * @param dificultad dificultad solicitada o null para todas
     * @param pagina número de página, comenzando en 1
     * @param tamanoPagina cantidad de preguntas por página
     * @return resultados y datos de paginación
     */
    @Override
    public PaginaPreguntas listarMisPreguntas(
            EstadoPregunta estado,
            String dificultad,
            int pagina,
            int tamanoPagina) {

        if (sesion.rol() != RolUsuario.AUTOR) {
            throw new SecurityException(
                    "Solo los autores pueden consultar sus preguntas."
            );
        }

        if (pagina < 1) {
            throw new IllegalArgumentException(
                    "El número de página debe ser mayor o igual a 1."
            );
        }

        if (tamanoPagina < 1 || tamanoPagina > 100) {
            throw new IllegalArgumentException(
                    "El tamaño de página debe estar entre 1 y 100."
            );
        }

        String filtroDificultad =
                dificultad == null || dificultad.isBlank()
                        ? null
                        : dificultad.trim();

        String autorId = sesion.usuarioId();

        long totalRegistros = repositorio.contarPorAutor(
                autorId,
                estado,
                filtroDificultad
        );

        long cantidadPaginas = totalRegistros == 0
                ? 0
                : 1 + (totalRegistros - 1) / tamanoPagina;

        if (cantidadPaginas > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "La cantidad de páginas supera el límite permitido."
            );
        }

        int totalPaginas = (int) cantidadPaginas;

        long inicio = ((long) pagina - 1) * tamanoPagina;

        if (inicio > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "El desplazamiento solicitado es demasiado grande."
            );
        }

        int desplazamiento = (int) inicio;

        List<Pregunta> preguntas = repositorio.listarPorAutor(
                autorId,
                estado,
                filtroDificultad,
                tamanoPagina,
                desplazamiento
        );

        return new PaginaPreguntas(
                preguntas,
                totalRegistros,
                pagina,
                totalPaginas
        );
    }
}
