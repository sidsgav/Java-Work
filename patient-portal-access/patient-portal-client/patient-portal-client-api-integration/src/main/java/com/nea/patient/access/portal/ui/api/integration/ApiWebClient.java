package com.nea.patient.access.portal.ui.api.integration;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.nea.patient.access.portal.backend.api.model.PatientSearchResponse;
import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;

@Service
public class ApiWebClient {

  private static final String PATIENTS_ROUTE = "/patients";
  private static final String PATIENTS_SEARCH_ROUTE = "/patients/search";
  private static final String GET_PATIENT_ROUTE = "/patients/{id}";
  private static final String DATE_OF_BIRTH_PARAMETER_NAME = "dateOfBirth";
  private static final String LAST_NAME_PARAMETER_NAME = "lastName";
  private static final String DATE_PATTERN = "dd-MM-yyyy";

  @Value("${patient.portal.access.api.connect.timeout}")
  private int connectTimeout;
  @Value("${patient.portal.access.api.read.timeout}")
  private int readTimeout;
  @Value("${patient.portal.access.api.host}")
  private String patientPortalAccessApiHost;

  private RestTemplate restTemplate;

  public ApiWebClient() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    requestFactory.setConnectTimeout(connectTimeout);
    requestFactory.setReadTimeout(readTimeout);
    requestFactory.setBufferRequestBody(false);

    restTemplate = new RestTemplate(requestFactory);
    restTemplate.setErrorHandler(new ResponseErrorHandler());
  }

  public boolean createNewPatient(final Patient newPatient) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Patient> request = new HttpEntity<Patient>(newPatient, headers);
    ResponseEntity<?> responseEntity = restTemplate.postForEntity(
        patientPortalAccessApiHost + PATIENTS_ROUTE, request, Void.class);
    if (HttpStatus.CREATED.equals(responseEntity.getStatusCode())) {
      return true;
    }
    return false;
  }

  public boolean updatePatient(final Patient patient) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<Patient> request = new HttpEntity<Patient>(patient, headers);
    ResponseEntity<?> responseEntity = restTemplate.exchange(
        patientPortalAccessApiHost + PATIENTS_ROUTE, HttpMethod.PUT, request,
        Void.class);
    if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      return true;
    }
    return false;
  }

  public Patient getPatient(final Integer patientId) {
    ResponseEntity<Patient> responseEntity = restTemplate.getForEntity(
        patientPortalAccessApiHost + GET_PATIENT_ROUTE, Patient.class, patientId);
    if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      return responseEntity.getBody();
    }
    return null;
  }

  public PatientSearchResponse searchPatients(final Date dateOfBirth, final String lastName) {
    URI uri = null;
    String formattedDate = new SimpleDateFormat(DATE_PATTERN).format(dateOfBirth);

    if (StringUtils.isNotBlank(lastName)) {
      uri = UriComponentsBuilder.fromUriString(
          patientPortalAccessApiHost + PATIENTS_SEARCH_ROUTE)
          .queryParam(DATE_OF_BIRTH_PARAMETER_NAME, formattedDate)
          .queryParam(LAST_NAME_PARAMETER_NAME, lastName)
          .encode().build().toUri();
    } else {
      uri = UriComponentsBuilder.fromUriString(
          patientPortalAccessApiHost + PATIENTS_SEARCH_ROUTE)
          .queryParam(DATE_OF_BIRTH_PARAMETER_NAME, formattedDate)
          .encode().build().toUri();
    }
    ResponseEntity<PatientSearchResponse> responseEntity = restTemplate.getForEntity(
        uri, PatientSearchResponse.class);
    if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
      return responseEntity.getBody();
    }
    return null;
  }
}
