package com.alexander.citas.services;

import com.alexander.citas.dto.CitaRequest;
import com.alexander.citas.dto.CitaResponse;
import com.alexander.commons.services.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse>{

	CitaResponse obtenerCitaPorIdSinEstado(Long id);
	
	boolean obtenerCitasConfirmadasOEnCurso(Long id);
	
	CitaResponse actualizarEstado(Long idCita, Long idEstado);
	
	boolean obtenerCitasConfirmadasOEnCursoMedico(Long id);
}
