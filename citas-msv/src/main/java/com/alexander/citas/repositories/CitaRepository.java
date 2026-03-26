package com.alexander.citas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alexander.citas.dto.CitaResponse;
import com.alexander.citas.entities.Cita;
import java.util.List;
import java.util.Optional;

import com.alexander.commons.enums.EstadoRegistro;
import com.alexander.citas.enums.EstadoCita;



@Repository
public interface CitaRepository extends JpaRepository<Cita, Long>{

	List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

	Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByIdPacienteAndEstadoRegistroNot(Long idPaciente, EstadoRegistro estadoRegistro);
	
	boolean existsByIdPacienteAndEstadoRegistroAndIdNot(Long idPaciente, EstadoRegistro estadoRegistro, Long idCita);
	
	Cita findByIdPacienteAndEstadoCitaOrEstadoCita(Long id, EstadoCita estadoCita, EstadoCita estadoCita2);
	
	Cita findByIdMedicoAndEstadoCitaOrEstadoCita(Long idMedico, EstadoCita estadoCita, EstadoCita estadoCita2);
	
	boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn
	(Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estados);
	
	boolean existsByIdMedicoAndIdNotAndEstadoRegistroAndEstadoCitaIn
	(Long idMedico, Long idCita, EstadoRegistro estadoRegistro,  List<EstadoCita> estados);
}
