
package co.edu.unicauca.saberpro.infrastructure.notification;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias de las notificaciones por correo.
 */
class NotificadorRevisionEmailTest {

    /**
     * Verifica que el correo tenga el destinatario correcto.
     */
    @Test
    void debePrepararCorreoParaElRevisor() throws Exception {

        NotificadorRevisionEmail notificador = crearNotificador();

        MimeMessage mensaje = notificador.prepararMensaje(
                crearRevisor(),
                crearPregunta()
        );

        InternetAddress destinatario = (InternetAddress)
                mensaje.getRecipients(Message.RecipientType.TO)[0];

        assertEquals(
                "docente@unicauca.edu.co",
                destinatario.getAddress()
        );
    }

    /**
     * Verifica el asunto de la notificación.
     */
    @Test
    void debeIncluirAsuntoDeAsignacion() throws Exception {

        MimeMessage mensaje = crearNotificador()
                .prepararMensaje(
                        crearRevisor(),
                        crearPregunta()
                );

        assertEquals(
                "Nueva pregunta asignada para revisión",
                mensaje.getSubject()
        );
    }

    /**
     * Verifica que el correo tenga el remitente configurado.
     */
    @Test
    void debeUtilizarRemitenteConfigurado() throws Exception {

        MimeMessage mensaje = crearNotificador()
                .prepararMensaje(
                        crearRevisor(),
                        crearPregunta()
                );

        InternetAddress remitente = (InternetAddress)
                mensaje.getFrom()[0];

        assertEquals(
                "sistema@ejemplo.com",
                remitente.getAddress()
        );
    }

    /**
     * Verifica que el mensaje incluya los datos
     * necesarios para identificar la asignación.
     */
    @Test
    void debeIncluirDatosDePreguntaYRevisor() throws Exception {

        Pregunta pregunta = crearPregunta();

        MimeMessage mensaje = crearNotificador()
                .prepararMensaje(
                        crearRevisor(),
                        pregunta
                );

        String contenido = mensaje.getContent().toString();

        assertTrue(contenido.contains("Juan Perez"));

        assertTrue(
                contenido.contains(pregunta.getId())
        );

        assertTrue(
                contenido.contains(
                        pregunta.getPreguntaDirecta()
                )
        );

        assertTrue(
                contenido.contains("Banco de Preguntas Saber Pro")
        );
    }

    /**
     * Verifica que el servidor y el puerto SMTP
     * tengan una configuración válida.
     */
    @Test
    void debeRechazarConfiguracionInvalida() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new NotificadorRevisionEmail(
                        null,
                        587,
                        "usuario",
                        "clave-prueba",
                        "sistema@ejemplo.com"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new NotificadorRevisionEmail(
                        "smtp.ejemplo.com",
                        0,
                        "usuario",
                        "clave-prueba",
                        "sistema@ejemplo.com"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new NotificadorRevisionEmail(
                        "smtp.ejemplo.com",
                        587,
                        "usuario",
                        "",
                        "sistema@ejemplo.com"
                )
        );
    }

    /**
     * Construye un notificador con datos ficticios.
     *
     * @return notificador preparado para pruebas
     */
    private NotificadorRevisionEmail crearNotificador() {

        return new NotificadorRevisionEmail(
                "smtp.ejemplo.com",
                587,
                "usuario@ejemplo.com",
                "clave-prueba",
                "sistema@ejemplo.com"
        );
    }

    /**
     * Construye un docente revisor de prueba.
     *
     * @return revisor válido
     */
    private Revisor crearRevisor() {

        return new Revisor(
                "REV001",
                "Juan Perez",
                "docente@unicauca.edu.co"
        );
    }

    /**
     * Construye una pregunta pendiente de revisión.
     *
     * @return pregunta válida
     */
    private Pregunta crearPregunta() {

        Pregunta pregunta = new Pregunta(
                "AUTOR001",
                "Contexto académico de prueba",
                "¿Cuál es la respuesta correcta?",
                List.of("A", "B", "C", "D"),
                "E",
                "Justificación académica",
                "Bibliografía de prueba",
                "Lectura crítica",
                "Comprensión",
                "Interpretación",
                "Media"
        );

        pregunta.enviarARevision();

        return pregunta;
    }
}
