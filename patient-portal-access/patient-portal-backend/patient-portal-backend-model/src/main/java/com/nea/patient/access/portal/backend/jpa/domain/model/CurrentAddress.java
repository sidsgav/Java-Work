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
@Table(name = "CurrentAddress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentAddress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String addressLine1;

  private String district;

  private String town;

  private String county;

  private String postcode;

  @Temporal(TemporalType.DATE)
  private Date fromDate;
}
