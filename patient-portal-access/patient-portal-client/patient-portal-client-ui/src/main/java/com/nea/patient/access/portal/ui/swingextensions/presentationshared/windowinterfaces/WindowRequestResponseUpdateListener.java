package com.nea.patient.access.portal.ui.swingextensions.presentationshared.windowinterfaces;

import java.util.List;

/**
 * An interface which can be be implemented by a component that represents a window which is able to
 * make network interface requests (generally asynchronous) either for data or forwarding user
 * entered data.
 */
public interface WindowRequestResponseUpdateListener {

  /**
   * Invoked when an intermediate update is received for the originating request.
   *
   * @param actionDescription that represents the action description of the originating request and
   *        can be used when informing the user of the intermediate update. The contents should be
   *        internationalised if required.
   * @param updateMessage indicating the intermediate update, which should be internationalised if
   *        required.
   */
  void requestResponseIntermediateUpdate(String actionDescription, String updateMessage);

  /**
   * Invoked when a request fails as a result of an error. The error message could represent a
   * failure or timeout on either Client/Server. The resulting action of this method call should be
   * to inform the user either through an error dialog or some other appropriate means.
   *
   * @param actionDescription that represents the action description of the originating request and
   *        can be used when informing the user of required error condition. The contents should be
   *        internationalised if required.
   * @param errorMessage indicating the error that occurred, which should be internationalised if
   *        required.
   */
  void informOperatorOfError(String actionDescription, String errorMessage);

  /**
   * Invoked by the returned response message upon a success, which requires the implementing class
   * to extract the data as required and update the UI window appropriately. If the UI window is
   * required to make a number of different interface requests, then the UI window may need to hold
   * some state information of the last request made in order to know what type of data needs to be
   * extracted from the supplied {@code List}.
   *
   * @param responseData container which stores reply data returned for an outgoing request.
   */
  void updateUserInterface(List<?> responseData);
}
