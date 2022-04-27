package com.vending.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vending.aop.LoggingPointcutAdvisor;

@Configuration
public class AppConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}

	@Bean
	public LoggingPointcutAdvisor loggingPointcutAdvisor() {
		return new LoggingPointcutAdvisor(
				"execution(* com.vending.repo..*.*(..)) || execution(* com.vending.service..*.*(..)) "
						+ "|| execution(* com.vending.controller..*.*(..)) || execution(* com.vending.dto..*.*(..))");
	}

}
