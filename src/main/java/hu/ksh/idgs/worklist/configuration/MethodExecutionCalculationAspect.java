package hu.ksh.idgs.worklist.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

@Configuration
@Aspect
public class MethodExecutionCalculationAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodExecutionCalculationAspect.class);

	@Around("execution(* hu.ksh.idgs.worklist.api.controller.*.*(..))")
	public Object executionTime(final ProceedingJoinPoint joinPoint) throws Throwable {

		final StopWatch watch = new StopWatch();
		try {
			watch.start();
			return joinPoint.proceed();
		} finally {
			watch.stop();

			MethodExecutionCalculationAspect.LOGGER.info("Function: {}, execution time: {}",
					joinPoint.getSignature().getName(), watch.getTotalTimeMillis());

		}
	}

}
