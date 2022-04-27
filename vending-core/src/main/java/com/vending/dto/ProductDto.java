package com.vending.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vending.entity.Product;
import com.vending.validator.ProductCostValid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ProductDto {

	private Long id;
	@PositiveOrZero
	private int amountAvailable;
	@Positive
	@ProductCostValid
	private int cost;
	@NotBlank
	private String productName;
	@NotNull
	private Long sellerId;

	public static Product of(ProductDto product) {
		Product res = new Product();
		BeanUtils.copyProperties(product, res, "seller");
		return res;
	}

	public static ProductDto ofEntity(Product product) {
		ProductDto res = new ProductDto();
		BeanUtils.copyProperties(product, res, "sellerId");
		res.setSellerId(product.getSeller().getId());
		return res;
	}

}
