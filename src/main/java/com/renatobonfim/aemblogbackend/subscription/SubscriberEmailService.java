package com.renatobonfim.aemblogbackend.subscription;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SubscriberEmailService {

    private final JavaMailSender javaMailSender;
    private final String mailFrom;
    private final String unsubscribeBaseUrl;

    public SubscriberEmailService(
            JavaMailSender javaMailSender,
            @Value("${app.config.email.from}") String mailFrom,
            @Value("${app.config.subscription.unsubscribe-url}") String unsubscribeBaseUrl) {

        this.javaMailSender = javaMailSender;
        this.mailFrom = mailFrom;
        this.unsubscribeBaseUrl = unsubscribeBaseUrl;
    }

    public void sendWelcomeEmail(Subscriber subscriber) {
        String unsubscribeLink = UriComponentsBuilder.fromHttpUrl(unsubscribeBaseUrl)
                .queryParam("token", subscriber.getUnsubscribeToken())
                .toUriString();

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(mailFrom);
            helper.setTo(subscriber.getEmail());
            helper.setSubject("Welcome to AEM Secrets");
            helper.setText(buildPlainText(subscriber, unsubscribeLink), buildHtml(subscriber, unsubscribeLink));

            javaMailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            throw new MailSendException("Failed to send subscription welcome email", ex);
        }
    }

    private String buildPlainText(Subscriber subscriber, String unsubscribeLink) {
        return String.format("""
                Hi %s,

                Thanks for subscribing to AEM Secrets. You'll receive updates when new content is published.

                If you ever want to stop receiving emails, use this link to unsubscribe:
                %s

                — AEM Secrets
                """,
                subscriber.getName(),
                unsubscribeLink);
    }

    private String buildHtml(Subscriber subscriber, String unsubscribeLink) {
        String name = escapeHtml(subscriber.getName());
        String link = escapeHtml(unsubscribeLink);

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
                                Thanks for subscribing to AEM Secrets. You'll receive updates when new content is published.
                              </p>
                              <p style="margin:0 0 20px;font-size:14px;line-height:1.6;">
                                <a href="%s" style="color:#412e1d;">Unsubscribe</a>
                              </p>
                              <h2 style="margin:24px 0 0;font-size:13px;color:#db6b18;font-weight:bold;">
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
                """.formatted(name, link);
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
