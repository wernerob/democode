package hu.ksh.idgs.worklist.model.value;

import java.util.Locale;

import hu.ksh.idgs.core.message.AbstractMessageProvider;
import hu.ksh.idgs.core.message.MajaMessage;
import hu.ksh.idgs.core.message.MajaServerMessageProvider;

public enum TemplateMessage implements MajaMessage {

	ADD_NEW_TOKEN_EMAIL_SUBJECT_1755,//

	;

	private static final AbstractMessageProvider MESSAGE_PROVIDER = MajaServerMessageProvider.getInstance();

	@Override
	public String getMessage() {
		return TemplateMessage.MESSAGE_PROVIDER.getMessage(this.getKey());
	}

	@Override
	public String getMessage(final Locale locale) {
		return TemplateMessage.MESSAGE_PROVIDER.getMessage(this.getKey(), locale);
	}

	@Override
	public String getMessage(final Object... arguments) {
		return TemplateMessage.MESSAGE_PROVIDER.getMessage(this.getKey(), arguments);
	}

	@Override
	public String getMessage(final Locale locale, final Object... arguments) {
		return TemplateMessage.MESSAGE_PROVIDER.getMessage(this.getKey(), locale, arguments);
	}

	@Override
	public String getKey() {
		return AbstractMessageProvider.getEnumLabelKey(this);
	}

}
