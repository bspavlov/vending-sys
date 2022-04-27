package com.vending.controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vending.dto.MessageDto;
import com.vending.dto.UserDto;
import com.vending.service.UserService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/users")
public class UserController {

	@Resource
	private UserService userService;

	@ApiOperation(value = "Create new user")
	@ApiResponses(value = {
			@ApiResponse(response = UserDto.class, code = 200, message = "User created"),
			@ApiResponse(response = MessageDto.class, code = 400, message = "Server error, see MessageDto properties for details") })
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public UserDto create(@RequestBody @Valid UserDto user) {
		return userService.create(user);
	}
	
	@PutMapping
	public UserDto edit(@RequestBody @Valid UserDto user) {
		return userService.edit(user);
	}

	@GetMapping("/by-email")
	public UserDto get(@RequestParam String email) {
		return userService.getByEmail(email);
	}

	@GetMapping("/current")
	public UserDto get() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String email = (String) auth.getPrincipal();
		return userService.getByEmail(email);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
		userService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
