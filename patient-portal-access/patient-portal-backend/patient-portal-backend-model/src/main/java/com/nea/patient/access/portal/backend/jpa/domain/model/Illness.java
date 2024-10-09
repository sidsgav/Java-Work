package com.nea.patient.access.portal.backend.jpa.domain.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Illness")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Illness {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String type;

  private String severity;

  @Temporal(TemporalType.DATE)
  private Date lastReviewed;

  @Temporal(TemporalType.DATE)
  private Date diagnosedDate;
}
