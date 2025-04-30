package hu.ksh.idgs.worklist.configuration;

import java.util.Collections;
import java.util.List;

import hu.ksh.idgs.core.configuration.ObservationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import hu.ksh.maja.core.security.AbstractSecurityConfig;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("!build")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@EnableFeignClients("hu.ksh.idgs.worklist.service.proxy")
public class SecurityConfig extends AbstractSecurityConfig {

//    @Autowired
//    ObservationConfig observationConfig;

    @Override
    protected String[] getUnauthenticatedEndpoints() {

        return  List.of(//
            "/actuator/**"
        )//
        .toArray(String[]::new);
    }

}
