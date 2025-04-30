package hu.ksh.idgs.worklist.dao;

import java.util.List;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.model.WorklistUserEntity;

public interface WorklistUserRepository extends IdgsRepository<WorklistUserEntity> {

	WorklistUserEntity getWorklistUserForHouseHold(final String formTemplateId, IdgsEntityId houseHoldId,
			final String roleName);

	String getWorklistUserId(IdgsEntityId worklistId, String roleName);

	WorklistUserEntity getWorklistUser(IdgsEntityId worklistId, String roleName);

	boolean existsWorklistUser(IdgsEntityId worklistId, String userId);

	List<WorklistUserEntity> getWorklistUsersForFormTemplateRoleName(final String formTemplateId,
			final String userRoleName);

	boolean existsFormTemplateIdsForUser(final String userId, String formTemplateId);
}
