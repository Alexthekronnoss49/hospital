package com.alexander.pacientes.services;

import com.alexander.commons.dto.PacienteRequest;
import com.alexander.commons.dto.PacienteResponse;
import com.alexander.commons.services.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse>{
	
	PacienteResponse obtenerPacientePorIdSinEstado(Long id);

}
