package com.vending.service;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.vending.entity.Role;
import com.vending.repo.RoleRepository;

@Service
@Transactional
public class RoleService {

	@Resource
	private RoleRepository roleRepository;

	public Role get(String name) {
		return roleRepository.findByName(name)
				.orElseThrow(() -> new EntityNotFoundException("Can't find role with name " + name));
	}

	public List<Role> getAllByNames(Iterable<String> names) {
		return roleRepository.findAllByNameIn(names);
	}

	public List<Role> get(BooleanExpression expression) {
		if (expression != null) {
			return IterableUtils.toList(roleRepository.findAll(expression, Sort.by("name")));
		}
		return roleRepository.findAll();
	}

}
