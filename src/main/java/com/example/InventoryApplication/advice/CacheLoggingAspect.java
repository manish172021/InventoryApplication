package com.example.InventoryApplication.advice;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class CacheLoggingAspect {
	
	@Before("@annotation(org.springframework.cache.annotation.Cacheable)")
	public void logCacheable(JoinPoint joinPoint) {
	    log.info("CACHEABLE → method: {}, args: {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
	}

	@AfterReturning("@annotation(org.springframework.cache.annotation.CacheEvict)")
	public void logCacheEvict(JoinPoint joinPoint) {
	    log.info("CACHE EVICTED → method: {}, args: {}", joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
	}
}
