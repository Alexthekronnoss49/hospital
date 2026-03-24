package com.alexander.medicos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.alexander.medicos", "com.alexander.commons"})
public class MedicosMsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicosMsvApplication.class, args);
	}

}
