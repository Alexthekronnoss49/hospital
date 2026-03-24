package com.alexander.pacientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.alexander.pacientes", "com.alexander.commons"})
public class PacientesMsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesMsvApplication.class, args);
	}

}
