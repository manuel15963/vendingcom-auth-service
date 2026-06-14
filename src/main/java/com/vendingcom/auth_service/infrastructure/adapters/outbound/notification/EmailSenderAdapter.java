package com.vendingcom.auth_service.infrastructure.adapters.outbound.notification;

import com.vendingcom.auth_service.application.port.output.notification.EmailSenderPort;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;

@Component
public class EmailSenderAdapter implements EmailSenderPort {

    private static final String PASSWORD_RECOVERY_TEMPLATE = "templates/password-recovery-email.html";

    private final JavaMailSender javaMailSender;

    public EmailSenderAdapter(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public Mono<Void> sendPasswordRecoveryCode(String to, String code, Integer expirationMinutes) {
        return Mono.fromRunnable(() -> sendEmail(to, code, expirationMinutes))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private void sendEmail(String to, String code, Integer expirationMinutes) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Código de recuperación de contraseña - VendingCom");

            String html = loadTemplate()
                    .replace("{{code}}", code)
                    .replace("{{expirationMinutes}}", String.valueOf(expirationMinutes));

            helper.setText(html, true);

            javaMailSender.send(message);
        } catch (Exception exception) {
            throw new RuntimeException("Error al enviar correo de recuperación", exception);
        }
    }

    private String loadTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource(PASSWORD_RECOVERY_TEMPLATE);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new RuntimeException("No se pudo cargar la plantilla de correo", exception);
        }
    }
}