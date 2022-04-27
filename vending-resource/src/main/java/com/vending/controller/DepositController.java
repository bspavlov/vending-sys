package com.vending.controller;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vending.dto.CentSlotsDto;
import com.vending.dto.DepositDto;
import com.vending.dto.MessageDto;
import com.vending.dto.UserDto;
import com.vending.service.DepositService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/deposit")
public class DepositController {

	@Resource
	private DepositService depositService;

	@ApiOperation(value = "Deposit money into own account")
	@ApiResponses(value = {
			@ApiResponse(response = UserDto.class, code = 200, message = "User dto with updated deposit"),
			@ApiResponse(response = MessageDto.class, code = 400, message = "Server error, see MessageDto properties for details") })
	@PostMapping
	@ResponseStatus(code = HttpStatus.OK)
	public UserDto create(@RequestBody @Valid DepositDto deposit) {
		return depositService.deposit(deposit);
	}

	@PostMapping("/reset")
	@ResponseStatus(code = HttpStatus.OK)
	public CentSlotsDto reset() {
		return depositService.reset();
	}

}
