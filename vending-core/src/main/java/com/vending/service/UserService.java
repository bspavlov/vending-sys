package com.vending.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.vending.dto.UserDto;
import com.vending.entity.Role;
import com.vending.entity.User;
import com.vending.repo.UserRepository;

@Service
@Transactional
public class UserService {

	@Resource
	private PasswordEncoder passwordEncoder;
	@Resource
	private UserRepository userRepository;
	@Resource
	private RoleService roleService;

	public UserDto create(UserDto user) {
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()), "Provided password must be not blank");
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
		}
		User res = UserDto.of(user);
		Set<String> roleNames = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
		List<Role> roles = roleService.getAllByNames(roleNames);
		res.setRoles(Sets.newHashSet(roles));
		String hash = passwordEncoder.encode(user.getPassword());
		res.setPassword(hash);
		res = userRepository.save(res);
		return UserDto.ofEntity(res, true);
	}

	public UserDto edit(UserDto dto) {
		User res = get(dto.getId());
		BeanUtils.copyProperties(dto, res, "password", "roles", "createdDate", "lastModifiedDate", "createdBy",
				"lastModifiedBy");

		res.getRoles().clear();
		Set<Role> roles = dto.getRoles().stream().map(r -> roleService.get(r.getName())).collect(Collectors.toSet());
		res.getRoles().addAll(roles);

		res = userRepository.save(res);
		return UserDto.ofEntity(res, true);
	}

	public User get(Long id) {
		User res = userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Can't find user with id " + id));
		return res;
	}

	public User get(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException("Can't find user with email " + email));
	}

	public UserDto getByEmail(String email) {
		User res = get(email);
		return UserDto.ofEntity(res, true);
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}

	public User save(User user) {
		return userRepository.save(user);
	}

	public String getCurrentPrincipal() {
		return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
