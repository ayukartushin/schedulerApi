package su.kartushin.busAPI.utils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class RequestIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestID = httpRequest.getHeader("requestID");

        if (requestID == null || requestID.isEmpty()) {
            requestID = UUID.randomUUID().toString();
        }

        try {
            MDC.put("requestID", requestID);
            chain.doFilter(request, response);
        } finally {
            MDC.remove("requestID");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}