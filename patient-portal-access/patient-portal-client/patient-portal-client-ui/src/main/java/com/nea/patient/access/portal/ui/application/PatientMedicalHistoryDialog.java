package com.nea.patient.access.portal.ui.application;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.springframework.context.ApplicationContext;

import com.nea.patient.access.portal.backend.jpa.domain.model.Illness;
import com.nea.patient.access.portal.backend.jpa.domain.model.MedicalHistory;
import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;
import com.nea.patient.access.portal.ui.api.integration.ApiWebClient;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationDialog;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationGreying;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationGreyingRule;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.FixedLengthTextField;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ReadOnlyTextField;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.SpringPacker;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.SpringPackerEqualiser;

public class PatientMedicalHistoryDialog extends ConfirmationDialog {

  private static final long serialVersionUID = 2618215593297688821L;

  private static final int NAME_COMPONENT_DISPLAY_LENGTH = 30;
  private static final int BLOOD_TYPE_COMPONENT_DISPLAY_MAX_LENGTH = 6;
  private static final int NHS_NUMBER_COMPONENT_MAX_LENGTH = 10;
  private static final int HEIGHT_WEIGHT_COMPONENT_MAX_LENGTH = 6;
  private static final int MAX_ROWS_DISPLAY_FOR_ALLERGIES_TEXT_AREA = 3;

  private ApplicationContext applicationContext;
  private ApiWebClient apiWebClient;
  private PatientDetails patientDetails;

  private ReadOnlyTextField firstNameTF;
  private ReadOnlyTextField lastNameTF;

  private FixedLengthTextField nhsNumberTF;
  private FixedLengthTextField bloodTypeTF;
  private FixedLengthTextField heightTF;
  private FixedLengthTextField weightTF;

  private JCheckBox asthmaCheckBox;
  private JCheckBox arthritisCheckBox;
  private JCheckBox cancerCheckBox;
  private JCheckBox eczemaCheckBox;
  private JCheckBox diabetesCheckBox;
  private JCheckBox epilepsySeizuresCheckBox;
  private JCheckBox kidneyStonesCheckBox;
  private JCheckBox heartDiseaseCheckBox;
  private JCheckBox highBloodPressureCheckBox;
  private JCheckBox digestiveProblemsCheckBox;
  private JCheckBox hepatitisCheckBox;
  private JCheckBox muscularDystrophyCheckBox;

  private final JTextArea allergiesTextArea = new JTextArea();
  private final JPanel contentsPanel = new JPanel(new BorderLayout());

  private boolean dialogRealized = false;

  public PatientMedicalHistoryDialog(final Frame dialogOwner, final String title,
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

    firstNameTF = new ReadOnlyTextField(patientDetails.getPatient().getFirstName(),
        NAME_COMPONENT_DISPLAY_LENGTH);
    lastNameTF = new ReadOnlyTextField(patientDetails.getPatient().getSurname(),
        NAME_COMPONENT_DISPLAY_LENGTH);

    nhsNumberTF = new FixedLengthTextField(NHS_NUMBER_COMPONENT_MAX_LENGTH,
        NHS_NUMBER_COMPONENT_MAX_LENGTH);
    bloodTypeTF = new FixedLengthTextField(BLOOD_TYPE_COMPONENT_DISPLAY_MAX_LENGTH,
        BLOOD_TYPE_COMPONENT_DISPLAY_MAX_LENGTH);
    heightTF = new FixedLengthTextField(HEIGHT_WEIGHT_COMPONENT_MAX_LENGTH,
        HEIGHT_WEIGHT_COMPONENT_MAX_LENGTH);
    weightTF = new FixedLengthTextField(HEIGHT_WEIGHT_COMPONENT_MAX_LENGTH,
        HEIGHT_WEIGHT_COMPONENT_MAX_LENGTH);

    SpringPacker patientDetailsPanel = new SpringPacker();
    patientDetailsPanel.addRow("First Name", new SpringPacker.ComponentField(firstNameTF));
    patientDetailsPanel.addRow("Last Name", new SpringPacker.ComponentField(lastNameTF));
    patientDetailsPanel.addRow("NHS Number *", new SpringPacker.ComponentField(nhsNumberTF));
    patientDetailsPanel.addRow("Blood Type", new SpringPacker.ComponentField(bloodTypeTF));
    patientDetailsPanel.addRow("Height (cm's) *", new SpringPacker.ComponentField(heightTF));
    patientDetailsPanel.addRow("Weight (kg's) *", new SpringPacker.ComponentField(weightTF));

    asthmaCheckBox = new JCheckBox();
    arthritisCheckBox = new JCheckBox();
    cancerCheckBox = new JCheckBox();
    eczemaCheckBox = new JCheckBox();
    diabetesCheckBox = new JCheckBox();
    epilepsySeizuresCheckBox = new JCheckBox();
    kidneyStonesCheckBox = new JCheckBox();
    heartDiseaseCheckBox = new JCheckBox();
    highBloodPressureCheckBox = new JCheckBox();
    digestiveProblemsCheckBox = new JCheckBox();
    hepatitisCheckBox = new JCheckBox();
    muscularDystrophyCheckBox = new JCheckBox();

    SpringPacker illnessConditionsPanel = new SpringPacker();
    illnessConditionsPanel.setBorder(new TitledBorder("Illness/Conditions (Please check all that apply)"));
    illnessConditionsPanel.addRow("Asthma", new SpringPacker.ComponentField(asthmaCheckBox));
    illnessConditionsPanel.addRow("Arthritis", new SpringPacker.ComponentField(arthritisCheckBox));
    illnessConditionsPanel.addRow("Cancer", new SpringPacker.ComponentField(cancerCheckBox));
    illnessConditionsPanel.addRow("Diabetes", new SpringPacker.ComponentField(diabetesCheckBox));
    illnessConditionsPanel.addRow("Digestive Problems", new SpringPacker.ComponentField(digestiveProblemsCheckBox));
    illnessConditionsPanel.addRow("Eczema", new SpringPacker.ComponentField(eczemaCheckBox));
    illnessConditionsPanel.addRow("Epilepsy Seizures", new SpringPacker.ComponentField(epilepsySeizuresCheckBox));
    illnessConditionsPanel.addRow("Heart Disease", new SpringPacker.ComponentField(heartDiseaseCheckBox));
    illnessConditionsPanel.addRow("Hepatitis", new SpringPacker.ComponentField(hepatitisCheckBox));
    illnessConditionsPanel.addRow("High Blood Pressure", new SpringPacker.ComponentField(highBloodPressureCheckBox));
    illnessConditionsPanel.addRow("Kidney Stones", new SpringPacker.ComponentField(kidneyStonesCheckBox));
    illnessConditionsPanel.addRow("Muscular Dystrophy", new SpringPacker.ComponentField(muscularDystrophyCheckBox));

    SpringPackerEqualiser equaliser = new SpringPackerEqualiser();
    equaliser.add(patientDetailsPanel);
    equaliser.add(illnessConditionsPanel);
    equaliser.equalise();

    patientDetailsPanel.pack();
    illnessConditionsPanel.pack();

    allergiesTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
    allergiesTextArea.setRows(MAX_ROWS_DISPLAY_FOR_ALLERGIES_TEXT_AREA);
    // Create a JScrollPane to house the JTextArea.
    JScrollPane allergiesTextScroller = new JScrollPane(allergiesTextArea);
    allergiesTextScroller.setBorder(BorderFactory.createTitledBorder(
        "Please list any drug allergies"));

    contentsPanel.add(patientDetailsPanel, BorderLayout.NORTH);
    contentsPanel.add(illnessConditionsPanel, BorderLayout.CENTER);
    contentsPanel.add(allergiesTextScroller, BorderLayout.SOUTH);

    // The following call will setup the required window components and make
    // the dialog resizeable by default, which we need to override.
    layoutWindow(contentsPanel, ConfirmationDialog.CANCEL_BUTTON, null, true);
    overrideOKButtonTextAndMnemonic("Submit", 'S');
    setResizable(false);

    // Add key listener to mandatory text fields that need to be populated for new patient detail
    // submission.
    ConfirmationGreying confirmationGreying = new ConfirmationGreying(this, new ConfirmGreyingRule());
    heightTF.addKeyListener(confirmationGreying);
    weightTF.addKeyListener(confirmationGreying);
    nhsNumberTF.addKeyListener(confirmationGreying);
  }

  /**
   * Callback method for the 'OK/Submit' button press. Overridden from super class
   * {@code ConfirmationDialog}.
   */
  @Override
  protected void doOk() {
    Patient patient = patientDetails.getPatient();
    patient.setBloodType(bloodTypeTF.getText());
    patient.setNhsNumber(nhsNumberTF.getText());
    patient.setHeight(Integer.valueOf(heightTF.getText()));
    patient.setWeight(Integer.valueOf(weightTF.getText()));

    MedicalHistory medicalHistory = patient.getMedicalHistory();
    if (medicalHistory == null) {
      medicalHistory = MedicalHistory.builder().build();
      patient.setMedicalHistory(medicalHistory);
    }
    medicalHistory.setAllergies(allergiesTextArea.getText());

    if (asthmaCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Asthma");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Asthma").build());
      }
    }
    if (arthritisCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Arthritis");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Arthritis").build());
      }
    }
    if (cancerCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Cancer");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Cancer").build());
      }
    }
    if (eczemaCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Eczema");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Eczema").build());
      }
    }
    if (diabetesCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Diabetes");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Diabetes").build());
      }
    }
    if (epilepsySeizuresCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Epilepsy Seizures");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Epilepsy Seizures").build());
      }
    }
    if (kidneyStonesCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Kidney Stones");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Kidney Stones").build());
      }
    }
    if (heartDiseaseCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Heart Disease");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Heart Disease").build());
      }
    }
    if (highBloodPressureCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "High Blood Pressure");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("High Blood Pressure").build());
      }
    }
    if (digestiveProblemsCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Digestive Problems");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Digestive Problems").build());
      }
    }
    if (hepatitisCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Hepatitis");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Hepatitis").build());
      }
    }
    if (muscularDystrophyCheckBox.isSelected()) {
      Optional<Illness> illness = getExistingIllnessOfType(medicalHistory, "Muscular Dystrophy");
      if (!illness.isPresent()) {
        medicalHistory.getIllnesses().add(Illness.builder().type("Muscular Dystrophy").build());
      }
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
      setStatusBarInformationText("Issue detected whilst trying to update patient medical history");
    }
  }

  private Optional<Illness> getExistingIllnessOfType(final MedicalHistory medicalHistory, final String type) {
    return medicalHistory.getIllnesses().stream()
        .filter(illness -> type.equals(illness.getType()))
        .findFirst();
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

      nhsNumberTF.setText(patient.getNhsNumber());
      bloodTypeTF.setText(patient.getBloodType());
      if (patient.getHeight() != null) {
        heightTF.setText(String.valueOf(patient.getHeight()));
      }
      if (patient.getWeight() != null) {
        weightTF.setText(String.valueOf(patient.getWeight()));
      }

      MedicalHistory medicalHistory = patient.getMedicalHistory();
      if (medicalHistory != null) {
        allergiesTextArea.setText(medicalHistory.getAllergies());

        for (Illness illness : medicalHistory.getIllnesses()) {
          if ("Asthma".equals(illness.getType())) {
            asthmaCheckBox.setSelected(true);
          }
          if ("Arthritis".equals(illness.getType())) {
            arthritisCheckBox.setSelected(true);
          }
          if ("Cancer".equals(illness.getType())) {
            cancerCheckBox.setSelected(true);
          }
          if ("Eczema".equals(illness.getType())) {
            eczemaCheckBox.setSelected(true);
          }
          if ("Diabetes".equals(illness.getType())) {
            diabetesCheckBox.setSelected(true);
          }
          if ("Epilepsy Seizures".equals(illness.getType())) {
            epilepsySeizuresCheckBox.setSelected(true);
          }
          if ("Kidney Stones".equals(illness.getType())) {
            kidneyStonesCheckBox.setSelected(true);
          }
          if ("Heart Disease".equals(illness.getType())) {
            heartDiseaseCheckBox.setSelected(true);
          }
          if ("High Blood Pressure".equals(illness.getType())) {
            highBloodPressureCheckBox.setSelected(true);
          }
          if ("Digestive Problems".equals(illness.getType())) {
            digestiveProblemsCheckBox.setSelected(true);
          }
          if ("Hepatitis".equals(illness.getType())) {
            hepatitisCheckBox.setSelected(true);
          }
          if ("Muscular Dystrophy".equals(illness.getType())) {
            muscularDystrophyCheckBox.setSelected(true);
          }
        }
      }
    } else {
      setStatusBarInformationText("Issue detected whilst trying to retrieve latest patient details");
    }
  }

  private boolean isMandatoryPatientDetailsCaptured() {
    if (dialogRealized) {
      clearStatusBarInformationText();
    }
    // Return true to grey out OK/Submit button, false otherwise.
    if (nhsNumberTF.getText().length() == 0 && weightTF.getText().length() == 0
        && heightTF.getText().length() == 0) {
      if (dialogRealized) {
        setStatusBarInformationText("NHS Number, Height and Weight must be supplied");
      }
      return true;
    }
    return false;
  }

  private class ConfirmGreyingRule implements ConfirmationGreyingRule {

    @Override
    public boolean confirmationGreyingRule() {
      return PatientMedicalHistoryDialog.this.isMandatoryPatientDetailsCaptured();
    }
  }
}
