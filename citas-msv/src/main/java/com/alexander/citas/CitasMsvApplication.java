package com.alexander.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.alexander.citas", "com.alexander.commons"})
public class CitasMsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(CitasMsvApplication.class, args);
	}

}
