package hu.ksh.idgs.worklist.service.impl;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.service.CacheService;

@Component
public class ScheduleComponent {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleComponent.class);

	@Autowired
	private CacheService cacheService;

	@Value("${git.commit.id.abbrev}")
	private String commitId;

	@Value("${git.build.time}")
	private String buildTime;

	@Scheduled(cron = "${maja.clearCache.cron}")
	public void evictAllCaches() {
		this.cacheService.deleteAllCache();
		ScheduleComponent.LOGGER.info("All cache cleaned");
	}

	@Scheduled(fixedDelay = 12, timeUnit = TimeUnit.HOURS)
	public void versionInfo() {
		ScheduleComponent.LOGGER.info("Revision info (commit: {}, buildtime: {})", this.commitId, this.buildTime);
	}

}
