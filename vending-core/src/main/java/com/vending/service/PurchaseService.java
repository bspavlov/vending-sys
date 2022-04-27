package com.vending.service;

import javax.annotation.Resource;
import javax.persistence.Version;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.vending.dto.CentSlotsDto;
import com.vending.dto.PurchaseDto;
import com.vending.entity.Product;
import com.vending.entity.User;

@Service
@Transactional
public class PurchaseService {

	@Resource
	private ProductService productService;
	@Resource
	private UserService userService;

	/**
	 * Race conditions that can occur in this method can be handled in multiple
	 * ways, in this case they are prevented on database transaction level. The
	 * READ_COMMITED transaction isolation level, which is the default one for most
	 * of the databases, keeps read and write locks (acquired on selected data)
	 * until the end of the transaction. Alternatively, another concepts such as
	 * optimistic locking can be used, see {@link Version} annotation.
	 * 
	 * @param purchase
	 * @return
	 */
	public CentSlotsDto buy(PurchaseDto purchase) {
		Preconditions.checkArgument(purchase != null);
		Preconditions.checkArgument(purchase.getProductId() != null);
		Preconditions.checkArgument(purchase.getAmount() > 0);
		String email = userService.getCurrentPrincipal();
		User current = userService.get(email);
		Product product = productService.get(purchase.getProductId());
		int totalCost = product.getCost() * purchase.getAmount();
		if (totalCost > current.getDeposit()) {
			throw new IllegalArgumentException("Insufficient deposit " + current.getDeposit()
					+ " for total purchase cost of " + totalCost + " cents");
		}
		if (purchase.getAmount() > product.getAmountAvailable()) {
			throw new IllegalArgumentException(
					"Insufficient amount " + product.getAmountAvailable() + " for requested amout "
							+ purchase.getAmount());
		}
		product.setAmountAvailable(product.getAmountAvailable() - purchase.getAmount());
		int change = current.getDeposit() - totalCost;
		CentSlotsDto centSlots = CentSlotsDto.getChange(change);
		current.setDeposit(0);
		productService.save(product);
		userService.save(current);
		return centSlots;
	}

}
