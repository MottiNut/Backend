package com.mottinut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
/*@EntityScan(basePackages = {
		"com.mottinut.auth.domain.entities",
		"com.mottinut.auth.domain.emalServices.entity",
		"com.mottinut.auth.infrastructure.persistence.entities"
})
@EnableJpaRepositories(basePackages = {
		"com.mottinut.auth.domain.repositories",
		"com.mottinut.auth.domain.emalServices.repositories",
		"com.mottinut.auth.infrastructure.persistence.repositories"
})*/
public class MottinutApplication {
	public static void main(String[] args) {
		SpringApplication.run(MottinutApplication.class, args);
	}

}
