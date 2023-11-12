package com.oselan.jpalookups;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/***
 * A JPA application to demonstrate lookup management and localization of lookups
 * @author Ahmad Hamid
 *
 */
@SpringBootApplication (scanBasePackages ={"com.oselan"} )
@EntityScan(basePackages = {"com.oselan"})
@EnableJpaRepositories(basePackages = {"com.oselan"} ,considerNestedRepositories = true) 
public class JpaLookupsApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpaLookupsApplication.class, args);
	}

}
