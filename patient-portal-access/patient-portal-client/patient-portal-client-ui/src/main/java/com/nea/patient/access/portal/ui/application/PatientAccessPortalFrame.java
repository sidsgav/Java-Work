package com.nea.patient.access.portal.ui.application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.nea.patient.access.portal.backend.api.model.PatientSearchResponse;
import com.nea.patient.access.portal.ui.api.integration.ApiWebClient;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.FixedLengthTextField;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.LockableFrame;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.PresentationUtilities;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.SpringPacker;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces.WindowDisposeListener;

@Component
public class PatientAccessPortalFrame extends LockableFrame {

  private static final long serialVersionUID = -3553136175508483298L;

  private static final int MAX_ROWS_FOR_SEARCH_RESULT_LIST = 5;
  private static final int NAME_COMPONENT_DISPLAY_LENGTH = 30;
  private static final int NAME_COMPONENT_MAX_LENGTH = 50;

  private NewPatientDialog newPatientDialog = null;
  private PatientMedicalHistoryDialog newMedicalHistoryDialog = null;
  private UpdatePatientDialog updatePatientDialog = null;
  private AddNoteDialog addNoteDialog = null;

  private ApplicationContext applicationContext;
  private ApiWebClient apiWebClient;

  private JDatePickerImpl dateOfBirthDatePicker;
  private FixedLengthTextField lastNameTF;

  private DefaultListModel<PatientDetails> patientsSearchResultsModel;
  private JList<PatientDetails> patientsSearchResultsList;

  private SelectedPatientButtonAction selectedPatientButtonAction;
  private JButton updateMedicalHistoryButton;
  private JButton updatePatientDetailsButton;
  private JButton addNoteButton;
  private JButton addAppointmentButton;

  public PatientAccessPortalFrame() {
    intialise();
    pack();
  }

  private void intialise() {
    JPanel searchMainPanel = new JPanel(new BorderLayout());
    searchMainPanel.setBorder(new TitledBorder("Patient Lookup"));
    searchMainPanel.setPreferredSize(new Dimension(800, 300));

    UtilDateModel utilDateModel = new UtilDateModel();
    Properties datePickerProperties = new Properties();
    datePickerProperties.put("text.today", "Today");
    datePickerProperties.put("text.month", "Month");
    datePickerProperties.put("text.year", "Year");
    JDatePanelImpl datePanel = new JDatePanelImpl(utilDateModel, datePickerProperties);
    dateOfBirthDatePicker = new JDatePickerImpl(datePanel, new DateWidgetTextFormatter());

    lastNameTF = new FixedLengthTextField(NAME_COMPONENT_DISPLAY_LENGTH,
        NAME_COMPONENT_MAX_LENGTH);

    ImageIcon searchIcon = new ImageIcon(getClass().getResource(
        "/com/nea/patient/access/portal/ui/application/images/Search.gif"));
    JButton searchButton = new JButton(new SearchButtonAction());
    searchButton.setIcon(searchIcon);

    SpringPacker searchPanel = new SpringPacker();
    searchPanel.addRow("Date Of Birth", new SpringPacker.ComponentField(dateOfBirthDatePicker),
        new SpringPacker.ComponentField(searchButton));
    searchPanel.addRow("Last Name", new SpringPacker.ComponentField(lastNameTF));
    searchPanel.pack();

    patientsSearchResultsModel = new DefaultListModel<PatientDetails>();
    patientsSearchResultsList = new JList<PatientDetails>(patientsSearchResultsModel);
    patientsSearchResultsList.setSelectedIndex(-1);
    patientsSearchResultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    patientsSearchResultsList.setVisibleRowCount(MAX_ROWS_FOR_SEARCH_RESULT_LIST);
    patientsSearchResultsList.addListSelectionListener(new SearchResultSelectionListener());

    JScrollPane searchResultListScroller = new JScrollPane(patientsSearchResultsList);
    searchResultListScroller.setBorder(BorderFactory.createTitledBorder("Search Results"));

    selectedPatientButtonAction = new SelectedPatientButtonAction();
    updateMedicalHistoryButton = new JButton(selectedPatientButtonAction);
    updateMedicalHistoryButton.setText("Update Medical History");
    updatePatientDetailsButton = new JButton(selectedPatientButtonAction);
    updatePatientDetailsButton.setText("Update Patient Details");
    addNoteButton = new JButton(selectedPatientButtonAction);
    addNoteButton.setText("Add Note");
    addAppointmentButton = new JButton(selectedPatientButtonAction);
    addAppointmentButton.setText("Add Appointment");

    JPanel selectedPatientOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    selectedPatientOptionsPanel.add(updateMedicalHistoryButton);
    selectedPatientOptionsPanel.add(updatePatientDetailsButton);
    selectedPatientOptionsPanel.add(addNoteButton);
    selectedPatientOptionsPanel.add(addAppointmentButton);

    searchMainPanel.add(searchPanel, BorderLayout.NORTH);
    searchMainPanel.add(searchResultListScroller, BorderLayout.CENTER);
    searchMainPanel.add(selectedPatientOptionsPanel, BorderLayout.SOUTH);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    ImageIcon homeIcon = new ImageIcon(getClass().getResource(
        "/com/nea/patient/access/portal/ui/application/images/HomeIcon.png"));
    JButton homeOptionButton = new JButton("Home", homeIcon);
    homeOptionButton.setBorderPainted(false);

    ImageIcon newPatientIcon = new ImageIcon(getClass().getResource(
        "/com/nea/patient/access/portal/ui/application/images/NewPatient.gif"));
    LaunchNewPatientDialogAction newPatientDialogAction = new LaunchNewPatientDialogAction("New Patient");
    JButton newPatientOptionButton = new JButton(newPatientDialogAction);
    newPatientOptionButton.setIcon(newPatientIcon);
    newPatientOptionButton.setBorderPainted(false);

    JPanel optionsPanel = new JPanel(new GridLayout(0, 1));
    optionsPanel.add(homeOptionButton);
    optionsPanel.add(newPatientOptionButton);

    splitPane.add(optionsPanel, JSplitPane.LEFT);
    splitPane.setContinuousLayout(true);
    splitPane.add(searchMainPanel, JSplitPane.RIGHT);

    ImageIcon applicationIcon = new ImageIcon(getClass().getResource(
        "/com/nea/patient/access/portal/ui/application/images/PatientPortalAccessLogoTransparent.png"));
    JLabel applicationIconLabel = new JLabel(applicationIcon);
    JPanel applicationIconPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    applicationIconPanel.add(applicationIconLabel);

    add(applicationIconPanel, BorderLayout.NORTH);
    add(splitPane, BorderLayout.CENTER);

    ImageIcon windowBarIcon = new ImageIcon(getClass().getResource(
        "/com/nea/patient/access/portal/ui/application/images/WindowBarLogoTransparent.png"));
    setIconImage(windowBarIcon.getImage());

    setPatientSelectedOptions();
  }

  @Override
  public boolean isMainApplicationFrame() {
    return true;
  }

  @Override
  public void doAdditionalUILookAndFeelUpdate() {
    // No implementation required.
  }

  @Override
  public void doForcedDisposeActionOnSecondaryFrameWindows() {
    // No implementation required.
  }

  @Override
  protected int getWindowIdentifier() {
    return WindowDisposeListener.MAIN_PRIMARY_WINDOW;
  }

  @Override
  protected void processWindowEvent(final WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      System.exit(0);
    }
  }

  public void setPatientSelectedOptions() {
    boolean patientSelected = false;
    if (patientsSearchResultsList.getSelectedIndex() != -1) {
      patientSelected = true;
    }
    selectedPatientButtonAction.setEnabled(patientSelected);
  }

  public void setApplicationContext(final ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
    apiWebClient = this.applicationContext.getBean(ApiWebClient.class);
  }

  private void performSearchAction() {
    patientsSearchResultsModel.clear();
    patientsSearchResultsList.setSelectedIndex(-1);

    Date selectedDateOfBirth = (Date) dateOfBirthDatePicker.getModel().getValue();
    if (selectedDateOfBirth == null) {
      PresentationUtilities.displayMessageDialogOfRequiredType(this,
          "Date Of Birth entry is mandatory for search criteria",
          JOptionPane.WARNING_MESSAGE, true);
      return;
    }

    enableWindowLockedState();
    boolean requestSuccess = true;
    PatientSearchResponse searchResponse = null;
    try {
      searchResponse = apiWebClient.searchPatients(selectedDateOfBirth,
          lastNameTF.getText());
    } catch (Exception e) {
      requestSuccess = false;
    }
    disableWindowLockedState();

    if (!requestSuccess || searchResponse == null ||
        searchResponse.getPatients() == null) {
      PresentationUtilities.displayMessageDialogOfRequiredType(this,
          "Issue detected whilst retrieving patient details", JOptionPane.ERROR_MESSAGE, true);
      return;
    }
    if (searchResponse.getPatients().isEmpty()) {
      PresentationUtilities.displayMessageDialogOfRequiredType(this,
          "No matching patient results found", JOptionPane.INFORMATION_MESSAGE, true);
      return;
    }

    searchResponse.getPatients().forEach(patient -> {
      patientsSearchResultsModel.addElement(
          PatientDetails.builder().patient(patient).build());
    });
  }

  public void performSelectedPatientAction(final ActionEvent ae) {
    // Display required Dialog as modal.
    PatientDetails patientDetails = patientsSearchResultsList.getSelectedValue();
    if (ae.getSource() == updateMedicalHistoryButton) {
      if (newMedicalHistoryDialog == null) {
        newMedicalHistoryDialog = new PatientMedicalHistoryDialog(
            this, "Patient Medical History", applicationContext, patientDetails);
        newMedicalHistoryDialog.setVisible(true);
        newMedicalHistoryDialog = null;
      }
    } else if (ae.getSource() == updatePatientDetailsButton) {
      if (updatePatientDialog == null) {
        updatePatientDialog = new UpdatePatientDialog(this, "Update Patient",
            applicationContext, patientDetails);
        updatePatientDialog.setVisible(true);
        updatePatientDialog = null;
      }
    } else if (ae.getSource() == addNoteButton) {
      if (addNoteDialog == null) {
        addNoteDialog = new AddNoteDialog(this, "Add Note", applicationContext, patientDetails);
        addNoteDialog.setVisible(true);
        addNoteDialog = null;
      }
    } else if (ae.getSource() == addAppointmentButton) {
      // TODO: Add functionality
    }
  }

  private class LaunchNewPatientDialogAction extends AbstractAction {

    private LaunchNewPatientDialogAction(final String name) {
      super(name);
    }

    @Override
    public void actionPerformed(final ActionEvent ae) {
      // Display New Patient Dialog as modal.
      if (PatientAccessPortalFrame.this.newPatientDialog == null) {
        PatientAccessPortalFrame.this.newPatientDialog = new NewPatientDialog(
            PatientAccessPortalFrame.this, "New Patient Enrollment",
            PatientAccessPortalFrame.this.applicationContext);
        PatientAccessPortalFrame.this.newPatientDialog.setVisible(true);
        PatientAccessPortalFrame.this.newPatientDialog = null;
      }
    }
  }

  private class SearchButtonAction extends AbstractAction {

    @Override
    public void actionPerformed(final ActionEvent ae) {
      PatientAccessPortalFrame.this.performSearchAction();
    }
  }

  private class SelectedPatientButtonAction extends AbstractAction {

    @Override
    public void actionPerformed(final ActionEvent ae) {
      PatientAccessPortalFrame.this.performSelectedPatientAction(ae);
    }
  }

  private class SearchResultSelectionListener implements ListSelectionListener {

    @Override
    public void valueChanged(final ListSelectionEvent e) {
      PatientAccessPortalFrame.this.setPatientSelectedOptions();
    }
  }
}
