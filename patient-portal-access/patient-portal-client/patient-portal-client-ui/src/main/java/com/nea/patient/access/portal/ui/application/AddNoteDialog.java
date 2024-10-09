package com.nea.patient.access.portal.ui.application;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.springframework.context.ApplicationContext;

import com.nea.patient.access.portal.backend.jpa.domain.model.Note;
import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;
import com.nea.patient.access.portal.ui.api.integration.ApiWebClient;
import com.nea.patient.access.portal.ui.swingextensions.presentationshared.ConfirmationDialog;

public class AddNoteDialog extends ConfirmationDialog {

  private static final long serialVersionUID = 8472745834043867895L;

  private static final int MAX_ROWS_DISPLAY_FOR_NOTES_TEXT_AREA = 10;
  private static final int MAX_COLUMNS_DISPLAY_FOR_NOTES_TEXT_AREA = 70;

  private ApplicationContext applicationContext;
  private ApiWebClient apiWebClient;
  private PatientDetails patientDetails;

  private final JTextArea noteTextArea = new JTextArea(MAX_ROWS_DISPLAY_FOR_NOTES_TEXT_AREA,
      MAX_COLUMNS_DISPLAY_FOR_NOTES_TEXT_AREA);
  private final JPanel contentsPanel = new JPanel(new BorderLayout());

  public AddNoteDialog(final Frame dialogOwner, final String title,
      final ApplicationContext applicationContext, final PatientDetails patientDetails) {
    super(dialogOwner, title);
    this.applicationContext = applicationContext;
    this.patientDetails = patientDetails;
    apiWebClient = this.applicationContext.getBean(ApiWebClient.class);
    initialiseDialog();
    realize();
  }

  private void initialiseDialog() {
    // Set Modality to true.
    setModal(true);

    noteTextArea.setBorder(BorderFactory.createLoweredBevelBorder());
    noteTextArea.addKeyListener(new NoteAreaKeyListener());
    // Create a JScrollPane to house the JTextArea.
    JScrollPane notesTextScroller = new JScrollPane(noteTextArea);
    notesTextScroller.setBorder(BorderFactory.createTitledBorder("Description"));

    contentsPanel.add(notesTextScroller, BorderLayout.CENTER);

    // The following call will setup the required window components and make
    // the dialog resizeable by default, which we need to override.
    layoutWindow(contentsPanel, ConfirmationDialog.CANCEL_BUTTON, null, true);
    overrideOKButtonTextAndMnemonic("Submit", 'S');
    setResizable(false);

    greyConfirmation();
  }

  /**
   * Callback method for the 'OK/Submit' button press. Overridden from super class
   * {@code ConfirmationDialog}.
   */
  @Override
  protected void doOk() {
    Note note = Note.builder()
        .description(noteTextArea.getText())
        .created(generateCurrentDate())
        .build();

    Patient patient = patientDetails.getPatient();
    patient.getNotes().add(note);

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
      setStatusBarInformationText("Issue detected whilst trying to add patient note");
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

  private void checkNoteDetailCaptured() {
    setConfirmationGreyed(noteTextArea.getText().length() == 0);
  }

  private Date generateCurrentDate() {
    LocalDate currentDate = LocalDate.now();
    return Date.from(currentDate.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant());
  }

  private class NoteAreaKeyListener extends KeyAdapter {

    @Override
    public void keyReleased(final KeyEvent e) {
      AddNoteDialog.this.checkNoteDetailCaptured();
    }
  }
}
