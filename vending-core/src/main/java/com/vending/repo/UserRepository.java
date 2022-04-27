package com.vending.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.vending.entity.User;

@Repository
public interface UserRepository
		extends JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

	@EntityGraph(attributePaths = { "roles" })
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);
}
