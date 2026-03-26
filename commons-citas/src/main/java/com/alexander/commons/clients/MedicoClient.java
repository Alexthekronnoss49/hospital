package com.alexander.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.alexander.commons.configuration.FeignClientConfig;
import com.alexander.commons.dto.MedicoResponse;

import jakarta.validation.constraints.Positive;

@FeignClient(name = "medicos-msv", configuration = FeignClientConfig.class)
public interface MedicoClient {
	
	@GetMapping("/{id}")
	MedicoResponse obtenerMedicoPorId(@PathVariable Long id);

	@GetMapping("/id-medico/{id}")
	MedicoResponse obtenerMedicoPorIdSinEstado(@PathVariable Long id);
	
	@GetMapping("/id-medico-disp/{id}")
	MedicoResponse obtenerMedicoConDisponibilidad(@PathVariable Long id);
	
	@PutMapping("/{idMedico}/disponibilidad/{idDisponibilidad}")
	MedicoResponse actualizarDisp(
			@PathVariable
			@Positive(message = "El id debe ser positivo") Long idMedico,
			@PathVariable
			@Positive(message = "El id estado debe ser positivo") Long idDisponibilidad);
}
