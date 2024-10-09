package com.nea.patient.access.portal.backend.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nea.patient.access.portal.backend.api.model.PatientSearchResponse;
import com.nea.patient.access.portal.backend.jpa.domain.dao.PatientRepository;
import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;

@RestController
@RequestMapping("/api")
public class PatientPortalBackendController {

  @Autowired
  private PatientRepository patientRepository;

  @GetMapping(path = "/patients/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Patient> getPatientById(@PathVariable("id") final Integer id) {
    Optional<Patient> patientById = patientRepository.findById(id);

    if (patientById.isPresent()) {
      return ResponseEntity.ok(patientById.get());
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping(path = "/patients/search", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PatientSearchResponse> seachPatients(
      @RequestParam(value = "dateOfBirth") @DateTimeFormat(pattern = "dd-MM-yyyy") final Date dateOfBirth,
      @RequestParam(value = "lastName", required = false) final String lastName) {
    List<Patient> patients = new ArrayList<>();
    if (StringUtils.isNotBlank(lastName)) {
      patients = patientRepository.findByDateOfBirthAndSurname(dateOfBirth,
          lastName);
    } else {
      patients = patientRepository.findByDateOfBirth(dateOfBirth);
    }
    PatientSearchResponse searchResponse = PatientSearchResponse.builder()
        .patients(patients).build();
    return ResponseEntity.ok(searchResponse);
  }

  @PostMapping(path = "/patients", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.CREATED)
  @Transactional
  public void createPatient(@RequestBody final Patient newPatient) {
    patientRepository.save(newPatient);
  }

  @PutMapping(path = "/patients", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  @Transactional
  public void updatePatient(@RequestBody final Patient patient) {
    patientRepository.save(patient);
  }
}
