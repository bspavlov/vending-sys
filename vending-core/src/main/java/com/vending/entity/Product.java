package com.vending.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import org.checkerframework.checker.index.qual.Positive;

import com.vending.validator.ProductCostValid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@PositiveOrZero
	private int amountAvailable;
	@Positive
	@ProductCostValid
	private int cost;
	@NotBlank
	private String productName;
	@ManyToOne(optional = false)
	private User seller;
//	@Version
//	private Integer version;
}
