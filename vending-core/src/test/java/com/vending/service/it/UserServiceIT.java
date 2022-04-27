package com.vending.service.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.vending.dto.UserDto;
import com.vending.service.RoleService;
import com.vending.service.UserService;

/**
 * 
 * @author pavlovb Example of integration test in the service layer
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@ActiveProfiles({ "development" })
@Sql({ "classpath:roles.sql" })
public class UserServiceIT {
	
	@Resource
	UserService userService;
	@Resource
	RoleService roleService;

	@Test
	public void testGetByEmail() {
		UserDto seller = userService.getByEmail("admin@vending.com");
		assertEquals("Admin", seller.getFirstName());
	}

}
