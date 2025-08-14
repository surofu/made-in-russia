package com.surofu.madeinrussia.infrastructure.filter;

import com.surofu.madeinrussia.application.model.session.SessionInfo;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${RATE_LIMIT_CAPACITY:40}")
    private int bucketCapacity;

    @Value("${RATE_LIMIT_REFILL_TOKENS:10}")
    private int refillTokens;

    @Value("${RATE_LIMIT_REFILL_SECONDS:5}")
    private int refillSeconds;

    @Value("${RATE_LIMIT_WHITE_LIST:}")
    private List<String> whitelist;

    @Value("${SESSION_SECRET:}")
    private String sessionSecret;

    private final Map<String, BucketWithTimestamp> buckets = new ConcurrentHashMap<>();

    private static final long BUCKET_TTL_MS = TimeUnit.HOURS.toMillis(1);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String xInternalRequestHeader = request.getHeader("X-Internal-Request");
        if (sessionSecret.equals(xInternalRequestHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        if (whitelist.contains(ip)) {
            filterChain.doFilter(request, response);
            return;
        }

        SessionInfo sessionInfo = SessionInfo.of(request);
        String key = sessionInfo.getDeviceId().toString().concat(sessionInfo.getIpAddress().toString());

        BucketWithTimestamp bucketWithTimestamp = buckets.compute(key, (k, v) -> {
            if (v == null || v.isExpired()) {
                return new BucketWithTimestamp(createNewBucket());
            }
            return v;
        });

        Bucket bucket = bucketWithTimestamp.getBucket();

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            log.info("Request from: {} | {}; Limit: {}", sessionInfo.getUserAgent().getBrowser().getName(), sessionInfo.getUserAgent().getOperatingSystem().getName(), probe.getRemainingTokens());
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            log.info("Request from: {} | {}; BLOCKED!!!", sessionInfo.getUserAgent().getBrowser().getName(), sessionInfo.getUserAgent().getOperatingSystem().getName());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            long waitSeconds = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitSeconds));
            response.getWriter().write("Rate limit exceeded. Try again in " + waitSeconds + " seconds");
        }
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(bucketCapacity)
                .refillIntervally(refillTokens, Duration.ofSeconds(refillSeconds))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private static class BucketWithTimestamp {
        private final Bucket bucket;
        private final AtomicLong lastAccessTime;

        public BucketWithTimestamp(Bucket bucket) {
            this.bucket = bucket;
            this.lastAccessTime = new AtomicLong(System.currentTimeMillis());
        }

        public Bucket getBucket() {
            lastAccessTime.set(System.currentTimeMillis());
            return bucket;
        }

        public boolean isExpired() {
            return (System.currentTimeMillis() - lastAccessTime.get()) > BUCKET_TTL_MS;
        }
    }
}