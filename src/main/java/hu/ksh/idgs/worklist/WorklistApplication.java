package hu.ksh.idgs.worklist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@ComponentScan(basePackages = "hu.ksh")
@SpringBootApplication
//@Profile("!build")
//@EnableFeignClients("hu.ksh.idgs.worklist.service.proxy")
@EnableScheduling
public class WorklistApplication {

	public static void main(final String[] args) {
		SpringApplication.run(WorklistApplication.class, args);
	}

}
