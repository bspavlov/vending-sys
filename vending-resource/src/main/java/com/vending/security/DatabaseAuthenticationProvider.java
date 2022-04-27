package com.vending.security;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.vending.entity.User;
import com.vending.service.UserService;

@Component
public class DatabaseAuthenticationProvider implements AuthenticationProvider {

	@Resource
	private UserService userService;
	@Resource
	private PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String providedEmail = authentication.getName();
		String providedPassword = String.valueOf(authentication.getCredentials());
		try {
			User user = userService.get(providedEmail);
			if (!passwordEncoder.matches(providedPassword, user.getPassword())) {
				throw new BadCredentialsException("Invalid email or password");
			}
			return setAuthentication(user);
		} catch (EntityNotFoundException e) {
			throw new UsernameNotFoundException("User with email " + providedEmail + " not found");
		}
	}

	public static Authentication setAuthentication(User user) {
		Collection<? extends GrantedAuthority> authorities = user.getRoles().stream()
				.map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
		Authentication authenticated = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
		SecurityContextHolder.getContext().setAuthentication(authenticated);
		return authenticated;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
