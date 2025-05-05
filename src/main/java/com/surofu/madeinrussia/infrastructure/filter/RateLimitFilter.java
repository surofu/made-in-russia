package com.surofu.madeinrussia.infrastructure.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${app.rate.limit.capacity}")
    private int bucketCapacity = 100;

    @Value("${app.rate.limit.refill.tokens}")
    private int refillTokens = 10;

    @Value("${app.rate.limit.refill.minutes}")
    private int refillMinutes = 1;

    @Value("${app.rate.limit.whitelist}")
    private List<String> whitelist = new ArrayList<>();

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Игнорируем лимитирование для white-list IP
        if (isWhitelisted(request.getRemoteAddr())) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = getClientKey(request); // IP + API Key если есть
        Bucket bucket = buckets.computeIfAbsent(clientKey, k -> createNewBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds",
                    String.valueOf(TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill())));
            response.getWriter().write("Rate limit exceeded. Try again in " +
                    TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill()) + " seconds");
        }
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.intervally(refillTokens, Duration.ofMinutes(refillMinutes));
        Bandwidth limit = Bandwidth.classic(bucketCapacity, refill);
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private String getClientKey(HttpServletRequest request) {
        String apiKey = request.getHeader("API-Key");
        return (apiKey != null) ? apiKey : request.getRemoteAddr();
    }

    private boolean isWhitelisted(String ip) {
        return whitelist.contains(ip);
    }
}
