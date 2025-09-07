package com.surofu.madeinrussia.application.aspect;

import com.surofu.madeinrussia.application.utils.DatabaseContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Profile("prod")
public class DataSourceAspect {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void setDataSource(JoinPoint joinPoint) {
        Transactional transactional = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getAnnotation(Transactional.class);

        if (transactional != null && transactional.readOnly()) {
            DatabaseContextHolder.setReadDataSource();
        } else {
            DatabaseContextHolder.setWriteDataSource();
        }
    }

    @AfterReturning("within(@org.springframework.stereotype.Repository *)")
    public void clearDataSource() {
        DatabaseContextHolder.clear();
    }
}
