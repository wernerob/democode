package hu.ksh.idgs.worklist.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
public class WorklistProperties {

	@Value("${maja.passwordPolicyEnabled:false}")
	private boolean passwordPolicyEnabled;

	@Value("${maja.perItemValidityOsaps}")
	private String[] perItemValidityOsaps;

	public boolean isPasswordPolicyEnabled() {
		return this.passwordPolicyEnabled;
	}

	public void setPasswordPolicyEnabled(final boolean passwordPolicyEnabled) {
		this.passwordPolicyEnabled = passwordPolicyEnabled;
	}

	public String[] getPerItemValidityOsaps() {
		return this.perItemValidityOsaps;
	}

	public void setPerItemValidityOsaps(final String[] perItemValidityOsaps) {
		this.perItemValidityOsaps = perItemValidityOsaps;
	}

}
