package com.vending.aop;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
public class LoggingPointcutAdvisor extends AspectJExpressionPointcutAdvisor {

	public LoggingPointcutAdvisor(String expression) {
		super();
		setExpression(expression);
		setAdvice(new AroundAdvice());
	}

	class AroundAdvice implements MethodInterceptor {

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {

			Object result;
			String packageName = invocation.getMethod().getDeclaringClass().getPackage().getName();
			String methodName = invocation.getMethod().getName();
			long startMethodTime = System.currentTimeMillis();

			try {
				result = invocation.proceed();
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
				throw e;
			}

			long endMethodTime = System.currentTimeMillis();
			long duration = (endMethodTime - startMethodTime);

			StringBuffer logMessage = new StringBuffer();

			logMessage.append(packageName);
			logMessage.append(".");
			logMessage.append(methodName);
			logMessage.append("(");
			// append args
			Object[] args = invocation.getArguments();
			for (int i = 0; i < args.length; i++) {
				logMessage.append(args[i]).append(",");
			}
			if (args.length > 0) {
				logMessage.deleteCharAt(logMessage.length() - 1);
			}

			logMessage.append(")");
			if (result != null) {
				logMessage.append(" with return value " + result + " of type " + result.getClass());
			} else {
				logMessage.append(" with return value null");
			}

			logMessage.append(" and execution time (ms) " + duration);

			log.debug(logMessage.toString());

			return result;
		}
	}

}
