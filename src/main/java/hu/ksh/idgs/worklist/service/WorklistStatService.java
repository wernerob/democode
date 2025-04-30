package hu.ksh.idgs.worklist.service;

import hu.ksh.idgs.worklist.dto.stat.WorklistStatDto;

public interface WorklistStatService {

	WorklistStatDto getStat(String formTemplateId);

}
