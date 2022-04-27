package com.vending.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import com.vending.entity.VendingRole;
import com.vending.security.UnauthorizedAuthenticationEntryPoint;
import com.vending.security.UserVerificationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource
	private UnauthorizedAuthenticationEntryPoint unauthorizedHandler;

	@Bean
	public UserVerificationFilter userVerificationFilter() {
		return new UserVerificationFilter();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry(null));
		http.cors().and().csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
				.and().authorizeRequests()
				.antMatchers("/", "/favicon.ico", "/**/*.png", "/**/*.gif", "/**/*.svg", "/**/*.jpg",
						"/**/*.html", "/**/*.css", "/**/*.js")
				.permitAll().antMatchers("/swagger-ui.html").permitAll()
				.antMatchers("/webjars/**").permitAll()
				.antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/v2/api-docs/**").permitAll()
				.antMatchers(HttpMethod.POST, "/auth/login").permitAll()
				.antMatchers(HttpMethod.GET, "/auth/logout").access("!isAnonymous()")
				.antMatchers("/auth/active-sessions").access("!isAnonymous()")
				.antMatchers("/auth/logout/all").access("!isAnonymous()")
				.antMatchers("/users/current").access("!isAnonymous()")
				.antMatchers("/users/**").hasAuthority(VendingRole.ROLE_SELLER.name())
				.antMatchers(HttpMethod.POST, "/products").hasAuthority(VendingRole.ROLE_SELLER.name())
				.antMatchers(HttpMethod.PUT, "/products").hasAuthority(VendingRole.ROLE_SELLER.name())
				.antMatchers(HttpMethod.DELETE, "/products").hasAuthority(VendingRole.ROLE_SELLER.name())
				.antMatchers(HttpMethod.POST, "/deposit/**").hasAuthority(VendingRole.ROLE_BUYER.name())
				.antMatchers(HttpMethod.POST, "/buy").hasAuthority(VendingRole.ROLE_BUYER.name())
				.anyRequest().authenticated();

		http.addFilterBefore(userVerificationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public <S extends Session> SessionRegistry sessionRegistry(FindByIndexNameSessionRepository<S> sessionRepository) {
		return new SpringSessionBackedSessionRegistry<>(sessionRepository);
	}

}
