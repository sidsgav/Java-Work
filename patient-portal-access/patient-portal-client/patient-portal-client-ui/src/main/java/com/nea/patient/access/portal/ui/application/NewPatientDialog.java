package com.nea.patient.access.portal.ui.application;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.springframework.context.ApplicationContext;

import com.nea.patient.access.portal.backend.jpa.domain.enums.Gender;
import com.nea.patient.access.portal.backend.jpa.domain.enums.MaritalStatus;
import com.nea.patient.access.portal.backend.jpa.domain.model.CurrentAddress;
import com.nea.patient.access.portal.backend.jpa.domain.model.EmergencyContact;
import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;
import com.nea.patient.access.portal.ui.api.integration.ApiWebClient;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationDialog;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationGreying;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationGreyingRule;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConstrainedReadOnlyComboBox;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.FixedLengthTextField;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.SpringPacker;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.SpringPackerEqualiser;

public class NewPatientDialog extends ConfirmationDialog {

  private static final long serialVersionUID = -770907492743633739L;

  private static final int TITLE_COMPONENT_DISPLAY_MAX_LENGTH = 20;
  private static final int NAME_COMPONENT_DISPLAY_LENGTH = 30;
  private static final int NAME_COMPONENT_MAX_LENGTH = 50;
  private static final int NHS_NUMBER_COMPONENT_MAX_LENGTH = 10;
  private static final int CONTACT_NUMBER_COMPONENT_MAX_LENGTH = 15;
  private static final int EMAIL_COMPONENT_MAX_LENGTH = 50;
  private static final int ADDRESS_COMPONENT_MAX_LENGTH = 50;
  private static final int ADDRESS_POSTCODE_COMPONENT_MAX_LENGTH = 10;
  private static final int EMERGENCY_CONTACT_RELATIONSHIP_COMPONENT_MAX_LENGTH = 20;

  private ApplicationContext applicationContext;
  private ApiWebClient apiWebClient;

  private FixedLengthTextField titleTF;
  private FixedLengthTextField firstNameTF;
  private FixedLengthTextField middleNameTF;
  private FixedLengthTextField lastNameTF;
  private JDatePickerImpl dateOfBirthDatePicker;
  private FixedLengthTextField nhsNumberTF;
  private ConstrainedReadOnlyComboBox genderCB;
  private ConstrainedReadOnlyComboBox maritalStatusCB;
  private FixedLengthTextField emailContactTF;
  private FixedLengthTextField mobileContactTF;

  private FixedLengthTextField addressLine1TF;
  private FixedLengthTextField addressDistrictTF;
  private FixedLengthTextField addressTownTF;
  private FixedLengthTextField addressCountyTF;
  private FixedLengthTextField addressPostcodeTF;

  private FixedLengthTextField emergencyContactFirstNameTF;
  private FixedLengthTextField emergencyContactLastNameTF;
  private FixedLengthTextField emergencyContactRelationshipTF;
  private FixedLengthTextField emergencyContactEmailContactTF;
  private FixedLengthTextField emergencyContactMobileContactTF;

  private final JPanel contentsPanel = new JPanel(new BorderLayout());

  public NewPatientDialog(final Frame dialogOwner, final String title,
      final ApplicationContext applicationContext) {
    super(dialogOwner, title);
    this.applicationContext = applicationContext;
    apiWebClient = this.applicationContext.getBean(ApiWebClient.class);
    initialiseDialog();
    realize();
  }

  private void initialiseDialog() {
    // Set Modality to true.
    setModal(true);

    titleTF = new FixedLengthTextField(TITLE_COMPONENT_DISPLAY_MAX_LENGTH,
        TITLE_COMPONENT_DISPLAY_MAX_LENGTH);
    firstNameTF = new FixedLengthTextField(NAME_COMPONENT_DISPLAY_LENGTH,
        NAME_COMPONENT_MAX_LENGTH);
    middleNameTF = new FixedLengthTextField(NAME_COMPONENT_DISPLAY_LENGTH,
        NAME_COMPONENT_MAX_LENGTH);
    lastNameTF = new FixedLengthTextField(NAME_COMPONENT_DISPLAY_LENGTH,
        NAME_COMPONENT_MAX_LENGTH);

    UtilDateModel utilDateModel = new UtilDateModel();
    Properties datePickerProperties = new Properties();
    datePickerProperties.put("text.today", "Today");
    datePickerProperties.put("text.month", "Month");
    datePickerProperties.put("text.year", "Year");
    JDatePanelImpl datePanel = new JDatePanelImpl(utilDateModel, datePickerProperties);
    dateOfBirthDatePicker = new JDatePickerImpl(datePanel, new DateWidgetTextFormatter());
    dateOfBirthDatePicker.addActionListener(new DatePickerActionListener());

    genderCB = new ConstrainedReadOnlyComboBox(GenderSelection.getGenderSelectionList());
    maritalStatusCB = new ConstrainedReadOnlyComboBox(MaritalSelection.getMaritalSelectionList());
    nhsNumberTF = new FixedLengthTextField(NHS_NUMBER_COMPONENT_MAX_LENGTH,
        NHS_NUMBER_COMPONENT_MAX_LENGTH);
    mobileContactTF = new FixedLengthTextField(CONTACT_NUMBER_COMPONENT_MAX_LENGTH,
        CONTACT_NUMBER_COMPONENT_MAX_LENGTH);
    emailContactTF = new FixedLengthTextField(EMAIL_COMPONENT_MAX_LENGTH,
        EMAIL_COMPONENT_MAX_LENGTH);

    SpringPacker patientDetailsPanel = new SpringPacker();
    patientDetailsPanel.setBorder(new TitledBorder("Patient Details"));
    patientDetailsPanel.addRow("Title *", new SpringPacker.ComponentField(titleTF));
    patientDetailsPanel.addRow("First Name *", new SpringPacker.ComponentField(firstNameTF));
    patientDetailsPanel.addRow("Middle Name", new SpringPacker.ComponentField(middleNameTF));
    patientDetailsPanel.addRow("Last Name *", new SpringPacker.ComponentField(lastNameTF));
    patientDetailsPanel.addRow("Date Of Birth *", new SpringPacker.ComponentField(dateOfBirthDatePicker));
    patientDetailsPanel.addRow("Gender *", new SpringPacker.ComponentField(genderCB));
    patientDetailsPanel.addRow("Marital Status *", new SpringPacker.ComponentField(maritalStatusCB));
    patientDetailsPanel.addRow("NHS Number", new SpringPacker.ComponentField(nhsNumberTF));

    addressLine1TF = new FixedLengthTextField(ADDRESS_COMPONENT_MAX_LENGTH,
        ADDRESS_COMPONENT_MAX_LENGTH);
    addressDistrictTF = new FixedLengthTextField(ADDRESS_COMPONENT_MAX_LENGTH,
        ADDRESS_COMPONENT_MAX_LENGTH);
    addressTownTF = new FixedLengthTextField(ADDRESS_COMPONENT_MAX_LENGTH,
        ADDRESS_COMPONENT_MAX_LENGTH);
    addressCountyTF = new FixedLengthTextField(ADDRESS_COMPONENT_MAX_LENGTH,
        ADDRESS_COMPONENT_MAX_LENGTH);
    addressPostcodeTF = new FixedLengthTextField(ADDRESS_POSTCODE_COMPONENT_MAX_LENGTH,
        ADDRESS_POSTCODE_COMPONENT_MAX_LENGTH);

    SpringPacker addressDetailsPanel = new SpringPacker();
    addressDetailsPanel.setBorder(new TitledBorder("Contact Details"));
    addressDetailsPanel.addRow("Address Line 1 *", new SpringPacker.ComponentField(addressLine1TF));
    addressDetailsPanel.addRow("District", new SpringPacker.ComponentField(addressDistrictTF));
    addressDetailsPanel.addRow("Town *", new SpringPacker.ComponentField(addressTownTF));
    addressDetailsPanel.addRow("County", new SpringPacker.ComponentField(addressCountyTF));
    addressDetailsPanel.addRow("Postcode *", new SpringPacker.ComponentField(addressPostcodeTF));
    addressDetailsPanel.addRow("Mobile/Telephone", new SpringPacker.ComponentField(mobileContactTF));
    addressDetailsPanel.addRow("Email", new SpringPacker.ComponentField(emailContactTF));

    emergencyContactFirstNameTF = new FixedLengthTextField(NAME_COMPONENT_DISPLAY_LENGTH,
        NAME_COMPONENT_MAX_LENGTH);
    emergencyContactLastNameTF = new FixedLengthTextField(NAME_COMPONENT_DISPLAY_LENGTH,
        NAME_COMPONENT_MAX_LENGTH);
    emergencyContactRelationshipTF = new FixedLengthTextField(EMERGENCY_CONTACT_RELATIONSHIP_COMPONENT_MAX_LENGTH,
        EMERGENCY_CONTACT_RELATIONSHIP_COMPONENT_MAX_LENGTH);
    emergencyContactMobileContactTF = new FixedLengthTextField(CONTACT_NUMBER_COMPONENT_MAX_LENGTH,
        CONTACT_NUMBER_COMPONENT_MAX_LENGTH);
    emergencyContactEmailContactTF = new FixedLengthTextField(EMAIL_COMPONENT_MAX_LENGTH,
        EMAIL_COMPONENT_MAX_LENGTH);

    SpringPacker emergencyDetailsPanel = new SpringPacker();
    emergencyDetailsPanel.setBorder(new TitledBorder("Emergency Details (Optional)"));
    emergencyDetailsPanel.addRow("First Name *", new SpringPacker.ComponentField(emergencyContactFirstNameTF));
    emergencyDetailsPanel.addRow("Last Name *", new SpringPacker.ComponentField(emergencyContactLastNameTF));
    emergencyDetailsPanel.addRow("Relationship", new SpringPacker.ComponentField(emergencyContactRelationshipTF));
    emergencyDetailsPanel.addRow("Mobile/Telephone", new SpringPacker.ComponentField(emergencyContactMobileContactTF));
    emergencyDetailsPanel.addRow("Email", new SpringPacker.ComponentField(emergencyContactEmailContactTF));

    SpringPackerEqualiser equaliser = new SpringPackerEqualiser();
    equaliser.add(patientDetailsPanel);
    equaliser.add(addressDetailsPanel);
    equaliser.add(emergencyDetailsPanel);
    equaliser.equalise();

    patientDetailsPanel.pack();
    addressDetailsPanel.pack();
    emergencyDetailsPanel.pack();

    contentsPanel.add(patientDetailsPanel, BorderLayout.NORTH);
    contentsPanel.add(addressDetailsPanel, BorderLayout.CENTER);
    contentsPanel.add(emergencyDetailsPanel, BorderLayout.SOUTH);

    // The following call will setup the required window components and make
    // the dialog resizeable by default, which we need to override.
    layoutWindow(contentsPanel, ConfirmationDialog.CANCEL_BUTTON, null, true);
    overrideOKButtonTextAndMnemonic("Submit", 'S');
    setResizable(false);

    // Add key listener to mandatory text fields that need to be populated for new patient detail
    // submission.
    ConfirmationGreying confirmationGreying = new ConfirmationGreying(this, new ConfirmGreyingRule());
    titleTF.addKeyListener(confirmationGreying);
    firstNameTF.addKeyListener(confirmationGreying);
    lastNameTF.addKeyListener(confirmationGreying);
    emailContactTF.addKeyListener(confirmationGreying);
    mobileContactTF.addKeyListener(confirmationGreying);
    addressLine1TF.addKeyListener(confirmationGreying);
    addressTownTF.addKeyListener(confirmationGreying);
    addressPostcodeTF.addKeyListener(confirmationGreying);
    emergencyContactFirstNameTF.addKeyListener(confirmationGreying);
    emergencyContactLastNameTF.addKeyListener(confirmationGreying);
    emergencyContactMobileContactTF.addKeyListener(confirmationGreying);
    emergencyContactEmailContactTF.addKeyListener(confirmationGreying);

    // Grey the Submit button until the user makes changes to the allowed editable
    // text fields.
    greyConfirmation();
  }

  /**
   * Callback method for the 'OK/Submit' button press. Overridden from super class
   * {@code ConfirmationDialog}.
   */
  @Override
  protected void doOk() {
    Patient patient = Patient.builder()
        .title(titleTF.getText())
        .firstName(firstNameTF.getText())
        .middleName(middleNameTF.getText())
        .surname(lastNameTF.getText())
        .dateOfBirth((Date) dateOfBirthDatePicker.getModel().getValue())
        .gender(getGenderSelectedItem().get())
        .maritalStatus(getMaritalStatusSelectedItem().get())
        .nhsNumber(nhsNumberTF.getText())
        .contactNumber(mobileContactTF.getText())
        .email(emailContactTF.getText())
        .build();

    CurrentAddress currentAddress = CurrentAddress.builder()
        .addressLine1(addressLine1TF.getText())
        .district(addressDistrictTF.getText())
        .town(addressTownTF.getText())
        .county(addressCountyTF.getText())
        .postcode(addressPostcodeTF.getText())
        .fromDate(null) // TODO Should we add a date selection widget like DOB?
        .build();
    patient.setCurrentAddress(currentAddress);

    if (emergencyContactFirstNameTF.getText().length() > 0) {
      EmergencyContact emergencyContact = EmergencyContact.builder()
          .firstName(emergencyContactFirstNameTF.getText())
          .surname(emergencyContactLastNameTF.getText())
          .relationship(emergencyContactRelationshipTF.getText())
          .contactNumber(emergencyContactMobileContactTF.getText())
          .email(emergencyContactEmailContactTF.getText())
          .build();
      patient.setEmergencyContact(emergencyContact);
    }

    goBusy();
    boolean requestSuccess = true;
    try {
      apiWebClient.createNewPatient(patient);
    } catch (Exception e) {
      requestSuccess = false;
    }
    unBusy();

    if (requestSuccess) {
      doClose();
    } else {
      setStatusBarInformationText("Issue detected whilst trying to create new patient details");
    }
  }

  @Override
  public void doAdditionalUILookAndFeelUpdate() {
    // Nothing to be done for this method.
  }

  @Override
  public void doForcedDisposeActionOnSecondaryFrameWindows() {
    // Nothing to be done for this method.
  }

  @Override
  protected int getWindowIdentifier() {
    return MODAL_DIALOG_TYPE;
  }

  private boolean isMandatoryPatientDetailsCaptured() {
    clearStatusBarInformationText();

    // Return true to grey out OK/Submit button, false otherwise.
    Date selectedDate = (Date) dateOfBirthDatePicker.getModel().getValue();
    if (titleTF.getText().length() == 0 || firstNameTF.getText().length() == 0 ||
        lastNameTF.getText().length() == 0 || selectedDate == null) {
      return true;
    }

    if (addressLine1TF.getText().length() == 0 || addressTownTF.getText().length() == 0
        || addressPostcodeTF.getText().length() == 0) {
      return true;
    }

    if (emailContactTF.getText().length() == 0 && mobileContactTF.getText().length() == 0) {
      setStatusBarInformationText("Patient Mobile/Telephone or Email details must be supplied");
      return true;
    }

    // Only enforce mandatory Emergency Contact details to be populated if partially entered.
    if (emergencyContactFirstNameTF.getText().length() == 0 &&
        emergencyContactLastNameTF.getText().length() == 0) {
      return false;
    } else if (emergencyContactFirstNameTF.getText().length() > 0 &&
        emergencyContactLastNameTF.getText().length() > 0) {
      if (emergencyContactMobileContactTF.getText().length() == 0 &&
          emergencyContactEmailContactTF.getText().length() == 0) {
        setStatusBarInformationText("Patient Emergency Contact Mobile/Telephone or Email details must be supplied");
        return true;
      }
    } else if (emergencyContactFirstNameTF.getText().length() == 0) {
      setStatusBarInformationText("Patient Emergency Contact First Name must be supplied");
      return true;
    } else if (emergencyContactLastNameTF.getText().length() == 0) {
      setStatusBarInformationText("Patient Emergency Contact Last Name must be supplied");
      return true;
    }
    return false;
  }

  private Optional<Gender> getGenderSelectedItem() {
    GenderSelection genderSelection = GenderSelection.getEnumForText(
        (String) genderCB.getSelectedItem()).get();
    return Arrays.stream(Gender.values())
        .filter(item -> item.name().equals(genderSelection.name()))
        .findFirst();
  }

  private Optional<MaritalStatus> getMaritalStatusSelectedItem() {
    MaritalSelection martialSelection = MaritalSelection.getEnumForText(
        (String) maritalStatusCB.getSelectedItem()).get();
    return Arrays.stream(MaritalStatus.values())
        .filter(item -> item.name().equals(martialSelection.name()))
        .findFirst();
  }

  private class ConfirmGreyingRule implements ConfirmationGreyingRule {

    @Override
    public boolean confirmationGreyingRule() {
      return NewPatientDialog.this.isMandatoryPatientDetailsCaptured();
    }
  }

  private class DatePickerActionListener implements ActionListener {

    @Override
    public void actionPerformed(final ActionEvent e) {
      if (NewPatientDialog.this.isMandatoryPatientDetailsCaptured()) {
        NewPatientDialog.this.greyConfirmation();
      } else {
        NewPatientDialog.this.ungreyConfirmation();
      }
    }
  }
}
