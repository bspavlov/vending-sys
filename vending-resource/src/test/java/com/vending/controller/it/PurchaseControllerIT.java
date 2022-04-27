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
import com.vending.dto.ProductDto;
import com.vending.dto.PurchaseDto;
import com.vending.dto.RoleDto;
import com.vending.dto.UserDto;
import com.vending.entity.VendingRole;

@ActiveProfiles({ "development" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class PurchaseControllerIT extends BaseControllerIT {

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

		// get seller user
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/by-email")
				.queryParam("email", "admin@vending.com");
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		response = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, UserDto.class);
		assertEquals("Admin", response.getBody().getFirstName());

		// create product
		ProductDto product = ProductDto.builder().amountAvailable(20).cost(5).productName("Product A")
				.sellerId(response.getBody().getId()).build();
		entity = getHttpEntityWithSessionCookie(product, sessionId);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/products");
		uri = builder.build().encode().toUri();
		ResponseEntity<ProductDto> productResponse = template.exchange(uri, HttpMethod.POST, entity, ProductDto.class);
		assertEquals(HttpStatus.CREATED, productResponse.getStatusCode());
		assertEquals(response.getBody().getId(), productResponse.getBody().getSellerId());

		// login as buyer
		credentials = Credentials.builder().username("abv@test.com").password("buyER2022").build();
		sessionId = login(credentials);

		// top up deposit
		DepositDto deposit = DepositDto.builder().amount(50).build();
		entity = getHttpEntityWithSessionCookie(deposit, sessionId);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/deposit");
		uri = builder.build().encode().toUri();
		response = template.exchange(uri, HttpMethod.POST, entity, UserDto.class);
		assertEquals(50, response.getBody().getDeposit());

		// buy product
		PurchaseDto purchase = PurchaseDto.builder().productId(productResponse.getBody().getId()).amount(3).build();
		entity = getHttpEntityWithSessionCookie(purchase, sessionId);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/buy");
		uri = builder.build().encode().toUri();
		ResponseEntity<CentSlotsDto> buyResonse = template.exchange(uri, HttpMethod.POST, entity, CentSlotsDto.class);
		Map<Integer, Integer> changeSlots = buyResonse.getBody().getCentSlots();
		assertEquals(0, changeSlots.get(100));
		assertEquals(0, changeSlots.get(50));
		assertEquals(1, changeSlots.get(20));
		assertEquals(1, changeSlots.get(10));
		assertEquals(1, changeSlots.get(5));

		// get buyer user and verify deposit
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/current")
				.queryParam("email", "abv@test.com");
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		response = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity, UserDto.class);
		assertEquals("Client", response.getBody().getFirstName());
		assertEquals(0, response.getBody().getDeposit());

		// get product and verify amount
		builder = UriComponentsBuilder
				.fromHttpUrl("http://localhost:" + port + "/products/" + productResponse.getBody().getId());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		productResponse = template.exchange(uri, HttpMethod.GET, entity, ProductDto.class);
		assertEquals(17, productResponse.getBody().getAmountAvailable());
	}

}
