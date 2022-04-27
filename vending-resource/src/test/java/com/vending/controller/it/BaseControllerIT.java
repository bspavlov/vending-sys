package com.vending.controller.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vending.VendingResourceApplication;
import com.vending.dto.Credentials;
import com.vending.dto.MessageDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { VendingResourceApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql({ "classpath:roles.sql" })
public class BaseControllerIT {

	@LocalServerPort
	int port;
	ObjectMapper mapper = new ObjectMapper();
	TestRestTemplate template = new TestRestTemplate();

	@BeforeEach
	public void init() {
		mapper.registerModule(new JavaTimeModule());
		mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
	}

	String login(Credentials credentials) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/auth/login");
		URI uri = builder.build().encode().toUri();
		HttpEntity<?> entity = getHttpEntity(credentials);
		ResponseEntity<Void> authResponse = template.exchange(uri, HttpMethod.POST, entity, Void.class);
		System.err.println(" auth response: " + authResponse.toString());
		assertEquals(HttpStatus.OK, authResponse.getStatusCode());
		HttpHeaders headers = authResponse.getHeaders();
		String cookie = headers.getFirst(HttpHeaders.SET_COOKIE);
		cookie = cookie.substring(cookie.indexOf("=") + 1, cookie.indexOf(";"));
		return cookie;
	}

	void logout(HttpEntity<?> entity) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/auth/logout");
		URI uri = builder.build().encode().toUri();
		ResponseEntity<Void> authResponse = template.exchange(uri, HttpMethod.GET, entity, Void.class);
		System.err.println(" auth response: " + authResponse.toString());
		assertEquals(HttpStatus.OK, authResponse.getStatusCode());
	}

	void logoutAll(HttpEntity<?> entity) {
		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl("http://localhost:" + port + "/auth/logout/all");
		URI uri = builder.build().encode().toUri();
		ResponseEntity<Void> authResponse = template.exchange(uri, HttpMethod.GET, entity, Void.class);
		System.err.println(" auth response: " + authResponse.toString());
		assertEquals(HttpStatus.OK, authResponse.getStatusCode());
	}

	ResponseEntity<MessageDto> invalidLogin(Credentials credentials) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/auth/login");
		URI uri = builder.build().encode().toUri();
		HttpEntity<?> entity = getHttpEntity(credentials);
		ResponseEntity<MessageDto> authResponse = template.exchange(uri, HttpMethod.POST, entity, MessageDto.class);
		System.err.println(" invalid auth response: " + authResponse.toString());
		return authResponse;
	}

	HttpEntity<?> getHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		return entity;
	}

	HttpEntity<?> getHttpEntity(Object object) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(object, headers);
		return entity;
	}

	HttpEntity<?> getHttpEntityWithSessionCookie(String sessionCookie) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Cookie", "SESSION" + "=" + sessionCookie + ";");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		return entity;
	}

	HttpEntity<?> getHttpEntityWithSessionCookieForExport(String sessionCookie) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "text/csv");
		headers.set("Cookie", "SESSION" + "=" + sessionCookie + ";");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		return entity;
	}

	HttpEntity<?> getHttpEntityWithSessionCookie(Object object, String sessionCookie) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Cookie", "SESSION" + "=" + sessionCookie + ";");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> entity = new HttpEntity<>(object, headers);
		return entity;
	}

	HttpEntity<?> getHttpEntityWithSessionCookie(Object object, String sessionCookie, MediaType contentType) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Cookie", "SESSION" + "=" + sessionCookie + ";");
		headers.setContentType(contentType);
		HttpEntity<?> entity = new HttpEntity<>(object, headers);
		return entity;
	}

}
