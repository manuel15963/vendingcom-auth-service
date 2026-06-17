package com.vendingcom.auth_service.util.request;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * Utility para extraer información del request HTTP
 * (IP del cliente, User Agent, etc.)
 */
public class RequestContextUtils {

    private static final String UNKNOWN_IP = "UNKNOWN";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_REAL_IP = "X-Real-IP";

    /*
     * Headers adicionales usados por algunos proxies/CDN.
     * Render normalmente puede enviar X-Forwarded-For.
     */
    private static final String CF_CONNECTING_IP = "CF-Connecting-IP";
    private static final String TRUE_CLIENT_IP = "True-Client-IP";

    /**
     * Extrae la dirección IP real del cliente desde el request.
     *
     * Orden:
     * 1. X-Forwarded-For
     * 2. X-Real-IP
     * 3. CF-Connecting-IP
     * 4. True-Client-IP
     * 5. RemoteAddress
     */
    public static String extractClientIp(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        String xForwardedFor = headers.getFirst(X_FORWARDED_FOR);
        if (hasText(xForwardedFor)) {
            String[] ips = xForwardedFor.split(",");
            if (ips.length > 0 && hasText(ips[0])) {
                return normalizeIp(ips[0].trim());
            }
        }

        String xRealIp = headers.getFirst(X_REAL_IP);
        if (hasText(xRealIp)) {
            return normalizeIp(xRealIp.trim());
        }

        String cfConnectingIp = headers.getFirst(CF_CONNECTING_IP);
        if (hasText(cfConnectingIp)) {
            return normalizeIp(cfConnectingIp.trim());
        }

        String trueClientIp = headers.getFirst(TRUE_CLIENT_IP);
        if (hasText(trueClientIp)) {
            return normalizeIp(trueClientIp.trim());
        }

        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            return normalizeIp(remoteAddress.getAddress().getHostAddress());
        }

        return UNKNOWN_IP;
    }

    /**
     * Extrae el User Agent del request.
     */
    public static String extractUserAgent(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String userAgent = request.getHeaders().getFirst(HttpHeaders.USER_AGENT);
        return Optional.ofNullable(userAgent)
                .filter(RequestContextUtils::hasText)
                .orElse("UNKNOWN");
    }

    /**
     * Normaliza IPs locales para que en auditoría no salga:
     * 0:0:0:0:0:0:0:1
     */
    private static String normalizeIp(String ip) {
        if (!hasText(ip)) {
            return UNKNOWN_IP;
        }

        String cleanIp = ip.trim();

        if ("0:0:0:0:0:0:0:1".equals(cleanIp) || "::1".equals(cleanIp)) {
            return "127.0.0.1";
        }

        return cleanIp;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}