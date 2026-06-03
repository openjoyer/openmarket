package com.openjoyer.openmarket.order_service.interfaces.rest;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Before("@annotation(loggable)")
    public void before(JoinPoint joinPoint, Loggable loggable) {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        log.info("[order-service]: [{}] - {}", ts, joinPoint.getSignature().getName());
    }
}
