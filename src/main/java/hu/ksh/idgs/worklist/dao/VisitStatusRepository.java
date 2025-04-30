package hu.ksh.idgs.worklist.dao;

import java.util.List;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitStatusSendDto;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.model.VisitStatusEntity;

public interface VisitStatusRepository extends IdgsRepository<VisitStatusEntity> {

	VisitStatusSendDto getNextSendableVisitStatus(final List<String> formTemplateIds);

	List<VisitStatusSendItemDto> getSendableWorklistItems(final String formTemplateId, final String userId);

	List<VisitStatusEntity> getVisitStatuses(List<IdgsEntityId> visitStatusIds);

}
