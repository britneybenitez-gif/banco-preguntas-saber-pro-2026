
package co.edu.unicauca.saberpro.infrastructure.notification;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.model.Revisor;
import co.edu.unicauca.saberpro.domain.service.INotificadorRevision;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Objects;
import java.util.Properties;

/**
 * Envía notificaciones de asignación mediante correo SMTP.
 */
public class NotificadorRevisionEmail
        implements INotificadorRevision {

    private final Session sesionCorreo;
    private final String remitente;

    /**
     * Configura la conexión con el servidor SMTP.
     *
     * @param servidor dirección del servidor SMTP
     * @param puerto puerto de conexión
     * @param usuario usuario de autenticación
     * @param contrasena contraseña de autenticación
     * @param remitente dirección del correo remitente
     */
    public NotificadorRevisionEmail(
            String servidor,
            int puerto,
            String usuario,
            String contrasena,
            String remitente) {

        validarTexto(servidor, "El servidor SMTP es obligatorio.");
        validarTexto(usuario, "El usuario SMTP es obligatorio.");
        validarTexto(contrasena, "La contraseña SMTP es obligatoria.");
        validarTexto(remitente, "El remitente es obligatorio.");

        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException(
                    "El puerto SMTP no es válido."
            );
        }

        this.remitente = remitente;

        Properties propiedades = new Properties();

        propiedades.put("mail.smtp.host", servidor);
        propiedades.put("mail.smtp.port", String.valueOf(puerto));
        propiedades.put("mail.smtp.auth", "true");

        // Exige una conexión cifrada con STARTTLS.
        propiedades.put("mail.smtp.starttls.enable", "true");
        propiedades.put("mail.smtp.starttls.required", "true");

        // Evita esperas indefinidas por problemas de conexión.
        propiedades.put("mail.smtp.connectiontimeout", "10000");
        propiedades.put("mail.smtp.timeout", "10000");
        propiedades.put("mail.smtp.writetimeout", "10000");

        this.sesionCorreo = Session.getInstance(
                propiedades,
                new Authenticator() {

                    /**
                     * Devuelve las credenciales SMTP.
                     */
                    @Override
                    protected PasswordAuthentication
                            getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                usuario,
                                contrasena
                        );
                    }
                }
        );
    }

    /**
     * Crea el notificador usando variables de entorno.
     *
     * @return notificador configurado
     */
    public static NotificadorRevisionEmail desdeEntorno() {

        String valorPuerto = System.getenv()
                .getOrDefault("SMTP_PORT", "587");

        int puerto;

        try {
            puerto = Integer.parseInt(valorPuerto);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "SMTP_PORT debe ser un número válido.",
                    e
            );
        }

        String usuario = System.getenv("SMTP_USER");

        String remitente = System.getenv()
                .getOrDefault("SMTP_FROM", usuario);

        return new NotificadorRevisionEmail(
                System.getenv("SMTP_HOST"),
                puerto,
                usuario,
                System.getenv("SMTP_PASSWORD"),
                remitente
        );
    }

    /**
     * Envía un correo al revisor asignado.
     *
     * @param revisor destinatario
     * @param pregunta pregunta que debe revisar
     */
    @Override
    public void notificarAsignacion(
            Revisor revisor,
            Pregunta pregunta) {

        Objects.requireNonNull(
                revisor,
                "El revisor es obligatorio."
        );

        Objects.requireNonNull(
                pregunta,
                "La pregunta es obligatoria."
        );

        try {

            MimeMessage mensaje = prepararMensaje(
                    revisor,
                    pregunta
            );

            Transport.send(mensaje);

        } catch (MessagingException e) {

            throw new IllegalStateException(
                    "No se pudo enviar el correo de notificación.",
                    e
            );
        }
    }

    /**
     * Construye el correo sin enviarlo.
     *
     * Permite comprobar su contenido en pruebas unitarias.
     *
     * @param revisor destinatario
     * @param pregunta pregunta asignada
     * @return mensaje preparado
     * @throws MessagingException si el mensaje no es válido
     */
    MimeMessage prepararMensaje(
            Revisor revisor,
            Pregunta pregunta) throws MessagingException {

        Objects.requireNonNull(revisor);
        Objects.requireNonNull(pregunta);

        MimeMessage mensaje = new MimeMessage(sesionCorreo);

        mensaje.setFrom(
                new InternetAddress(remitente)
        );

        mensaje.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(revisor.correo())
        );

        mensaje.setSubject(
                "Nueva pregunta asignada para revisión",
                "UTF-8"
        );

        String contenido = """
                Banco de Preguntas Saber Pro

                Estimado(a) %s:

                Se le ha asignado una nueva pregunta
                para revisión académica.

                Identificador: %s

                Pregunta:
                %s

                Por favor, ingrese al sistema para
                consultar la pregunta asignada.

                Este mensaje fue generado por el
                Banco de Preguntas Saber Pro.
                """.formatted(
                revisor.nombre(),
                pregunta.getId(),
                pregunta.getPreguntaDirecta()
        );

        mensaje.setText(contenido, "UTF-8");

        return mensaje;
    }

    /**
     * Verifica que un dato de configuración no esté vacío.
     *
     * @param valor valor que se desea validar
     * @param mensaje descripción del error
     */
    private void validarTexto(
            String valor,
            String mensaje) {

        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
