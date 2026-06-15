package com.vendingcom.auth_service.infrastructure.adapters.outbound.notification;

import com.vendingcom.auth_service.application.port.output.notification.EmailSenderPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class EmailSenderAdapter implements EmailSenderPort {

    private static final String PASSWORD_RECOVERY_TEMPLATE = "templates/password-recovery-email.html";
    private static final String RESEND_EMAILS_ENDPOINT = "/emails";

    private final WebClient webClient;
    private final String fromEmail;

    public EmailSenderAdapter(
            WebClient.Builder webClientBuilder,
            @Value("${email.resend.api-key}") String resendApiKey,
            @Value("${email.from}") String fromEmail
    ) {
        this.fromEmail = fromEmail;
        this.webClient = webClientBuilder
                .baseUrl("https://api.resend.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + resendApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<Void> sendPasswordRecoveryCode(String to, String code, Integer expirationMinutes) {
        String html = loadTemplate()
                .replace("{{code}}", code)
                .replace("{{expirationMinutes}}", String.valueOf(expirationMinutes));

        ResendEmailRequest request = new ResendEmailRequest(
                fromEmail,
                List.of(to),
                "Código de recuperación de contraseña - VendingCom",
                html
        );

        return webClient.post()
                .uri(RESEND_EMAILS_ENDPOINT)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .then()
                .onErrorMap(error -> new RuntimeException("Error al enviar correo de recuperación con Resend", error));
    }

    private String loadTemplate() {
        try {
            ClassPathResource resource = new ClassPathResource(PASSWORD_RECOVERY_TEMPLATE);
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (Exception exception) {
            throw new RuntimeException("No se pudo cargar la plantilla de correo", exception);
        }
    }

    private record ResendEmailRequest(
            String from,
            List<String> to,
            String subject,
            String html
    ) {
    }
}