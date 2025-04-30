package hu.ksh.idgs.worklist.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import hu.ksh.idgs.worklist.dao.HouseHoldRepository;
import hu.ksh.idgs.worklist.service.HouseHoldService;
import hu.ksh.maja.core.exception.ServiceException;
import jakarta.persistence.PersistenceException;

@Service("houseHoldService")
public class HouseHoldServiceImpl implements HouseHoldService {

	@Lazy
	@Autowired
	private HouseHoldRepository houseHoldRepository;

	@Override
	public boolean existsOnForm(final String formTemplateId, final String kshAddressId, final String houseHoldId)
			throws ServiceException {

		if (formTemplateId == null || kshAddressId == null || houseHoldId == null) {
			return false;
		}

		try {
			return this.houseHoldRepository.existsOnForm(formTemplateId, kshAddressId, houseHoldId);
		} catch (final PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

}
