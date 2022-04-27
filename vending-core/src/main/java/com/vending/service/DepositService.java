package com.vending.service;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vending.dto.CentSlotsDto;
import com.vending.dto.DepositDto;
import com.vending.dto.UserDto;
import com.vending.entity.User;

@Service
@Transactional
public class DepositService {

	@Resource
	private UserService userService;

	public UserDto deposit(DepositDto deposit) {
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User current = userService.get(email); // logged user making the request
		int amount = current.getDeposit() + deposit.getAmount();
		current.setDeposit(amount);
		current = userService.save(current);
		return UserDto.ofEntity(current, true);

	}

	public CentSlotsDto reset() {
		String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User current = userService.get(email);
		CentSlotsDto centSlots = CentSlotsDto.getChange(current.getDeposit());
		current.setDeposit(0);
		userService.save(current);
		return centSlots;
	}

}
