package com.nea.patient.access.portal.ui.application;

import java.util.Arrays;
import java.util.Optional;

public enum MaritalSelection {

  SINGLE("Single"),
  MARRIED("Married"),
  DIVORCED("Divorced"),
  WIDOWED("Widowed"),
  SEPARATED("Separated"),
  COHABITING("Cohabiting");

  private String status;

  private MaritalSelection(final String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public static String[] getMaritalSelectionList() {
    return Arrays.stream(MaritalSelection.values())
        .map(item -> item.getStatus())
        .toArray(String[]::new);
  }

  public static Optional<MaritalSelection> getEnumForText(final String status) {
    return Arrays.stream(MaritalSelection.values())
        .filter(item -> item.getStatus().equals(status))
        .findFirst();
  }
}
