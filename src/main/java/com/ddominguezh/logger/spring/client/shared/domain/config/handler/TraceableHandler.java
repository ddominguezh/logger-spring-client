package com.ddominguezh.logger.spring.client.shared.domain.config.handler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import com.ddominguezh.logger.core.shared.domain.LoggingAppender;
import com.ddominguezh.logger.core.shared.infrastructure.Log4jLogginAppender;

@Aspect
@Component
public class TraceableHandler {

	private static LoggingAppender logger = new Log4jLogginAppender();
	private Instant start;

	@Before(value = "@annotation(com.ddominguezh.logger.spring.client.shared.domain.config.annotations.Traceable)")
	public void startMethod(JoinPoint joinPoint) {
		this.start = Instant.now();
		CodeSignature signature = (CodeSignature) joinPoint.getSignature();
		logger.info(signature.getDeclaringTypeName(), signature.getName(), "Start");
		String[] parameters = signature.getParameterNames();
		for(int i = 0 ; i < parameters.length ; i++) {
			String parameterValue = joinPoint.getArgs()[i] != null ? joinPoint.getArgs()[i].toString() : "null";
			logger.debug(signature.getDeclaringTypeName(), signature.getName(), parameters[i] + "=" + parameterValue);
		}
	}

	@After(value = "@annotation(com.ddominguezh.logger.spring.client.shared.domain.config.annotations.Traceable)")
	public void endMethod(JoinPoint joinPoint) {
		CodeSignature signature = (CodeSignature) joinPoint.getSignature();
		logger.info(signature.getDeclaringTypeName(), signature.getName(), "End. Duration: " + ChronoUnit.SECONDS.between(start, Instant.now())  + " seconds");
	}
	
	@AfterThrowing(value = "@annotation(com.ddominguezh.spring.client.core.shared.domain.config.annotations.Traceable)", throwing = "exception")
	public void handlerException(JoinPoint joinPoint, Throwable exception) {
		CodeSignature signature = (CodeSignature) joinPoint.getSignature();
		logger.error(signature.getDeclaringTypeName(), signature.getName(), exception);
	}
	
}

