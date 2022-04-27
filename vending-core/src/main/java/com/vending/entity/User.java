package com.vending.entity;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(nullable = false, unique = true)
	@Email(message = "Invalid email address")
	@NotBlank
	private String email;
	@Column(nullable = false)
	@NotBlank
	@Size(min = 8, message = "The password must contain at least 8 characters")
	@Pattern(regexp = "^.*(?=.{8,})(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*_0-9]).*", message = "Password should include an uppercase letter and either a symbol or a number.")
	private String password;
	@Column(nullable = false)
	@NotBlank
	private String firstName;
	@Column(nullable = false)
	@NotBlank
	private String lastName;
	@PositiveOrZero
	private int deposit;
	@CreatedDate
	private Instant createdDate;
	@LastModifiedDate
	private Instant lastModifiedDate;
	@CreatedBy
	private String createdBy;
	@LastModifiedBy
	private String lastModifiedBy;
	@Builder.Default
	@ManyToMany
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<Role>();
	@Builder.Default
	@OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Product> products = new HashSet<>();
//	@Version
//	private Integer version;

}
