package com.alexander.citas.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alexander.citas.dto.CitaRequest;
import com.alexander.citas.dto.CitaResponse;
import com.alexander.citas.entities.Cita;
import com.alexander.citas.enums.EstadoCita;
import com.alexander.citas.mappers.CitasMapper;
import com.alexander.citas.repositories.CitaRepository;
import com.alexander.commons.clients.MedicoClient;
import com.alexander.commons.clients.PacienteClient;
import com.alexander.commons.dto.MedicoResponse;
import com.alexander.commons.dto.PacienteResponse;
import com.alexander.commons.enums.EstadoRegistro;
import com.alexander.commons.exceptions.EntidadRelacionadaException;
import com.alexander.commons.exceptions.RecursoNoEncontradoException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CitaServiceImp implements CitaService{
	
	private final CitaRepository citaRepository;
	
	private final CitasMapper citasMapper;
	
	private final PacienteClient pacienteClient;
	
	private final MedicoClient medicoClient;

	@Override
	@Transactional(readOnly = true)
	public List<CitaResponse> listar() {

		return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)
				.stream()
				.map(cita ->
				citasMapper.entityToResponse(
						cita,
						obtenerPacienteResponseSinEstado(cita.getIdPaciente()),
						obtenerMedicoResponseSinEstado(cita.getIdMedico()))
				).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CitaResponse obtenerPorId(Long id) {
		Cita cita = obtenerCitaOException(id);

		return citasMapper.entityToResponse(
				cita,
				obtenerPacienteResponseSinEstado(cita.getIdPaciente()),
				obtenerMedicoResponseSinEstado(cita.getIdMedico()));
	}
	
	@Override
	@Transactional(readOnly = true)
	public CitaResponse obtenerCitaPorIdSinEstado(Long id) {
		
		Cita cita = citaRepository.findById(id).orElseThrow(()->
					new RecursoNoEncontradoException("Cita sin estado no encontrada con id: "+id));
					
		return citasMapper.entityToResponse(cita,
				obtenerPacienteResponseSinEstado(cita.getIdPaciente()),
				obtenerMedicoResponseSinEstado(cita.getIdMedico()));
	}

	@Override
	public CitaResponse registrar(CitaRequest request) {
		
		comprobarCitaExistente(request.idPaciente());
		
		comprobarCitasPendientesEnCursoOConfirmadasMedico(request.idMedico());
			
		PacienteResponse paciente = obtenerPacienteResponse(request.idPaciente());

		MedicoResponse medico = obtenerMedicoDisponibleResponse(request.idMedico());
		
		medicoClient.actualizarDisp(medico.id(), 5L);
		
		Cita cita = citaRepository.save(citasMapper.requestToEntity(request));
		
		return citasMapper.entityToResponse(cita, paciente, medico);
	}

	@Override
	public CitaResponse actualizar(CitaRequest request, Long id) {
		
		comprobarCitaExistentePacienteActualizar(request.idPaciente(), id);
		
		Cita cita = obtenerCitaOException(id);
		
		if (cita.getEstadoCita().equals(EstadoCita.PENDIENTE) || cita.getEstadoCita().equals(EstadoCita.CONFIRMADA)) {
			
		}else {
			throw new IllegalArgumentException("No se puede actualizar una cita que no esté en estado pendiente o confirmada.");
		}
		
		PacienteResponse paciente = obtenerPacienteResponse(request.idPaciente());
		
		MedicoResponse medico;
		
		if (request.idMedico() == cita.getIdMedico()) {
			 medico = obtenerMedicoResponse(request.idMedico());
		}else {
			medico = obtenerMedicoDisponibleResponse(request.idMedico());
		}
		
		comprobarCitasPendientesEnCursoOConfirmadasMedicoActualizar(medico.id(), id);
		
		EstadoCita estadoNuevo = EstadoCita.fromCodigo(request.idEstadoCita());
		
		
		medicoClient.actualizarDisp(cita.getIdMedico(), 1L);
		
		comprobarTransicionCita(cita.getEstadoCita(), estadoNuevo, medico.id());
		
		citasMapper.updateEntityFromRequest(request, cita, estadoNuevo);
		
		return citasMapper.entityToResponse(cita, paciente, medico);
	}

	@Override
	public void eliminar(Long id) {
		Cita cita = obtenerCitaOException(id);
		validarEstadoCitaAlEliminar(cita);
		
		cita.setEstadoRegistro(EstadoRegistro.ELIMINADO);
		
		medicoClient.actualizarDisp(cita.getIdMedico(), 1L);
	}
	
	@Override
	public boolean obtenerCitasConfirmadasOEnCurso(Long id) {
		List<EstadoCita> estadosCita = new ArrayList<>();
		estadosCita.add(EstadoCita.CONFIRMADA);
		estadosCita.add(EstadoCita.EN_CURSO);
		
		Cita cita = citaRepository.findByIdPacienteAndEstadoCitaIn(id, estadosCita);
		if (cita != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean obtenerCitasConfirmadasOEnCursoMedico(Long id) {
		List<EstadoCita> estadosCita = new ArrayList<>();
		estadosCita.add(EstadoCita.CONFIRMADA);
		estadosCita.add(EstadoCita.EN_CURSO);
		
		Cita cita = citaRepository.findByIdMedicoAndEstadoCitaIn(id, estadosCita);
		if (cita != null) {
			return true;
		}
		return false;
	}
	

	@Override
	public CitaResponse actualizarEstado(Long id, Long idEstado) {
		
		Cita cita = obtenerCitaOException(id);
		
		PacienteResponse paciente = obtenerPacienteResponse(cita.getIdPaciente());
		
		MedicoResponse medico = obtenerMedicoResponse(cita.getIdMedico());
		
		EstadoCita estadoNuevo = EstadoCita.fromCodigo(idEstado);
		
		comprobarTransicionCita(cita.getEstadoCita(), estadoNuevo, medico.id());
		
		cita.setEstadoCita(estadoNuevo);
		
		Cita citaActualizada = citaRepository.save(cita);
		
		return citasMapper.entityToResponse(citaActualizada, paciente, medico);
	}
	
	private Cita obtenerCitaOException(Long id) {
		return citaRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(()->
		new RecursoNoEncontradoException("Cita no encontrada con el id: "+id));
	}
	
	private void validarEstadoCitaAlEliminar(Cita cita) {
		if (cita.getEstadoCita() == EstadoCita.CONFIRMADA || cita.getEstadoCita() == EstadoCita.EN_CURSO) {
			throw new EntidadRelacionadaException("No se puede eliminar una cita confirmada o en curso.");
		}
	}
	
	private PacienteResponse obtenerPacienteResponse(Long idPaciente) {
		return pacienteClient.obtenerPacientePorId(idPaciente);
		
	}
	
	private PacienteResponse obtenerPacienteResponseSinEstado(Long idPaciente) {
		return pacienteClient.obtenerPacientePorIdSinEstado(idPaciente);
	}
	
	private MedicoResponse obtenerMedicoResponse(Long idMedico) {
		return medicoClient.obtenerMedicoPorId(idMedico);
	}
	
	private MedicoResponse obtenerMedicoDisponibleResponse(Long idMedico) {
		return medicoClient.obtenerMedicoConDisponibilidad(idMedico);
	}
	
	private MedicoResponse obtenerMedicoResponseSinEstado(Long idMedico) {
		return  medicoClient.obtenerMedicoPorIdSinEstado(idMedico);
	}
	
	private void comprobarCitaExistente(Long idPacente) {
		List<EstadoCita> estadosCita = new ArrayList<>();
		estadosCita.add(EstadoCita.PENDIENTE);
		estadosCita.add(EstadoCita.CONFIRMADA);
		estadosCita.add(EstadoCita.EN_CURSO);
		
		if(citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(idPacente, EstadoRegistro.ACTIVO, estadosCita)) {
			throw new IllegalArgumentException("Este paciente ya tiene una cita activa.");
		}
	}
	
	private void comprobarCitaExistentePacienteActualizar(Long idPacente, Long idCita) {
		List<EstadoCita> estadosCita = new ArrayList<>();
		estadosCita.add(EstadoCita.PENDIENTE);
		estadosCita.add(EstadoCita.CONFIRMADA);
		estadosCita.add(EstadoCita.EN_CURSO);
		
		if(citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(idPacente, EstadoRegistro.ACTIVO, estadosCita, idCita)) {
			throw new IllegalArgumentException("Este paciente ya tiene una cita activa.");
		}
	}
	
	private void comprobarCitasPendientesEnCursoOConfirmadasMedico(Long id) {
		List<EstadoCita> estadosCita = new ArrayList<>();
		estadosCita.add(EstadoCita.PENDIENTE);
		estadosCita.add(EstadoCita.CONFIRMADA);
		estadosCita.add(EstadoCita.EN_CURSO);
		
		if (citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(id, EstadoRegistro.ACTIVO,  estadosCita)) {
			throw new IllegalArgumentException("No se le puede asignar más de una cita a un médico.");
		}
	}
	
	private void comprobarCitasPendientesEnCursoOConfirmadasMedicoActualizar(Long idMedico, Long idCita) {
		List<EstadoCita> estadosCita = new ArrayList<>();
		estadosCita.add(EstadoCita.PENDIENTE);
		estadosCita.add(EstadoCita.CONFIRMADA);
		estadosCita.add(EstadoCita.EN_CURSO);
		
		
		if (citaRepository.existsByIdMedicoAndIdNotAndEstadoRegistroAndEstadoCitaIn
				(idMedico, idCita, EstadoRegistro.ACTIVO,  estadosCita)) {
			throw new IllegalArgumentException("No se le puede asignar más de una cita a un médico.");
		}
	}
	
	private void comprobarTransicionCita(EstadoCita estadoCitaActual, EstadoCita estadoNuevo, Long idMedico) {
		
		switch (estadoCitaActual) {
		case PENDIENTE: 
			
			if (estadoNuevo == EstadoCita.CONFIRMADA){
				medicoClient.actualizarDisp(idMedico, 5L);
				
			}else if (estadoNuevo == EstadoCita.CANCELADA){
				medicoClient.actualizarDisp(idMedico, 1L);
				
			}else if (estadoNuevo == EstadoCita.PENDIENTE){
				medicoClient.actualizarDisp(idMedico, 5L);
				
			}else {
				throw new IllegalStateException("Las citas pendientes solo se pueden confirmar o cancelar");
			}
		
			break;
			
		case CONFIRMADA: 
			
			if (estadoNuevo == EstadoCita.EN_CURSO){
				medicoClient.actualizarDisp(idMedico, 2L);
				
			}else if (estadoNuevo == EstadoCita.CANCELADA){
				medicoClient.actualizarDisp(idMedico, 1L);
				
			}else if (estadoNuevo == EstadoCita.CONFIRMADA){
				medicoClient.actualizarDisp(idMedico, 5L);
				
			}else {
				throw new IllegalStateException("Las citas confirmadas solo se pueden poner en curso o cancelar");
			}
		
			break;
			
		case EN_CURSO: 
			
			if (estadoNuevo == EstadoCita.FINALIZADA){
				medicoClient.actualizarDisp(idMedico, 1L);
				
			}else if (estadoNuevo == EstadoCita.EN_CURSO){
				medicoClient.actualizarDisp(idMedico, 2L);
				
			}else {
				throw new IllegalStateException("Las citas en curso solo se pueden finalizar");
			}
		
			break;
		
		case FINALIZADA: 
			throw new IllegalStateException("No se puede actualizar una cita finalizada");
			
		case CANCELADA: 
			throw new IllegalStateException("No se puede actualizar una cita cancelada");
		
		
		default:
			throw new IllegalStateException("Estado de cita no válido");
		}
		
	}
	

	
}
