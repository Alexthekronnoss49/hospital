package com.alexander.pacientes.mappers;

import java.net.Authenticator.RequestorType;

import org.springframework.stereotype.Component;

import com.alexander.commons.dto.PacienteRequest;
import com.alexander.commons.dto.PacienteResponse;
import com.alexander.commons.enums.EstadoRegistro;
import com.alexander.commons.mappers.CommonMapper;
import com.alexander.pacientes.entitites.Paciente;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente>{

	@Override
	public PacienteResponse entityToResponse(Paciente entity) {
		if (entity == null) return null;
		
		return new PacienteResponse(
				entity.getId(),
				String.join(" ",
						entity.getNombre(),
						entity.getApellidoPaterno(),
						entity.getApellidoMaterno()),
				entity.getEdad(),
				entity.getPeso(),
				entity.getEstatura(),
				entity.getImc(),
				entity.getEmail(),
				entity.getTelefono(),
				entity.getDireccion(),
				entity.getNumExpediente());
	}
	
	@Override
	public Paciente requestToEntity(PacienteRequest request) {
		if (request == null) return null;
		
		return Paciente.builder()
				.nombre(request.nombre())
				.apellidoPaterno(request.apellidoPaterno())
				.apellidoMaterno(request.apellidoMaterno())
				.email(request.email())
				.edad(request.edad())
				.estatura(request.estatura())
				.peso(request.peso())
				.telefono(request.telefono())
				.direccion(request.direccion())
				.estadoRegistro(EstadoRegistro.ACTIVO)
				.build();
	}
	
	public Paciente requestToEntity(PacienteRequest request, Double imc, String numExpediente) {
		if (request == null) return null;
		
		Paciente paciente = requestToEntity(request);
		
		paciente.setImc(imc);
		
		paciente.setNumExpediente(numExpediente);
		
		return paciente;
	}

	@Override
	public Paciente updateEntityFromRequest(PacienteRequest request, Paciente entity) {
		if (request == null || entity == null) return null;
		
		entity.setNombre(request.nombre());
		entity.setApellidoPaterno(request.apellidoPaterno());
		entity.setApellidoMaterno(request.apellidoMaterno());
		entity.setEmail(request.email().toLowerCase());
		entity.setEdad(request.edad());
		entity.setEstatura(request.estatura());
		entity.setPeso(request.peso());
		entity.setTelefono(request.telefono());
		entity.setDireccion(request.direccion());
		
		return entity;
	}
	
	

}
