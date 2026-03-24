package com.alexander.medicos.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexander.commons.controllers.CommonController;
import com.alexander.commons.dto.MedicoRequest;
import com.alexander.commons.dto.MedicoResponse;
import com.alexander.medicos.services.MedicoService;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping
@Validated
public class MedicosController extends CommonController<MedicoRequest, MedicoResponse, MedicoService>{

	public MedicosController(MedicoService service) {
		super(service);
	}
	
	@GetMapping("/id-medico/{id}")
	public ResponseEntity<MedicoResponse> obtenerMedicoPorIdSinEstado(@PathVariable
			@Positive(message = "El id debe ser positivo") Long id){
		return ResponseEntity.ok(service.obtenerMedicoSinEstado(id));
	}

}
