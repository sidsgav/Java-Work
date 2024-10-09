package com.nea.patient.access.portal.backend.jpa.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MedicalHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String allergies;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "patientId", nullable = false)
  @OrderBy("datePerformed DESC")
  @Builder.Default
  private List<Vaccination> vaccinations = new ArrayList<>();
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "patientId", nullable = false)
  @OrderBy("dateOfOperation DESC")
  @Builder.Default
  private List<Operation> operations = new ArrayList<>();
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "patientId", nullable = false)
  @OrderBy("dateOfTest DESC")
  @Builder.Default
  private List<TestResult> testResults = new ArrayList<>();
  
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "patientId", nullable = false)
  @OrderBy("diagnosedDate DESC")
  @Builder.Default
  private List<Illness> illnesses = new ArrayList<>();
}
