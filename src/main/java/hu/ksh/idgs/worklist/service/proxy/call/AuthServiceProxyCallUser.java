package hu.ksh.idgs.worklist.service.proxy.call;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;
import hu.ksh.idgs.auth.api.model.GetUserIdByNameResponseApiModel;
import hu.ksh.idgs.auth.api.model.ResultStatusApiModel;
import hu.ksh.idgs.auth.api.model.UserAddRequestApiModel;
import hu.ksh.idgs.auth.api.model.UserAddResponseApiModel;
import hu.ksh.idgs.auth.api.model.UserModifyRequestApiModel;
import hu.ksh.idgs.auth.api.model.UserModifyResponseApiModel;
import hu.ksh.idgs.worklist.configuration.WorklistProperties;
import hu.ksh.idgs.worklist.dto.WorklistUserDto;
import hu.ksh.idgs.worklist.service.proxy.AuthServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class AuthServiceProxyCallUser {

	@Lazy
	@Autowired
	private AuthServiceProxy authServiceProxy;

	@Lazy
	@Autowired
	private WorklistProperties worklistProperties;

	public String getUserId(final String userName, final String authorization) throws ServiceException {
		try {
			final ResponseEntity<GetUserIdByNameResponseApiModel> response = this.authServiceProxy
					.getUserIdByName(authorization, userName);
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Authentication service was unavailable while getting userId");
			}

			final GetUserIdByNameResponseApiModel responseBody = response.getBody();
			if (responseBody == null) {
				return null;
			}

			return responseBody.getUserId();
		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	public String registerUser(final String authorization, final WorklistUserDto worklistUserData, final String roleId)
			throws ServiceException {

		final UserAddRequestApiModel userAddRequest = getRegisterUserRequest(worklistUserData, roleId);

		try {
			final ResponseEntity<UserAddResponseApiModel> resposne = this.authServiceProxy.addUser(authorization,
					userAddRequest);

			if (resposne.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Authentication service was unavailable while registering a new user");
			}

			final UserAddResponseApiModel responseBody = resposne.getBody();
			if (responseBody.getStatus() != ResultStatusApiModel.OK) {
				throw new ServiceException("Authentication service got an error while registering a new user");
			}

			return responseBody.getUserId();
		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	public void modifyUser(final String authorization, final WorklistUserDto userData, final String userId)
			throws ServiceException {

		final UserModifyRequestApiModel userModifyRequest = getModifyUserRequest(userData, userId);

		try {
			final ResponseEntity<UserModifyResponseApiModel> response = this.authServiceProxy.modifyUser(authorization,
					userModifyRequest);

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Authentication service was unavailable while modifing user");
			}

			final UserModifyResponseApiModel responseBody = response.getBody();
			if (responseBody == null) {
				throw new ServiceException("Service returns empty response");
			}
			if (responseBody.getStatus() != ResultStatusApiModel.OK) {
				throw new ServiceException("Authentication service got an error while modifying user");
			}

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private UserAddRequestApiModel getRegisterUserRequest(final WorklistUserDto worklistUserData, final String roleId)
			throws ServiceException {
		final UserAddRequestApiModel userAddRequest = new UserAddRequestApiModel();
		userAddRequest.setUserName(worklistUserData.getUserName());
		userAddRequest.setLastName(worklistUserData.getLastName());
		userAddRequest.setFirstName(worklistUserData.getFirstName());
		userAddRequest.setRoleId(roleId);

		final String passwordHash = worklistUserData.getPasswordHash();
		if (this.worklistProperties.isPasswordPolicyEnabled()) {
			userAddRequest.setPassword(
					getDefaultInterviewerPassword(worklistUserData.getLastName(), worklistUserData.getUserName()));
		} else if (passwordHash != null && !passwordHash.isBlank()) {
			userAddRequest.setPasswordHash("{MD5}" + passwordHash);
		}

		return userAddRequest;
	}

	private String getDefaultInterviewerPassword(final String lastName, final String userName) throws ServiceException {

		if (lastName == null || lastName.length() < 3 || userName == null) {
			throw new ServiceException(
					String.format("Incomplete user data (lastName: %s, userName: %s)", lastName, userName));
		}

		final String passwordPreTag = StringUtils.stripAccents(lastName.substring(0, 3));

		return StringUtils.capitalize(passwordPreTag) + userName.substring(0, 5);

	}

	private UserModifyRequestApiModel getModifyUserRequest(final WorklistUserDto userData, final String userId) {

		final UserModifyRequestApiModel userModifyRequest = new UserModifyRequestApiModel();
		userModifyRequest.setUserId(userId);
		userModifyRequest.setUserName(userData.getUserName());
		userModifyRequest.setFirstName(userData.getFirstName());
		userModifyRequest.setLastName(userData.getLastName());

		final String passwordHash = userData.getPasswordHash();
		if (this.worklistProperties.isPasswordPolicyEnabled()) {
			return userModifyRequest;
		} else if (passwordHash != null && !passwordHash.isBlank()) {
			userModifyRequest.setPasswordHash("{MD5}" + passwordHash);
		}

		return userModifyRequest;
	}

}
