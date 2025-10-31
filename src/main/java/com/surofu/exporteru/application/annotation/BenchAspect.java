package com.surofu.exporteru.application.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BenchAspect {
    private final Logger LOG = LoggerFactory.getLogger(BenchAspect.class);

    @Around("@annotation(benchAnnotation)")
    public Object benchMethod(ProceedingJoinPoint joinPoint, Bench benchAnnotation) throws Throwable {
        if (benchAnnotation.quantity() < 1) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        if (benchAnnotation.quantity() > 10_000) {
            throw new IllegalArgumentException("Quantity must be less than or equal to 10,000");
        }

        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        for (int i = 0; i < benchAnnotation.quantity() - 1; i++) {
            joinPoint.proceed();
        }

        long end = System.currentTimeMillis();
        long duration = end - start;
        long average = duration / benchAnnotation.quantity();

        LOG.info("{}. Bench quantity: {}; Bench execution time: {} ms; Average: {} ms",
                joinPoint.getSignature().getName(),
                benchAnnotation.quantity(),
                duration,
                average);
        return result;
    }
}
