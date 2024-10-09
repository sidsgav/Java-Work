package com.nea.patient.access.portal.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class PatientPortalBackendApplication {

  public static void main(final String[] args) {
    SpringApplication.run(PatientPortalBackendApplication.class, args);
  }
}
