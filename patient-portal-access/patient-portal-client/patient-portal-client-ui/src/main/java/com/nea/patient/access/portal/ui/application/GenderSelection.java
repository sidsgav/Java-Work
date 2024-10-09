package com.nea.patient.access.portal.ui.application;

import java.util.Arrays;
import java.util.Optional;

public enum GenderSelection {

  UNKNOWN("Unknown"),
  MALE("Male"),
  FEMALE("Female");

  private String gender;

  private GenderSelection(final String gender) {
    this.gender = gender;
  }

  public String getGender() {
    return gender;
  }

  public static String[] getGenderSelectionList() {
    return Arrays.stream(GenderSelection.values())
        .map(item -> item.getGender())
        .toArray(String[]::new);
  }

  public static Optional<GenderSelection> getEnumForText(final String gender) {
    return Arrays.stream(GenderSelection.values())
        .filter(item -> item.getGender().equals(gender))
        .findFirst();
  }
}
