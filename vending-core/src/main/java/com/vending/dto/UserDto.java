package com.vending.dto;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vending.entity.User;

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
public class UserDto {
	
	private Long id;
	@Email(message = "Invalid email address")
	@NotBlank
	private String email;
	@NotBlank
	private String firstName;
	@NotBlank
	private String lastName;
	private String password;
	@PositiveOrZero
	private int deposit;
	private Instant createdDate;
	private Instant lastModifiedDate;
	private String createdBy;
	private String lastModifiedBy;
	@Builder.Default
	private Set<RoleDto> roles = new HashSet<RoleDto>();
	
	public static User of(UserDto userDto) {
		User res = new User();
		BeanUtils.copyProperties(userDto, res, "password", "roles", "createdDate", "lastModifiedDate", "createdBy",
				"lastModifiedBy");
		return res;
	}

	public static List<User> of(List<UserDto> users) {
		return users.stream().map(u -> of(u)).collect(Collectors.toList());
	}

	public static List<UserDto> ofEntities(Collection<User> users) {
		return users.stream().map(u -> ofEntity(u, false)).collect(Collectors.toList());
	}

	public static UserDto ofEntity(User user, boolean details) {
		UserDto res = new UserDto();
		BeanUtils.copyProperties(user, res, "roles", "password");
		if (details) {
			res.setRoles(RoleDto.ofEntities(user.getRoles()));
		}
		return res;
	}

}
