package com.alexander.medicos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alexander.medicos.entities.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long>{

}
