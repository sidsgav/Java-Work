package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Class to detect keystrokes and monitor fields to check when all required entries have been
 * completed and the OK/Apply buttons can be ungreyed on a {@code ConfirmationDialog}.<br>
 * This class is used by registering each UI component capable of keyboard entry by instantiating an
 * instance of this class and supplying it in call to {@code addKeyListener()}, for example
 * {@code UIcomp.addKeyListener(new ConfirmationGreying(firstRef, secondRef))}. The parameter
 * {@code firstRef} in the example code shown reflects a reference to class
 * {@code ConfirmationDialog} and the parameter {@code secondRef} reflects a reference to class
 * implementing interface {@code ConfirmationGreyingRule} which will generally be the specialised
 * class extending {@code ConfirmationDialog} and the method on interface
 * {@code ConfirmationGreyingRule} is called once a valid key stroke has been detected by this class
 * and this method needs to return a boolean indicating whether all required entries have been
 * completed to allow the OK/Apply buttons to be either greyed or ungreyed by this class.
 */
public final class ConfirmationGreying extends KeyAdapter {

  private final ConfirmationDialog confirmationDialog;

  private final ConfirmationGreyingRule confirmationGreyingArbiter;

  /**
   * Create instance to monitor key events for a UI component that takes user input and which is
   * shown on a window which extends class {@code ConfirmationDialog}. This class should be
   * instantiated and given to the UI input component as follows : UIInputComp.addKeyListener(new
   * ConfirmationGreying(firstRef, secondRef));
   *
   * @param confirmationDialog reference to window class extending {@code ConfirmationDialog} and is
   *        used to call accessor methods on class {@code ConfirmationDialog} to grey or ungrey
   *        buttons.
   * @param confirmationGreyingArbiter reference to interface implementation which will generally be
   *        the window instantiating and displaying the UI input component. The single method of
   *        this interface is called when this class detects a valid key event. If the method of
   *        this interface returns {@code true} then the OK/Apply buttons of
   *        {@code ConfirmationDialog} are greyed, otherwise they are ungreyed.
   */
  public ConfirmationGreying(final ConfirmationDialog confirmationDialog,
      final ConfirmationGreyingRule confirmationGreyingArbiter) {
    this.confirmationDialog = confirmationDialog;
    this.confirmationGreyingArbiter = confirmationGreyingArbiter;

    // Set initial state.
    if (confirmationGreyingArbiter.confirmationGreyingRule()) {
      confirmationDialog.greyConfirmation();
    } else {
      confirmationDialog.ungreyConfirmation();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void keyReleased(final KeyEvent e) {
    // Ensure that the key event is one that we wish to process.
    if (PresentationUtilities.processKeyReleasedEvent(e)) {
      final boolean greyIt = ConfirmationGreying.this.confirmationGreyingArbiter.confirmationGreyingRule();

      // Determine if greying state needs to be changed.
      if (ConfirmationGreying.this.confirmationDialog.isConfirmationGreyed() != greyIt) {
        if (greyIt) {
          ConfirmationGreying.this.confirmationDialog.greyConfirmation();
        } else {
          ConfirmationGreying.this.confirmationDialog.ungreyConfirmation();
        }
      }
    }
  }
}
