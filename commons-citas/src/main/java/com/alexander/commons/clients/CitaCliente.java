package com.alexander.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas-msv")
public interface CitaCliente {

	@GetMapping("/comprobar-cita/{id}")
	boolean obtenerCitaConfirmadaOEnCurso(@PathVariable Long id);
	
	@GetMapping("/comprobar-cita-medico/{id}")
	boolean obtenerCitaConfirmadaOEnCursoMedico(@PathVariable Long id);
	
}
