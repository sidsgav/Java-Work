package com.nea.patient.access.portal.backend.jpa.domain.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Integer> {

  List<Patient> findByDateOfBirth(final Date dateOfBirth);

  List<Patient> findByDateOfBirthAndSurname(final Date dateOfBirth, final String surname);
}
