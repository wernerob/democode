package hu.ksh.idgs.worklist.dto;

public enum WorklistTemplates {

	NEW_TOKEN("new-token-email.ftl");

	private String templateName;

	WorklistTemplates(final String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateName() {
		return this.templateName;
	}

}
