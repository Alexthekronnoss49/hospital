package com.alexander.medicos.services;

import com.alexander.commons.dto.MedicoRequest;
import com.alexander.commons.dto.MedicoResponse;
import com.alexander.commons.services.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse>{

	MedicoResponse obtenerMedicoSinEstado(Long id);
	
}
