package com.vending.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.vending.dto.CentSlotsDto;
import com.vending.dto.PurchaseDto;
import com.vending.entity.Product;
import com.vending.entity.User;

public class PurchaseServiceTest {

	@InjectMocks
	PurchaseService purchaseService;
	@Mock
	ProductService productService;
	@Mock
	UserService userService;
	@Mock
	User user;
	@Mock
	Product product;

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		Mockito.when(userService.getCurrentPrincipal()).thenReturn("test.example.com");
		Mockito.when(userService.get(Mockito.anyString())).thenReturn(user);
		Mockito.when(user.getDeposit()).thenReturn(65);
		Mockito.when(product.getAmountAvailable()).thenReturn(10);
		Mockito.when(product.getCost()).thenReturn(5);
	}

	@Test
	public void testBuy() {
		Mockito.when(productService.get(1L)).thenReturn(product);
		PurchaseDto purchase = PurchaseDto.builder().productId(1L).amount(4).build();
		CentSlotsDto change = purchaseService.buy(purchase);
		Map<Integer, Integer> changeSlots = change.getCentSlots();
		assertEquals(0, changeSlots.get(100));
		assertEquals(0, changeSlots.get(50));
		assertEquals(2, changeSlots.get(20));
		assertEquals(0, changeSlots.get(10));
		assertEquals(1, changeSlots.get(5));
		Mockito.verify(product).getCost();
		Mockito.verify(product, Mockito.times(2)).getAmountAvailable();
		Mockito.verify(product).setAmountAvailable(6);
		Mockito.verifyNoMoreInteractions(product);
		Mockito.verify(user, Mockito.times(2)).getDeposit();
		Mockito.verify(user).setDeposit(0);
		Mockito.verifyNoMoreInteractions(user);
		Mockito.verify(userService).save(user);
		Mockito.verifyNoMoreInteractions(user);
		Mockito.verify(productService).save(product);
		Mockito.verifyNoMoreInteractions(product);
	}

	@Test
	public void testBuyInvalidProcuctIdArg() {
		Mockito.when(productService.get(1L)).thenReturn(product);
		assertThrows(IllegalArgumentException.class, () -> {
			PurchaseDto purchase = PurchaseDto.builder().productId(null).amount(4).build();
			purchaseService.buy(purchase);
		});
	}

	@Test
	public void testBuyInvalidAmountdArg() {
		Mockito.when(productService.get(1L)).thenReturn(product);
		assertThrows(IllegalArgumentException.class, () -> {
			PurchaseDto purchase = PurchaseDto.builder().productId(1L).amount(-4).build();
			purchaseService.buy(purchase);
		});
	}

	@Test
	public void testInsufficientDeposit() {
		Mockito.when(productService.get(1L)).thenReturn(product);
		Exception ex = assertThrows(IllegalArgumentException.class, () -> {
			PurchaseDto purchase = PurchaseDto.builder().productId(1L).amount(100).build();
			purchaseService.buy(purchase);
		});
		assertTrue(ex.getMessage().contains("Insufficient deposit"));
	}

	@Test
	public void testInsufficientAmount() {
		Mockito.when(productService.get(1L)).thenReturn(product);
		Exception ex = assertThrows(IllegalArgumentException.class, () -> {
			PurchaseDto purchase = PurchaseDto.builder().productId(1L).amount(11).build();
			purchaseService.buy(purchase);
		});
		assertTrue(ex.getMessage().contains("Insufficient amount"));
	}

}
