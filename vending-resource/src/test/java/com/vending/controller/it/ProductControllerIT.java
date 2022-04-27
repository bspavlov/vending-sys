package com.vending.controller.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.vending.dto.Credentials;
import com.vending.dto.MessageDto;
import com.vending.dto.ProductDto;
import com.vending.dto.UserDto;

@ActiveProfiles({ "development" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ProductControllerIT extends BaseControllerIT {

	@Test
	public void testEndpoints() {

		Credentials credentials = Credentials.builder().username("admin@vending.com").password("vendiNG2021").build();
		String sessionId = login(credentials);

		// get seller user
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/users/by-email")
				.queryParam("email", "admin@vending.com");
		URI uri = builder.build().encode().toUri();
		HttpEntity<?> entity = getHttpEntityWithSessionCookie(sessionId);
		ResponseEntity<UserDto> response = template.exchange(builder.build().encode().toUri(), HttpMethod.GET, entity,
				UserDto.class);
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

		// get created product
		builder = UriComponentsBuilder
				.fromHttpUrl("http://localhost:" + port + "/products/" + productResponse.getBody().getId());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		productResponse = template.exchange(uri, HttpMethod.GET, entity, ProductDto.class);
		assertEquals(5, productResponse.getBody().getCost());

		// edit product
		product = productResponse.getBody();
		product.setCost(10);
		builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + port + "/products");
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(product, sessionId);
		productResponse = template.exchange(uri, HttpMethod.PUT, entity, ProductDto.class);
		assertEquals(HttpStatus.OK, productResponse.getStatusCode());
		assertEquals(10, productResponse.getBody().getCost());

		// get edited product
		builder = UriComponentsBuilder
				.fromHttpUrl("http://localhost:" + port + "/products/" + productResponse.getBody().getId());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		productResponse = template.exchange(uri, HttpMethod.GET, entity, ProductDto.class);
		assertEquals(10, productResponse.getBody().getCost());

		// delete product
		builder = UriComponentsBuilder
				.fromHttpUrl("http://localhost:" + port + "/products/" + productResponse.getBody().getId());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		ResponseEntity<Void> deleteResponse = template.exchange(uri, HttpMethod.DELETE, entity, Void.class);
		assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

		// try to get deleted product
		builder = UriComponentsBuilder
				.fromHttpUrl("http://localhost:" + port + "/products/" + productResponse.getBody().getId());
		uri = builder.build().encode().toUri();
		entity = getHttpEntityWithSessionCookie(sessionId);
		ResponseEntity<MessageDto> errorResponse = template.exchange(uri, HttpMethod.GET, entity, MessageDto.class);
		assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatusCode());
	}

}
