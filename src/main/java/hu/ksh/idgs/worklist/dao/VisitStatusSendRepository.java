package hu.ksh.idgs.worklist.dao;

import java.util.List;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.worklist.model.VisitStatusEntity;
import hu.ksh.idgs.worklist.model.VisitStatusSendEntity;

public interface VisitStatusSendRepository extends IdgsRepository<VisitStatusSendEntity> {

	List<VisitStatusEntity> getVisitItems(String packageId);

	boolean hasExistingPackageId(String packageId);

}
