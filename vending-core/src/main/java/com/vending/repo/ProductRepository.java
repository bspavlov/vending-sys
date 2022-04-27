package com.vending.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vending.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
