package com.nea.patient.access.portal.ui.application;

import org.apache.commons.lang3.StringUtils;

import com.nea.patient.access.portal.backend.jpa.domain.model.CurrentAddress;
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
public class PatientDetails {

  private static final String COMMA = ", ";
  
  private Patient patient;

  @Override
  public String toString() {
    CurrentAddress currentAddress = patient.getCurrentAddress();
    StringBuilder patientSummary = new StringBuilder(patient.getTitle())
        .append(StringUtils.SPACE).append(patient.getFirstName())
        .append(StringUtils.SPACE).append(patient.getSurname())
        .append(COMMA).append(currentAddress.getAddressLine1())
        .append(COMMA).append(currentAddress.getTown())
        .append(COMMA).append(currentAddress.getPostcode());
    return patientSummary.toString();
  }
}
