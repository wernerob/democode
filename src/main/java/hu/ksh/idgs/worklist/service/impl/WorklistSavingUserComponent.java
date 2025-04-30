package hu.ksh.idgs.worklist.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.worklist.configuration.MajaProperties;
import hu.ksh.idgs.worklist.dto.AuthModulParameterValue;
import hu.ksh.idgs.worklist.dto.WorklistUserDto;
import hu.ksh.idgs.worklist.service.proxy.call.AuthServiceProxyCallUser;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class WorklistSavingUserComponent {

	@Autowired
	private AuthServiceProxyCallUser authServiceProxyCallUser;

	@Autowired
	private MajaProperties majaProperties;

	public String getUserIdWithRegistering(final WorklistUserDto userData, final String authorization,
			final String roleId) throws ServiceException {

		final String authModulStr = this.majaProperties.getAuthModul();

		final AuthModulParameterValue parameter = AuthModulParameterValue.findByValue(authModulStr);
		if (parameter == null) {
			throw new ServiceException(String.format("Unsupported auth modul: %s", authModulStr));
		}

		return switch (parameter) {
		case AUTH -> getUserIdWithRegisteringAuth(userData, authorization, roleId);
		default -> throw new ServiceException(String.format("Unhandled auth modul: %s", authModulStr));
		};

	}

	private String getUserIdWithRegisteringAuth(final WorklistUserDto userData, final String authorization,
			final String roleId) throws ServiceException {

		if (userData == null) {
			return null;
		}

		final String userName = userData.getUserName();
		String userId = this.authServiceProxyCallUser.getUserId(userName, authorization);
		if (userId == null) {
			userId = this.authServiceProxyCallUser.registerUser(authorization, userData, roleId);
		} else {
			this.authServiceProxyCallUser.modifyUser(authorization, userData, userId);
		}

		return userId;
	}

}
