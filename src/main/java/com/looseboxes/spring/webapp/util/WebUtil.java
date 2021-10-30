package com.looseboxes.spring.webapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

public final class WebUtil {

    private static final Logger log = LoggerFactory.getLogger(WebUtil.class);

    private static final String[] HEADERS_TO_TRY = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR" };

    public static String getClientIpAddress(ServletRequest request, String resultIfNone) {
        String ip = null;
        try {
            if(request instanceof HttpServletRequest) {
                HttpServletRequest httpServletRequest = (HttpServletRequest)request;
                for (String header : HEADERS_TO_TRY) {
                    ip = httpServletRequest.getHeader(header);
                    if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                        if (ip.contains(",")) {
                            ip = ip.split(",")[0];
                        }
                        break;
                    }
                }
            }
        }catch(Exception e) {
            log.warn("Error resolving ip address", e);
        }
        if(ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip == null ? resultIfNone : ip;
    }

    // To prevent the arbitrary creation of HttpSession via HttpServletRequest.getSession
    // we first try to access the HttpSession via the RequestContextHolder
    public static HttpSession getSession(HttpServletRequest request) {
        return getSession(request, true);
    }

    public static Optional<HttpSession> getSessionOptional(HttpServletRequest request) {
        return Optional.ofNullable(getSession(request, false));
    }

    private static @Nullable HttpSession getSession(HttpServletRequest request, boolean create) {
        return getCurrentSessionOptional(create).orElseGet(() -> request.getSession(create));
    }

    public static Optional<HttpSession> getCurrentSessionOptional(boolean create) {
        return getCurrentHttpRequestOptional().map(current -> current.getSession(create));
    }

    public static Optional<HttpServletRequest> getCurrentHttpRequestOptional() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .filter(ServletRequestAttributes.class::isInstance)
            .map(ServletRequestAttributes.class::cast)
            .map(ServletRequestAttributes::getRequest);
    }
}
