package com.nea.patient.access.portal.backend.jpa.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nea.patient.access.portal.backend.jpa.domain.enums.Gender;
import com.nea.patient.access.portal.backend.jpa.domain.enums.MaritalStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String title;

  private String firstName;

  private String middleName;

  private String surname;

  @Temporal(TemporalType.DATE)
  private Date dateOfBirth;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Enumerated(EnumType.STRING)
  private MaritalStatus maritalStatus;

  private String bloodType;

  private String contactNumber;

  private String email;

  private Integer height;

  private Integer weight;
  
  private String nhsNumber;

  @OneToOne(cascade = CascadeType.ALL, optional = true)
  @JoinColumn(name = "emergencyContactId", referencedColumnName = "id")
  private EmergencyContact emergencyContact;

  @OneToOne(cascade = CascadeType.ALL, optional = false)
  @JoinColumn(name = "currentAddressId", referencedColumnName = "id")
  private CurrentAddress currentAddress;

  @OneToOne(cascade = CascadeType.ALL, optional = true)
  @JoinColumn(name = "medicalHistoryId", referencedColumnName = "id")
  private MedicalHistory medicalHistory;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "patientId", nullable = false)
  @OrderBy("created DESC")
  @Builder.Default
  private List<Note> notes = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "patientId", nullable = false)
  @OrderBy("lastPrescribed DESC")
  @Builder.Default
  private List<Prescription> prescriptions = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  @JoinColumn(name = "patientId", nullable = false)
  @OrderBy("dateTime DESC")
  @Builder.Default
  private List<Appointment> appointments = new ArrayList<>();
}
