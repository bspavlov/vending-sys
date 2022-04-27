package com.vending.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vending.dto.Credentials;
import com.vending.security.DatabaseAuthenticationProvider;
import com.vending.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Resource
	private DatabaseAuthenticationProvider authenticationProvider;
	@Resource
	private UserService userService;
	@Resource
	private SessionRegistry sessionRegistry;

	@PostMapping("/login")
	public ResponseEntity<Void> auth(@RequestBody @Valid Credentials credentials,
			HttpServletResponse response) {
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(credentials.getUsername(),
				credentials.getPassword());
		authenticationProvider.authenticate(auth);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@GetMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@GetMapping("/logout/all")
	public ResponseEntity<Void> logoutAll(HttpServletRequest request) {
		String principal = userService.getCurrentPrincipal();
		sessionRegistry.getAllSessions(principal, false).forEach(s -> {
			s.expireNow();
		});
		;
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@GetMapping("/active-sessions")
	public int getActiveSessions() {
		String principal = userService.getCurrentPrincipal();
		return sessionRegistry.getAllSessions(principal, false).size();
	}
}
