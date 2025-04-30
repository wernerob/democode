package hu.ksh.idgs.worklist.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dto.stat.WorklistStatDto;
import hu.ksh.idgs.worklist.dto.stat.WorklistStatFillRateItemDto;
import hu.ksh.idgs.worklist.service.WorklistStatService;

@Service("worklistStatService")
public class WorklistStatServiceImpl implements WorklistStatService {

	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Override
	public WorklistStatDto getStat(final String formTemplateId) {

		final WorklistStatDto worklistStat = new WorklistStatDto();
		worklistStat.setFillRate(getFillRate(formTemplateId));
		return worklistStat;
	}

	private List<WorklistStatFillRateItemDto> getFillRate(final String formTemplateId) {
		return this.worklistItemRepository.getFillRateStat(formTemplateId);
	}

}
