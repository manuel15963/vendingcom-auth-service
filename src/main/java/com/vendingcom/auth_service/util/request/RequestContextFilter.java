package com.vendingcom.auth_service.util.request;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Filtro que captura información del request HTTP (IP, User Agent)
 * y la pone disponible en el contexto reactivo para toda la cadena de procesamiento.
 *
 * Uso en los servicios:
 * <pre>
 * RequestContext requestContext = (RequestContext) requireNonNull(ctx.get("requestContext"));
 * String clientIp = requestContext.clientIp();
 * String userAgent = requestContext.userAgent();
 * </pre>
 */
@Component
public class RequestContextFilter implements WebFilter {

    public static final String REQUEST_CONTEXT_KEY = "requestContext";

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        RequestContext requestContext = RequestContext.from(exchange);

        return chain.filter(exchange)
                .contextWrite(Context.of(REQUEST_CONTEXT_KEY, requestContext));
    }

}


