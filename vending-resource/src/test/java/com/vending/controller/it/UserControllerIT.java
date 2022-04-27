package com.vending.controller.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Sets;
import com.vending.dto.Credentials;
import com.vending.dto.MessageDto;
import com.vending.dto.RoleDto;
import com.vending.dto.UserDto;
import com.vending.entity.VendingRole;

@ActiveProfiles({ "development" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserControllerIT extends BaseControllerIT {

	@Test
	public void testEndpoints() {

		Credentials credentials = Credentials.builder().username("admin@vending.com").password("vendiNG2021").build();
		String sessionId = login(credentials);

		// get active sessions
		HttpEntity<?> entity = getHttpEntityWithSessionCookie(sessionId);
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl("http://localhost:" + port + "/auth/active-sessions");
		URI uri = builder.build().encode().toUri();
		ResponseEntity<Integer> sResponse = template.exchange(uri, HttpMethod.GET, entity, Integer.class);
		assertTrue(sResponse.getBody() > 0);

		// logout all
		logoutAll(entity);

		credentials = Credentials.builder().username("admin@vending.com").password("vendiNG2021").build();
		sessionId = login(credentials);

		entity = getHttpEntityWithSessionCookie(sessionId);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/auth/active-sessions");
		uri = builder.build().encode().toUri();
		sResponse = template.exchange(uri, HttpMethod.GET, entity, Integer.class);
		assertEquals(1, sResponse.getBody());

		// create buyer user
		UserDto user = UserDto.builder().email("abv@test.com").firstName("Client").lastName("Buyer")
				.password("buyER2022")
				.roles(Sets.newHashSet(RoleDto.builder().name(VendingRole.ROLE_BUYER.name()).build())).build();
		entity = getHttpEntityWithSessionCookie(user, sessionId);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users");
		uri = builder.build().encode().toUri();
		ResponseEntity<UserDto> response = template.exchange(uri, HttpMethod.POST, entity, UserDto.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("abv@test.com", response.getBody().getEmail());
		assertEquals("Client", response.getBody().getFirstName());
		assertTrue(response.getBody().getRoles().stream()
				.anyMatch(r -> r.getName().equals(VendingRole.ROLE_BUYER.name())));

		// get created user
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/by-email").queryParam("email",
				response.getBody().getEmail());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		response = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, UserDto.class);
		assertEquals("admin@vending.com", response.getBody().getCreatedBy());
		assertNotNull(response.getBody().getCreatedDate());
		assertNotNull(response.getBody().getCreatedBy());
		assertNotNull(response.getBody().getLastModifiedDate());

		// edit user
		user = response.getBody();
		user.setFirstName("New name");
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users");
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(user, sessionId);
		response = template.exchange(uri, HttpMethod.PUT, entity, UserDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("abv@test.com", response.getBody().getEmail());
		assertEquals("New name", response.getBody().getFirstName());
		assertTrue(response.getBody().getRoles().stream()
				.anyMatch(r -> r.getName().equals(VendingRole.ROLE_BUYER.name())));

		// get edited user
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/by-email").queryParam("email",
				response.getBody().getEmail());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		response = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, UserDto.class);
		assertEquals("admin@vending.com", response.getBody().getCreatedBy());
		assertEquals("admin@vending.com", response.getBody().getLastModifiedBy());
		assertNotNull(response.getBody().getCreatedDate());
		assertTrue(response.getBody().getLastModifiedDate().isAfter(response.getBody().getCreatedDate()));

		// delete user
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/" + response.getBody().getId());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		ResponseEntity<Void> deleteResponse = template.exchange(uri, HttpMethod.DELETE, entity, Void.class);
		assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

		// try to get deleted user
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/by-email").queryParam("email",
				response.getBody().getEmail());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		ResponseEntity<MessageDto> errorResponse = template.exchange(builder.build().encode().toUri(), HttpMethod.GET,
				entity, MessageDto.class);
		assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatusCode());

		// logout
		logout(entity);

		// try to get user
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/by-email").queryParam("email",
				response.getBody().getEmail());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		errorResponse = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, MessageDto.class);
		assertEquals(HttpStatus.UNAUTHORIZED, errorResponse.getStatusCode());
	}

}
