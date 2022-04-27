package com.vending.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vending.dto.CentSlotsDto;
import com.vending.dto.PurchaseDto;
import com.vending.service.PurchaseService;

@RestController
@RequestMapping("/buy")
public class PurchaseController {

	@Resource
	private PurchaseService purchaseService;

	@PostMapping
	public CentSlotsDto buy(@RequestBody @Valid PurchaseDto purchase) {
		return purchaseService.buy(purchase);
	}

}
