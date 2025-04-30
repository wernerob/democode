package hu.ksh.idgs.worklist.service.impl;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import hu.ksh.idgs.worklist.dto.WorklistTemplates;
import hu.ksh.maja.core.exception.ServiceException;

@Component("templateComponent")
public class TemplateComponent {

	@Autowired
	private FreeMarkerConfig freeMarkerConfig;

	public String getStringFromTemplate(final String formCode, final WorklistTemplates worklistTemplate,
			final Map<String, Object> model) throws ServiceException {

		try {

			final String templatePath = Paths.get("osap" + formCode, worklistTemplate.getTemplateName()).toString();

			final Template template = this.freeMarkerConfig.getConfiguration().getTemplate(templatePath);
			return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
		} catch (final IOException | TemplateException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

}
