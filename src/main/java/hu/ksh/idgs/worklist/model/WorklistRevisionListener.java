package hu.ksh.idgs.worklist.model;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import hu.ksh.maja.core.security.MajaWebTokenAuthentication;

public class WorklistRevisionListener implements RevisionListener {

	@Override
	public void newRevision(final Object revisionEntity) {

		if (revisionEntity instanceof WorklistRevisionEntity) {
			final WorklistRevisionEntity audit = (WorklistRevisionEntity) revisionEntity;

			final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication instanceof MajaWebTokenAuthentication) {
				final MajaWebTokenAuthentication webTokenAuthentication = (MajaWebTokenAuthentication) authentication;
				audit.setUserId(webTokenAuthentication.getName());
			}

		}

	}

}
