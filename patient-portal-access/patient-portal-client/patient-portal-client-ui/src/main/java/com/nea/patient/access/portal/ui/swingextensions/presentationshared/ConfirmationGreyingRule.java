package com.nea.patient.access.portal.ui.swingextensions.presentationshared;

/**
 * An interface used in conjunction with class {@code ConfirmationGreying} and
 * {@code ConfirmationDialog}. The component (which will generally subclass
 * {@code ConfirmationDialog}) implementing the single method of this interface decides whether all
 * required entries have been completed and thus return {@code boolean} indicating whether the
 * OK/Apply buttons (available on {@code ConfirmationDialog} can be ungreyed or should remain
 * greyed.
 */
public interface ConfirmationGreyingRule {

  /**
   * This method will generally be implemented by a class presenting a window that allows data to be
   * inserted into a one or more UI components and this method when called by class
   * {@code ConfirmationGreying} needs to determine whether all required UI input fields are
   * populated to allow say the OK/Apply buttons of class {@code ConfirmationDialog} to be ungreyed
   * or remain greyed. This method will be called by class {@code ConfirmationGreying} when a valid
   * key event has been detected on registered UI input component.
   *
   * @return the value {@code true} means keep the buttons greyed, otherwise ungrey the buttons.
   */
  boolean confirmationGreyingRule();
}
