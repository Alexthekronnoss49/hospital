package com.alexander.pacientes.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexander.commons.clients.CitaCliente;
import com.alexander.commons.dto.PacienteRequest;
import com.alexander.commons.dto.PacienteResponse;
import com.alexander.commons.enums.EstadoRegistro;
import com.alexander.commons.exceptions.RecursoNoEncontradoException;
import com.alexander.pacientes.entitites.Paciente;
import com.alexander.pacientes.mappers.PacienteMapper;
import com.alexander.pacientes.repositories.PacienteRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PacienteServiceImplementation implements PacienteService{
	
	private final PacienteRepository pacienteRepository;
	
	private final PacienteMapper pacienteMapper;
	
	private final CitaCliente citaCliente;
	
	@Override
	@Transactional(readOnly = true)
	public List<PacienteResponse> listar() {
		log.info("Listando...");
		return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
				.map(pacienteMapper::entityToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PacienteResponse obtenerPorId(Long id) {
		Paciente paciente = obtenerPacienteOException(id);
		return pacienteMapper.entityToResponse(paciente);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
		return pacienteMapper.entityToResponse(pacienteRepository.findById(id).orElseThrow(() ->
		new RecursoNoEncontradoException("Paciente sin estado no encontrado con id: "+id)));
	}

	@Override
	public PacienteResponse registrar(PacienteRequest request) {
		
		comprobarEmalUnico(request.email());
		
		comprobarTelefonoUnico(request.telefono());
		
		Double imc = caluclarIMC(request.peso(), request.estatura());
		
		String numExp = expediente(request.telefono());
		
		Paciente paciente = pacienteRepository.save(pacienteMapper.requestToEntity(request, imc, numExp));
		
		return pacienteMapper.entityToResponse(paciente);
	}

	@Override
	public PacienteResponse actualizar(PacienteRequest request, Long id) {
		
		if (obtenerDatosCita(id)) {
			throw new
			IllegalArgumentException
			("No se puede actualizar un paciente si tiene citas confirmadas o en curso.");
		}
		
		Paciente paciente = obtenerPacienteOException(id);
		
		comprobarEmalUnicoActualizar(request.email(), id);
		
		comprobarTelefonoUnicoactualizar(request.telefono(), id);
		
		pacienteMapper.updateEntityFromRequest(request, paciente);
		
		if (cambioIMC(request, paciente)) {
			Double imc = caluclarIMC(request.peso(), request.estatura());
			paciente.setImc(imc);
		}
		
		if (cambioTelefono(request, paciente)) {
			String numExp = expediente(request.telefono());
			paciente.setNumExpediente(numExp);
		}
		
		return pacienteMapper.entityToResponse(paciente);
	}

	@Override
	public void eliminar(Long id) {
		Paciente paciente = obtenerPacienteOException(id);
		
		if (obtenerDatosCita(id)) {
			throw new
			IllegalArgumentException
			("No se puede eliminar un paciente si tiene citas confirmadas o en curso.");
		}
		
		paciente.setEstadoRegistro(EstadoRegistro.ELIMINADO);
		
		log.info("Paciente con id {} ha sido marcado como eliminado", id);
	}
	
	
	public Paciente obtenerPacienteOException(Long id) {
		return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(() ->
				new RecursoNoEncontradoException("Paciente activo no encontrado con id: "+id));
	}
	
	private void comprobarEmalUnico(String email) {
		if (pacienteRepository.existsByEmailAndEstadoRegistro(email.toLowerCase(), EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Este correo ya está registrado.");
		}
		
	}
	
	private void comprobarTelefonoUnico(String telefono) {
		if (pacienteRepository.existsByTelefonoAndEstadoRegistro(telefono, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Este teléfono ya está registrado.");
		}
		
	}
	
	private void comprobarEmalUnicoActualizar(String email, Long id) {
		if (pacienteRepository.existsByEmailAndEstadoRegistroAndIdNot(email.toLowerCase(), EstadoRegistro.ACTIVO, id)) {
			throw new IllegalArgumentException("Este correo ya está registrado.");
		}
		
	}
	
	private void comprobarTelefonoUnicoactualizar(String telefono, Long id) {
		if (pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(telefono, EstadoRegistro.ACTIVO, id)) {
			throw new IllegalArgumentException("Este teléfono ya está registrado.");
		}
		
	}
	
	
	private Double caluclarIMC(Double peso, Double estatura) {
		
		Double imc = peso / Math.pow(estatura, 2);
		
		BigDecimal bd = new BigDecimal(imc).setScale(2, RoundingMode.HALF_UP);
		
	    double resultado = bd.doubleValue();
		
		return resultado;
	}
	
	private String expediente(String telefono) {
		
		char[] secuencia = telefono.toCharArray();
		
		String numEx = "";
		
		for(char num : secuencia) {
			numEx += num + "X";
		}
		
		return numEx;
	}
	
	private boolean cambioIMC(PacienteRequest request, Paciente paciente){
		
		return !request.peso().equals(paciente.getPeso()) || !request.estatura().equals(paciente.getEstatura());
		
	}
	
	private boolean cambioTelefono(PacienteRequest request, Paciente paciente){
		
		return !request.telefono().equalsIgnoreCase(paciente.getTelefono());
		
	}

	private boolean obtenerDatosCita(Long idPaciente) {
		return citaCliente.obtenerCitaConfirmadaOEnCurso(idPaciente);
	}

}
