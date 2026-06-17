package com.vendingcom.auth_service.util.request;

/**
 * Contenedor para información del request HTTP que se pasa
 * a través del contexto reactivo de Reactor.
 */
public record RequestContext(
        String clientIp,
        String userAgent
) {
    /**
     * Crea un RequestContext completo desde un ServerWebExchange
     */
    public static RequestContext from(org.springframework.web.server.ServerWebExchange exchange) {
        String ip = RequestContextUtils.extractClientIp(exchange);
        String userAgent = RequestContextUtils.extractUserAgent(exchange);
        return new RequestContext(ip, userAgent);
    }
}

