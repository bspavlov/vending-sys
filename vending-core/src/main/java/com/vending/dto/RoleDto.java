package com.vending.dto;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

import com.vending.entity.Role;

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
public class RoleDto {

	private String name;
	private String roleGroup;

	public static Role of(RoleDto role) {
		Role res = new Role();
		BeanUtils.copyProperties(role, res, "users");
		return res;
	}

	public static Set<Role> of(Set<RoleDto> roles) {
		return roles.stream().map(r -> of(r)).collect(Collectors.toSet());
	}

	public static RoleDto of(Role role) {
		RoleDto res = new RoleDto();
		BeanUtils.copyProperties(role, res, "users");
		return res;
	}

	public static Set<RoleDto> ofEntities(Set<Role> roles) {
		return roles.stream().map(r -> of(r)).collect(Collectors.toSet());
	}

}
