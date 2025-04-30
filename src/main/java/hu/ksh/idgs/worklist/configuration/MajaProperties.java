package hu.ksh.idgs.worklist.configuration;

import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties("maja")
@Validated
public class MajaProperties {

	@NotBlank
	private String selfLoaderDomain;

	@NotBlank
	private String authModul;

	public String getSelfLoaderDomain() {
		return this.selfLoaderDomain;
	}

	public void setSelfLoaderDomain(final String selfLoaderDomain) {
		this.selfLoaderDomain = selfLoaderDomain;
	}

	public String getAuthModul() {
		return this.authModul;
	}

	public void setAuthModul(final String authModul) {
		this.authModul = authModul;
	}

}
