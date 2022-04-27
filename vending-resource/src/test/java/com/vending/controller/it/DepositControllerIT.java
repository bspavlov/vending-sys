package com.vending.controller.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Map;

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
import com.vending.dto.CentSlotsDto;
import com.vending.dto.Credentials;
import com.vending.dto.DepositDto;
import com.vending.dto.RoleDto;
import com.vending.dto.UserDto;
import com.vending.entity.VendingRole;

@ActiveProfiles({ "development" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class DepositControllerIT extends BaseControllerIT {
	
	@Test
	public void testEndpoints() {

		Credentials credentials = Credentials.builder().username("admin@vending.com").password("vendiNG2021").build();
		String sessionId = login(credentials);

		// create buyer user
		UserDto user = UserDto.builder().email("abv@test.com").firstName("Client").lastName("Buyer")
				.password("buyER2022")
				.roles(Sets.newHashSet(RoleDto.builder().name(VendingRole.ROLE_BUYER.name()).build())).build();
		HttpEntity<?> entity = getHttpEntityWithSessionCookie(user, sessionId);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users");
		URI uri = builder.build().encode().toUri();
		ResponseEntity<UserDto> response = template.exchange(uri, HttpMethod.POST, entity, UserDto.class);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("abv@test.com", response.getBody().getEmail());
		assertEquals("Client", response.getBody().getFirstName());
		assertTrue(response.getBody().getRoles().stream()
				.anyMatch(r -> r.getName().equals(VendingRole.ROLE_BUYER.name())));

		// login as a buyer
		credentials = Credentials.builder().username("abv@test.com").password("buyER2022").build();
		sessionId = login(credentials);

		// top up deposit
		DepositDto deposit = DepositDto.builder().amount(10).build();
		entity = getHttpEntityWithSessionCookie(deposit, sessionId);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/deposit");
		uri = builder.build().encode().toUri();
		response = template.exchange(uri, HttpMethod.POST, entity, UserDto.class);
		assertEquals(10, response.getBody().getDeposit());

		// get user
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/current");
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		response = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, UserDto.class);
		assertEquals("abv@test.com", response.getBody().getEmail());
		assertEquals(10, response.getBody().getDeposit());

		// reset deposit
		entity = getHttpEntityWithSessionCookie(sessionId);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/deposit/reset");
		uri = builder.build().encode().toUri();
		ResponseEntity<CentSlotsDto> changeResponse = template.exchange(uri, HttpMethod.POST, entity,
				CentSlotsDto.class);
		Map<Integer, Integer> changeSlots = changeResponse.getBody().getCentSlots();
		assertEquals(0, changeSlots.get(100));
		assertEquals(0, changeSlots.get(50));
		assertEquals(0, changeSlots.get(20));
		assertEquals(1, changeSlots.get(10));
		assertEquals(0, changeSlots.get(5));

		// get user and verify deposit
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/current");
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		response = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, UserDto.class);
		assertEquals("abv@test.com", response.getBody().getEmail());
		assertEquals(0, response.getBody().getDeposit());
	}

}
