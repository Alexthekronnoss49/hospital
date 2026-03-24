package com.alexander.medicos.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alexander.commons.dto.MedicoRequest;
import com.alexander.commons.dto.MedicoResponse;
import com.alexander.commons.enums.DisponibilidadMedico;
import com.alexander.commons.enums.EspecialidadMedico;
import com.alexander.commons.enums.EstadoRegistro;
import com.alexander.commons.exceptions.RecursoNoEncontradoException;
import com.alexander.medicos.entities.Medico;
import com.alexander.medicos.mappers.MedicoMapper;
import com.alexander.medicos.repositories.MedicoRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class MedicoServiceImp  implements MedicoService{
	
	private final MedicoRepository medicoRepository;
	
	private final MedicoMapper medicoMapper;
	
	@Override
	@Transactional(readOnly = true)
	public List<MedicoResponse> listar() {
		return  medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO)
				.stream().map(medicoMapper::entityToResponse).toList();
	}

	@Override
	public MedicoResponse obtenerPorId(Long id) {
		return medicoMapper.entityToResponse(obtenerMedicoOExceptionMedico(id));
	}
	
	@Override
	public MedicoResponse obtenerMedicoSinEstado(Long id) {
		return medicoMapper.entityToResponse(medicoRepository.findById(id).orElseThrow(() ->
		new RecursoNoEncontradoException("Médico sin estado no encontrado con id: "+id)));
	}

	@Override
	public MedicoResponse registrar(MedicoRequest request) {
		
		comprobarEmalUnico(request.email());
		comprobarTelefonoUnico(request.telefono());
		comprobarCedulaUnica(request.cedulaProfesional());
		
		Medico medico = medicoMapper.requestToEntity(request);
		
		medico.setEspecialidadMedico(EspecialidadMedico.fromCodigo(request.idEspecialidad()));
		medico.setDisponibildadMedico(DisponibilidadMedico.DISPONIBLE);
		
		medicoRepository.save(medico);
		
		return medicoMapper.entityToResponse(medico);
	}

	@Override
	public MedicoResponse actualizar(MedicoRequest request, Long id) {
		Medico medico = obtenerMedicoOExceptionMedico(id);
		
		comprobarEmalUnicoActualizar(request.email(), id);
		comprobarTelefonoUnicoActualizar(request.telefono(), id);
		comprobarCedulaUnicaActualizar(request.cedulaProfesional(), id);
		
		medicoMapper.updateEntityFromRequest(request, medico);
		
		medico.setEspecialidadMedico(EspecialidadMedico.fromCodigo(request.idEspecialidad()));
		
		return medicoMapper.entityToResponse(medico);
	}

	@Override
	public void eliminar(Long id) {
		Medico medico = obtenerMedicoOExceptionMedico(id);
		
		medico.setEstadoRegistro(EstadoRegistro.ELIMINADO);
		
	}
	
	private Medico obtenerMedicoOExceptionMedico(Long id) {
		return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(() ->
				new RecursoNoEncontradoException("Médico activo no encontrado con el id: "+id));
	}
	
	private void comprobarEmalUnico(String email) {
		if (medicoRepository.existsByEmailAndEstadoRegistro(email.toLowerCase(), EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Este correo ya está registrado.");
		}
		
	}
	
	private void comprobarTelefonoUnico(String telefono) {
		if (medicoRepository.existsByTelefonoAndEstadoRegistro(telefono, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Este teléfono ya está registrado.");
		}
		
	}
	
	private void comprobarEmalUnicoActualizar(String email, Long id) {
		if (medicoRepository.existsByEmailAndEstadoRegistroAndIdNot(email.toLowerCase(), EstadoRegistro.ACTIVO, id)) {
			throw new IllegalArgumentException("Este correo ya está registrado.");
		}
		
	}
	
	private void comprobarTelefonoUnicoActualizar(String telefono, Long id) {
		if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(telefono, EstadoRegistro.ACTIVO, id)) {
			throw new IllegalArgumentException("Este teléfono ya está registrado.");
		}
		
	}
	
	private void comprobarCedulaUnica(String cedula) {
		if (medicoRepository.existsByCedulaProfesionalAndEstadoRegistro(cedula, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Esa cédula ya está registrada.");
		}
		
	}
	
	private void comprobarCedulaUnicaActualizar(String cedula, Long id) {
		if (medicoRepository.existsByCedulaProfesionalAndIdNotAndEstadoRegistro(cedula, id, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Esa cédula ya está registrada.");
		}
		
	}

	
}
