package com.alexander.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.alexander.commons.configuration.FeignClientConfig;

@FeignClient(name = "citas-msv", configuration = FeignClientConfig.class)
public interface CitaCliente {

	@GetMapping("/comprobar-cita/{id}")
	boolean obtenerCitaConfirmadaOEnCurso(@PathVariable Long id);
	
	@GetMapping("/comprobar-cita-medico/{id}")
	boolean obtenerCitaConfirmadaOEnCursoMedico(@PathVariable Long id);

}
