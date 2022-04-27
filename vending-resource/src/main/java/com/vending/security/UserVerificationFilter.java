package com.vending.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vending.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
public class UserVerificationFilter extends OncePerRequestFilter {

	@Resource
	private UserService userService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
//			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//			if (auth != null && auth.getPrincipal() instanceof String) {
//				String email = (String) auth.getPrincipal();
//				User user = userService.get(email);
//				DatabaseAuthenticationProvider.setAuthentication(user);
//			}
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setHeader("Access-Control-Allow-Origin", "*");
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		return path.startsWith("/auth/");
	}

}
