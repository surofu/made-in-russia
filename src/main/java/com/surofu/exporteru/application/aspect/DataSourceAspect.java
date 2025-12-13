package com.surofu.exporteru.application.aspect;

import com.surofu.exporteru.application.utils.DatabaseContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
@Profile({"prod", "prod-test"})
@Slf4j
public class DataSourceAspect {
  @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
  public void setDataSource(JoinPoint ignoredJoinPoint) {
    log.debug("Use datasource: {}",
        TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master");
    if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
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
