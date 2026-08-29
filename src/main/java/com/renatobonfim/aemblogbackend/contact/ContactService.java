package com.renatobonfim.aemblogbackend.contact;

import com.renatobonfim.aemblogbackend.notification.ContactEmailRequestedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ContactService {

    private final JavaMailSender javaMailSender;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final String mailFrom;
    private final String mailPersonal;

    public ContactService(
            JavaMailSender javaMailSender,
            ApplicationEventPublisher applicationEventPublisher,
            @Value("${app.config.email.from}") String mailFrom,
            @Value("${app.config.email.personal}") String mailPersonal) {

        this.javaMailSender = javaMailSender;
        this.applicationEventPublisher = applicationEventPublisher;
        this.mailFrom = mailFrom;
        this.mailPersonal = mailPersonal;
    }

    public void sendEmail(Contact contact) {
        applicationEventPublisher.publishEvent(new ContactEmailRequestedEvent(
                contact.getName(),
                contact.getEmail(),
                contact.getMessage()
        ));
    }

    @Retryable(retryFor = { MessagingException.class, MailException.class }, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void deliverContactEmail(Contact contact) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(mailFrom);
        helper.setReplyTo(mailFrom);
        helper.setTo(contact.getEmail());
        helper.setBcc(mailPersonal);
        helper.setSubject("AEM Secrets - Contact form received");
        helper.setText(buildPlainText(contact), buildHtml(contact));

        log.debug("Sending email to {} ", contact.getEmail());
        javaMailSender.send(mimeMessage);
    }

    private String buildPlainText(Contact contact) {
        return String.format("""
                Hi %s,

                I got your message. I'll get back to you as soon as I can.

                ----
                Your name: %s
                Your email: %s
                Message:
                %s
                ----

                — AEM Secrets
                """,
                contact.getName(),
                contact.getName(),
                contact.getEmail(),
                contact.getMessage());
    }

    private String buildHtml(Contact contact) {
        String name = escapeHtml(contact.getName());
        String email = escapeHtml(contact.getEmail());
        String message = escapeHtml(contact.getMessage()).replace("\n", "<br/>");

        return """
                <!DOCTYPE html>
                    <html>
                    <body style="margin:0;padding:0;background:#f4f4f5;font-family:Roboto,Verdana,Arial;color:#db6b18;">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f5;padding:32px 16px;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="560" cellpadding="0" cellspacing="0" style="background:#faf3e5;border-radius:8px;overflow:hidden;">
                                    <tr>
                                        <td style="background:#412e1d;color:#faf3e5;padding:20px 28px;font-size:18px;font-weight:bold;">
                                            AEM Secrets
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:28px;">
                                            <p style="margin:0 0 16px;font-size:16px;line-height:1.5;">
                                                Hi <strong>%s</strong>,
                                            </p>
                                            <p style="margin:0 0 20px;font-size:15px;line-height:1.6;">
                                                I got your message. I'll get back to you as soon as I can.
                                            </p>
                                            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f9fafb;border:1px solid #e5e7eb;border-radius:6px;">
                                                <tr>
                                                    <td style="padding:16px 18px;font-size:14px;line-height:1.6;">
                                                        <p style="margin:0 0 8px;"><strong>Your name:</strong> %s</p>
                                                        <p style="margin:0 0 8px;"><strong>Your email:</strong> %s</p>
                                                        <p style="margin:0 0 6px;"><strong>Message:</strong></p>
                                                        <p style="margin:0;white-space:pre-wrap;">%s</p>
                                                    </td>
                                                </tr>
                                            </table>
                                            <h2 style="margin:24px 0 0;font-size:13px;color:#db6b18; font-weight: bold">
                                                — AEM Secrets
                                            </h2>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                    </body>
                </html>
                """.formatted(name, name, email, message);
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
