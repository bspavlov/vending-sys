package com.vending.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class ProductCostValidator implements ConstraintValidator<ProductCostValid, Integer> {

	@Override
	public boolean isValid(Integer cost, ConstraintValidatorContext context) {
		return cost % 5 == 0;
	}

}
