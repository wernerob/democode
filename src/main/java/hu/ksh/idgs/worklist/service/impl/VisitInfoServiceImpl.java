package hu.ksh.idgs.worklist.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import hu.ksh.idgs.core.dto.FormTemplateVisitItemsDto;
import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.util.CodeTableUtil;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dao.WorklistUserRepository;
import hu.ksh.idgs.worklist.dto.VisitInfoDto;
import hu.ksh.idgs.worklist.dto.VisitItemDto;
import hu.ksh.idgs.worklist.service.VisitInfoService;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallVisitFormTemplate;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.helper.RoleHelper;
import hu.ksh.maja.core.service.MajaAuthenticationService;
import hu.ksh.maja.core.value.Language;
import hu.ksh.maja.core.value.SpecialVisitCode;

@Service("visitInfoService")
public class VisitInfoServiceImpl implements VisitInfoService {

	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Autowired
	private FormServiceProxyCallVisitFormTemplate formServiceProxyCallVisitFormTemplate;

	@Autowired
	private MajaAuthenticationService majaAuthenticationService;

	@Autowired
	private WorklistUserRepository worklistUserRepository;

	@Override
	public VisitInfoDto getVisitInfo(final String authorization, final IdgsEntityId worklistItemId,
			final Language language) throws ServiceException {

		try {

			checkAccess(worklistItemId);

			final VisitInfoDto visitInfo = new VisitInfoDto();

			final String formTemplateId = this.worklistItemRepository.getFormTemplateId(worklistItemId);
			Assert.notNull(formTemplateId, String.format("Unknown worklist item id: %s", worklistItemId.toString()));
			final FormTemplateVisitItemsDto visitCodeItemsForTemplate = this.formServiceProxyCallVisitFormTemplate
					.getVisitCodeItemsByFormTemlate(authorization, formTemplateId, language);

			final List<CodeTableItemDto> finalCodeTableItems = visitCodeItemsForTemplate.getFinalCodeTableItems();
			final List<CodeTableItemDto> tempCodeTableItems = visitCodeItemsForTemplate.getTempCodeTableItems();
			final List<CodeTableItemDto> otherFinalCodeTableItems = visitCodeItemsForTemplate
					.getOtherFinalCodeTableItems();
			final List<CodeTableItemDto> successCodeTableItems = visitCodeItemsForTemplate.getSuccessCodeTableItems();
			final List<CodeTableItemDto> adminFinalCodeTableItems = visitCodeItemsForTemplate
					.getAdminFinalCodeTableItems();
			@SuppressWarnings("serial")
			final List<CodeTableItemDto> allVisitCodeTableItems = new ArrayList<>() {

				{
					addAll(finalCodeTableItems);
					addAll(tempCodeTableItems);
					addAll(otherFinalCodeTableItems);
					addAll(successCodeTableItems);
					addAll(adminFinalCodeTableItems);
				}
			};

			visitInfo.getFinalVisitCodeTableItems().addAll(getValidCodeTableItems(finalCodeTableItems));
			visitInfo.getTempVisitCodeTableItems().addAll(getValidCodeTableItems(tempCodeTableItems));
			visitInfo.getOtherFinalCodeTableItems().addAll(getValidCodeTableItems(otherFinalCodeTableItems));
			visitInfo.getAdminFinalVisitCodeTableItems().addAll(getValidCodeTableItems(adminFinalCodeTableItems));

			final Map<IdgsEntityId, CodeTableItemDto> allVisitCodeItemMap = allVisitCodeTableItems.stream()
					.collect(Collectors.toMap(CodeTableItemDto::getId, Function.identity(), (visit1, visit2) -> {
						return visit1;
					}));
			final List<VisitItemDto> visitItems = getVisitItems(worklistItemId);

			for (final VisitItemDto visitItem : visitItems) {

				CodeTableItemDto visitCodeTableItem = getMockCodeTableForVisitCodes(visitItem.getVisitCode());
				if (visitCodeTableItem != null) {
					visitItem.setCodeTableItem(visitCodeTableItem);
					visitInfo.getSavedVisitItems().add(visitItem);
					continue;
				}

				final String visitCodeTableItemId = visitItem.getVisitCodeTableItemId();
				visitCodeTableItem = allVisitCodeItemMap.get(IdgsEntityId.fromString(visitCodeTableItemId));
				if (visitCodeTableItem == null) {
					continue;
				}
				visitItem.setCodeTableItem(visitCodeTableItem);
				visitInfo.getSavedVisitItems().add(visitItem);
			}

			return visitInfo;
		} catch (final IllegalArgumentException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private CodeTableItemDto getMockCodeTableForVisitCodes(final String visitCode) {

		final String description = SpecialVisitCode.getDesciption(visitCode);
		if (description == null) {
			return null;
		}

		final CodeTableItemDto item = new CodeTableItemDto();
		item.setCode(visitCode);
		item.setDesciption(description);

		return item;

	}

	private List<VisitItemDto> getVisitItems(final IdgsEntityId worklistItemId) {
		return this.worklistItemRepository.getVisitItemsByWorklist(worklistItemId);
	}

	private List<CodeTableItemDto> getValidCodeTableItems(final List<CodeTableItemDto> codeTableItems) {
		return CodeTableUtil.getValidCodeTableItems(codeTableItems);

	}

	private void checkAccess(final IdgsEntityId worklistItemId) throws ServiceException {

		final String roleName = this.majaAuthenticationService.getRoleName();

		if (RoleHelper.isAdmin(roleName) || RoleHelper.isOsapAdmin(roleName)) {
			return;
		}

		if (!this.worklistUserRepository.existsWorklistUser(worklistItemId,
				this.majaAuthenticationService.getUserId())) {
			throw new ServiceException(String.format("User (%s) does not have access to worklist (%s)",
					this.majaAuthenticationService.getUserName(), worklistItemId));
		}

	}

}
