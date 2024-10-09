package com.nea.patient.access.portal.ui.application;

import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.springframework.context.ApplicationContext;

import com.nea.patient.access.portal.backend.jpa.domain.model.CurrentAddress;
import com.nea.patient.access.portal.backend.jpa.domain.model.EmergencyContact;
import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;
import com.nea.patient.access.portal.ui.api.integration.ApiWebClient;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationDialog;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationGreying;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationGreyingRule;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.FixedLengthTextField;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.SpringPacker;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.SpringPackerEqualiser;

public class UpdatePatientDialog extends ConfirmationDialog {

  private static final long serialVersionUID = -1365806174112580124L;

  private static final int NAME_COMPONENT_DISPLAY_LENGTH = 30;
  private static final int NAME_COMPONENT_MAX_LENGTH = 50;
  private static final int CONTACT_NUMBER_COMPONENT_MAX_LENGTH = 15;
  private static final int EMAIL_COMPONENT_MAX_LENGTH = 50;
  private static final int ADDRESS_COMPONENT_MAX_LENGTH = 50;
  private static final int ADDRESS_POSTCODE_COMPONENT_MAX_LENGTH = 10;
  private static final int EMERGENCY_CONTACT_RELATIONSHIP_COMPONENT_MAX_LENGTH = 20;

  private ApplicationContext applicationContext;
  private ApiWebClient apiWebClient;
  private PatientDetails patientDetails;

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

  private boolean dialogRealized = false;

  public UpdatePatientDialog(final Frame dialogOwner, final String title,
      final ApplicationContext applicationContext, final PatientDetails patientDetails) {
    super(dialogOwner, title);
    this.applicationContext = applicationContext;
    this.patientDetails = patientDetails;
    apiWebClient = this.applicationContext.getBean(ApiWebClient.class);
    initialiseDialog();
    reloadPatientDetails();
    realize();
    setConfirmationGreyed(isMandatoryPatientDetailsCaptured());
    dialogRealized = true;
  }

  private void initialiseDialog() {
    // Set Modality to true.
    setModal(true);

    mobileContactTF = new FixedLengthTextField(CONTACT_NUMBER_COMPONENT_MAX_LENGTH,
        CONTACT_NUMBER_COMPONENT_MAX_LENGTH);
    emailContactTF = new FixedLengthTextField(EMAIL_COMPONENT_MAX_LENGTH,
        EMAIL_COMPONENT_MAX_LENGTH);

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
    equaliser.add(addressDetailsPanel);
    equaliser.add(emergencyDetailsPanel);
    equaliser.equalise();

    addressDetailsPanel.pack();
    emergencyDetailsPanel.pack();

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
    emailContactTF.addKeyListener(confirmationGreying);
    mobileContactTF.addKeyListener(confirmationGreying);
    addressLine1TF.addKeyListener(confirmationGreying);
    addressTownTF.addKeyListener(confirmationGreying);
    addressPostcodeTF.addKeyListener(confirmationGreying);
    emergencyContactFirstNameTF.addKeyListener(confirmationGreying);
    emergencyContactLastNameTF.addKeyListener(confirmationGreying);
    emergencyContactMobileContactTF.addKeyListener(confirmationGreying);
    emergencyContactEmailContactTF.addKeyListener(confirmationGreying);
  }

  /**
   * Callback method for the 'OK/Submit' button press. Overridden from super class
   * {@code ConfirmationDialog}.
   */
  @Override
  protected void doOk() {
    Patient patient = patientDetails.getPatient();
    patient.setContactNumber(mobileContactTF.getText());
    patient.setEmail(emailContactTF.getText());

    CurrentAddress currentAddress = patient.getCurrentAddress();
    currentAddress.setAddressLine1(addressLine1TF.getText());
    currentAddress.setDistrict(addressDistrictTF.getText());
    currentAddress.setTown(addressTownTF.getText());
    currentAddress.setCounty(addressCountyTF.getText());
    currentAddress.setPostcode(addressPostcodeTF.getText());

    if (emergencyContactFirstNameTF.getText().length() > 0) {
      EmergencyContact emergencyContact = patient.getEmergencyContact();
      if (emergencyContact == null) {
        emergencyContact = new EmergencyContact();
        patient.setEmergencyContact(emergencyContact);
      }
      emergencyContact.setFirstName(emergencyContactFirstNameTF.getText());
      emergencyContact.setSurname(emergencyContactLastNameTF.getText());
      emergencyContact.setRelationship(emergencyContactRelationshipTF.getText());
      emergencyContact.setContactNumber(emergencyContactMobileContactTF.getText());
      emergencyContact.setEmail(emergencyContactEmailContactTF.getText());
    }

    goBusy();
    boolean requestSuccess = true;
    try {
      apiWebClient.updatePatient(patient);
    } catch (Exception e) {
      requestSuccess = false;
    }
    unBusy();

    if (requestSuccess) {
      doClose();
    } else {
      setStatusBarInformationText("Issue detected whilst trying to update patient details");
    }
  }

  private void reloadPatientDetails() {
    boolean requestSuccess = true;
    Patient patient = null;
    try {
      patient = apiWebClient.getPatient(patientDetails.getPatient().getId());
    } catch (Exception e) {
      requestSuccess = false;
    }

    if (requestSuccess && patient != null) {
      patientDetails.setPatient(patient);

      mobileContactTF.setText(patient.getContactNumber());
      emailContactTF.setText(patient.getEmail());
      addressLine1TF.setText(patient.getCurrentAddress().getAddressLine1());
      addressDistrictTF.setText(patient.getCurrentAddress().getDistrict());
      addressTownTF.setText(patient.getCurrentAddress().getTown());
      addressCountyTF.setText(patient.getCurrentAddress().getCounty());
      addressPostcodeTF.setText(patient.getCurrentAddress().getPostcode());

      EmergencyContact emergencyContact = patient.getEmergencyContact();
      if (emergencyContact != null) {
        emergencyContactFirstNameTF.setText(emergencyContact.getFirstName());
        emergencyContactLastNameTF.setText(emergencyContact.getSurname());
        emergencyContactRelationshipTF.setText(emergencyContact.getRelationship());
        emergencyContactMobileContactTF.setText(emergencyContact.getContactNumber());
        emergencyContactEmailContactTF.setText(emergencyContact.getEmail());
      }
    } else {
      setStatusBarInformationText("Issue detected whilst trying to retrieve latest patient details");
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
    if (dialogRealized) {
      clearStatusBarInformationText();
    }
    // Return true to grey out OK/Submit button, false otherwise.
    if (addressLine1TF.getText().length() == 0 || addressTownTF.getText().length() == 0
        || addressPostcodeTF.getText().length() == 0) {
      return true;
    }

    if (emailContactTF.getText().length() == 0 && mobileContactTF.getText().length() == 0) {
      if (dialogRealized) {
        setStatusBarInformationText("Patient Mobile/Telephone or Email details must be supplied");
      }
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
        if (dialogRealized) {
          setStatusBarInformationText("Patient Emergency Contact Mobile/Telephone or Email details must be supplied");
        }
        return true;
      }
    } else if (emergencyContactFirstNameTF.getText().length() == 0) {
      if (dialogRealized) {
        setStatusBarInformationText("Patient Emergency Contact First Name must be supplied");
      }
      return true;
    } else if (emergencyContactLastNameTF.getText().length() == 0) {
      if (dialogRealized) {
        setStatusBarInformationText("Patient Emergency Contact Last Name must be supplied");
      }
      return true;
    }
    return false;
  }

  private class ConfirmGreyingRule implements ConfirmationGreyingRule {

    @Override
    public boolean confirmationGreyingRule() {
      return UpdatePatientDialog.this.isMandatoryPatientDetailsCaptured();
    }
  }
}
