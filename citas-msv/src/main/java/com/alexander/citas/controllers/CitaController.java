package com.alexander.citas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.citas.dto.CitaRequest;
import com.alexander.citas.dto.CitaResponse;
import com.alexander.citas.services.CitaService;
import com.alexander.commons.controllers.CommonController;
import jakarta.validation.constraints.Positive;

@RestController
@Validated
public class CitaController extends CommonController<CitaRequest, CitaResponse, CitaService>{

	public CitaController(CitaService service) {
		super(service);
	}
	
	@GetMapping("/id-cita/{id}")
	public ResponseEntity<CitaResponse> obtenerCitaPorIdSinEstado(
			@PathVariable
			@Positive(message = "El id debe ser positivo") Long id){
		
		return ResponseEntity.ok(service.obtenerCitaPorIdSinEstado(id));
		
	}
	
	@GetMapping("/comprobar-cita/{id}")
	public ResponseEntity<Boolean> obtenerCitaConfirmadaOEnCurso(
			@PathVariable
			@Positive(message = "El id debe ser positivo") Long id){
		
		return ResponseEntity.ok(service.obtenerCitasConfirmadasOEnCurso(id));
		
	}
	
	@GetMapping("/comprobar-cita-medico/{id}")
	public ResponseEntity<Boolean> obtenerCitaConfirmadaOEnCursoMedico(
			@PathVariable
			@Positive(message = "El id debe ser positivo") Long id){
		
		return ResponseEntity.ok(service.obtenerCitasConfirmadasOEnCursoMedico(id));
		
	}
	
	
	@PatchMapping("/{idCita}/estado/{idEstado}")
	public ResponseEntity<CitaResponse> actualizarEstado(
			@PathVariable
			@Positive(message = "El id debe ser positivo") Long idCita,
			@PathVariable
			@Positive(message = "El id estado debe ser positivo") Long idEstado){
		
		return ResponseEntity.ok(service.actualizarEstado(idCita, idEstado));
		
	}

}
