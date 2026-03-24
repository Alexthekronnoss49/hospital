package com.alexander.pacientes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alexander.pacientes.entitites.Paciente;
import java.util.Optional;

import com.alexander.commons.enums.EstadoRegistro;
import java.util.List;



@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long>{
	
	List<Paciente> findByEstadoRegistro(EstadoRegistro estadoRegistro);
	
	Optional<Paciente> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByEmailAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);
	
	boolean existsByTelefonoAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);
	
	boolean existsByEmailAndEstadoRegistroAndIdNot(String email, EstadoRegistro estadoRegistro, Long id);
	
	boolean existsByTelefonoAndEstadoRegistroAndIdNot(String telefono, EstadoRegistro estadoRegistro, Long id);

}
