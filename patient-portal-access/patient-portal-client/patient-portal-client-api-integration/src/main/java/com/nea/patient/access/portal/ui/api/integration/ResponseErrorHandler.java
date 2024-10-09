package com.nea.patient.access.portal.ui.api.integration;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class ResponseErrorHandler extends DefaultResponseErrorHandler {

  @Override
  public boolean hasError(final ClientHttpResponse response) throws IOException {
    boolean hasError = super.hasError(response);
    if (hasError) {
      HttpStatus statusCode = response.getStatusCode();
      if (HttpStatus.BAD_REQUEST.equals(statusCode) ||
          HttpStatus.NOT_FOUND.equals(statusCode)) {
        return false;
      } else {
        return true;
      }
    }
    return false;
  }
}
