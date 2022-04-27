package com.vending.dto;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CentSlotsDto {

	private Map<Integer, Integer> centSlots = new LinkedHashMap<>();

	public CentSlotsDto() {
		centSlots.put(100, 0);
		centSlots.put(50, 0);
		centSlots.put(20, 0);
		centSlots.put(10, 0);
		centSlots.put(5, 0);
	}

	public static CentSlotsDto getChange(int cents) {
		CentSlotsDto res = new CentSlotsDto();
		int remainder = cents;
		int value = cents;
		Iterator<Integer> it = res.centSlots.keySet().iterator();
		while (it.hasNext() && remainder != 0) {
			Integer coin = it.next();
			value = remainder / coin;
			remainder = remainder % coin;
			res.centSlots.put(coin, value);
		}
		for (Integer coin : res.centSlots.keySet()) {
			System.err.println("coin " + coin + " with amount of " + res.centSlots.get(coin));
		}
		return res;
	}

}
