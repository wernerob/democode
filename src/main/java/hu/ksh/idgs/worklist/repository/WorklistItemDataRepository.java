package hu.ksh.idgs.worklist.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;

public interface WorklistItemDataRepository extends PagingAndSortingRepository<WorklistItemEntity, IdgsEntityId> {

	@Modifying
	@Query("UPDATE WorklistItemEntity SET sendStatus = hu.ksh.idgs.worklist.model.value.SendStatusType.NOT_SENT WHERE formTemplateId = :formTemplateId AND sendStatus = hu.ksh.idgs.worklist.model.value.SendStatusType.SENT")
	int resetFormTemplateSendStatus(@Param("formTemplateId") final String formTemplateId);

}
