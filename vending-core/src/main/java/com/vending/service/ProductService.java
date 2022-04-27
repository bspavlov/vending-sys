package com.vending.service;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vending.dto.ProductDto;
import com.vending.entity.Product;
import com.vending.entity.User;
import com.vending.repo.ProductRepository;

@Service
@Transactional
public class ProductService {

	@Resource
	private ProductRepository productRepository;
	@Resource
	private UserService userService;

	public ProductDto create(ProductDto product) {
		Product res = ProductDto.of(product);
		User user = userService.get(product.getSellerId());
		res.setSeller(user);
		res = productRepository.save(res);
		return ProductDto.ofEntity(res);
	}

	public ProductDto edit(ProductDto product) {
		Product res = get(product.getId());
		BeanUtils.copyProperties(product, res, "seller");
		User user = userService.get(product.getSellerId());
		res.setSeller(user);
		res = productRepository.save(res);
		return ProductDto.ofEntity(res);
	}

	public Product get(Long id) {
		Product res = productRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Can't find product with id " + id));
		return res;
	}

	public ProductDto getById(Long id) {
		return ProductDto.ofEntity(get(id));
	}

	public void delete(Long id) {
		productRepository.deleteById(id);
	}

	public Product save(Product product) {
		return productRepository.save(product);
	}

}
