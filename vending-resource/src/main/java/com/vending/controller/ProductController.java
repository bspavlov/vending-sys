package com.vending.controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vending.dto.MessageDto;
import com.vending.dto.ProductDto;
import com.vending.service.ProductService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Resource
	private ProductService productService;

	@ApiOperation(value = "Create new product")
	@ApiResponses(value = {
			@ApiResponse(response = ProductDto.class, code = 200, message = "Product created"),
			@ApiResponse(response = MessageDto.class, code = 400, message = "Server error, see MessageDto properties for details") })
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public ProductDto create(@RequestBody @Valid ProductDto product) {
		return productService.create(product);
	}
	
	@PutMapping
	public ProductDto edit(@RequestBody @Valid ProductDto product) {
		return productService.edit(product);
	}

	@GetMapping("/{id}")
	public ProductDto get(@PathVariable Long id) {
		return productService.getById(id);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
		productService.delete(id);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
