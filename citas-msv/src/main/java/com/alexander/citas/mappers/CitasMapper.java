package com.alexander.citas.mappers;

import org.springframework.stereotype.Component;

import com.alexander.citas.dto.CitaRequest;
import com.alexander.citas.dto.CitaResponse;
import com.alexander.citas.entities.Cita;
import com.alexander.citas.enums.EstadoCita;
import com.alexander.commons.dto.DatosMedico;
import com.alexander.commons.dto.DatosPaciente;
import com.alexander.commons.dto.MedicoResponse;
import com.alexander.commons.dto.PacienteResponse;
import com.alexander.commons.enums.EstadoRegistro;
import com.alexander.commons.mappers.CommonMapper;

@Component
public class CitasMapper implements CommonMapper<CitaRequest, CitaResponse, Cita>{

	@Override
	public CitaResponse entityToResponse(Cita entity) {
		if (entity ==null) return null;
		
		return new CitaResponse(
				entity.getId(),
				null,
				null,
				entity.getFechaCita(),
				entity.getSintomas(),
				entity.getEstadoCita().getDescripcion());
	}
	
	public CitaResponse entityToResponse(Cita entity, PacienteResponse paciente, MedicoResponse medico) {
		if (entity ==null) return null;
		
		return new CitaResponse(
				entity.getId(),
				pacienteResponseToDatosPaciente(paciente),
				pacienteResponseToDatosMedico(medico),
				entity.getFechaCita(),
				entity.getSintomas(),
				entity.getEstadoCita().getDescripcion());
	}
	
	@Override
	public Cita requestToEntity(CitaRequest request) {
		if(request == null) return null;
		
		return Cita.builder()
				.idPaciente(request.idPaciente())
				.idMedico(request.idMedico())
				.fechaCita(request.fechaCita())
				.sintomas(request.sintomas())
				.estadoCita(EstadoCita.PENDIENTE)
				.estadoRegistro(EstadoRegistro.ACTIVO)
				.build();
	}
	
	@Override
	public Cita updateEntityFromRequest(CitaRequest request, Cita entity) {
		if(request == null || entity == null) return null;
		
		entity.setIdPaciente(request.idPaciente());
		entity.setIdMedico(request.idMedico());
		entity.setFechaCita(request.fechaCita());
		entity.setSintomas(request.sintomas());
		
		return entity;
	}
	
	public Cita updateEntityFromRequest(CitaRequest request, Cita entity, EstadoCita estadoCita) {
		if(request == null || entity == null) return null;
		
		updateEntityFromRequest(request, entity);
		entity.setEstadoCita(estadoCita);

		return entity;
	}
	
	private DatosPaciente pacienteResponseToDatosPaciente(PacienteResponse paciente) {
		if(paciente == null) return null;
		
		return new DatosPaciente(
				paciente.nombre(),
				paciente.numExpediente(),
				paciente.edad() + " años",
				paciente.peso() + " kg.",
				paciente.estatura() + " m.",
				paciente.imc() + "",
				paciente.telefono()
				);
	}
	
	private DatosMedico pacienteResponseToDatosMedico(MedicoResponse medico) {
		if(medico == null) return null;
		
		return new DatosMedico(
				medico.nombre(),
				medico.cedulaProfesional(),
				medico.especialidad()
				);
	}

}
