package com.nea.patient.access.portal.backend.api.model;

import java.util.List;

import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSearchResponse {

  private List<Patient> patients;
}
