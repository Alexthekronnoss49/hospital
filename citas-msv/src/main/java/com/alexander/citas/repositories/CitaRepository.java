package com.alexander.citas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alexander.citas.entities.Cita;
import java.util.List;
import java.util.Optional;

import com.alexander.commons.enums.EstadoRegistro;
import com.alexander.citas.enums.EstadoCita;



@Repository
public interface CitaRepository extends JpaRepository<Cita, Long>{

	List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

	Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(Long idPaciente, EstadoRegistro estadoRegistro, List<EstadoCita> estados);
	
	boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaInAndIdNot(Long idPaciente, EstadoRegistro estadoRegistro, List<EstadoCita> estados, Long idCita);
	
	Cita findByIdPacienteAndEstadoCitaIn(Long id, List<EstadoCita> estadoCita);
	
	Cita findByIdMedicoAndEstadoCitaIn(Long idMedico, List<EstadoCita> estadoCita);
	
	boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn
	(Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estados);
	
	boolean existsByIdMedicoAndIdNotAndEstadoRegistroAndEstadoCitaIn
	(Long idMedico, Long idCita, EstadoRegistro estadoRegistro,  List<EstadoCita> estados);
}
