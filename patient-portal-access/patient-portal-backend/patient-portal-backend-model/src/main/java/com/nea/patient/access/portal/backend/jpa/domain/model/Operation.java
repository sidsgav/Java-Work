package com.nea.patient.access.portal.backend.jpa.domain.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.nea.patient.access.portal.backend.jpa.domain.enums.Anesthesia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Operation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Operation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String description;
  
  private String location;
  
  private String performedBy;

  @Enumerated(EnumType.STRING)
  private Anesthesia anesthesia;
  
  @Temporal(TemporalType.DATE)
  private Date dateOfOperation;
}
