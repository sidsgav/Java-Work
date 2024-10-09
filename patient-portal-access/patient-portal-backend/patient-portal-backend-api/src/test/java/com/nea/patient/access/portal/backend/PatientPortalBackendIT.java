package com.nea.patient.access.portal.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.nea.patient.access.portal.backend.jpa.domain.dao.PatientRepository;
import com.nea.patient.access.portal.backend.jpa.domain.enums.Gender;
import com.nea.patient.access.portal.backend.jpa.domain.enums.MaritalStatus;
import com.nea.patient.access.portal.backend.jpa.domain.model.CurrentAddress;
import com.nea.patient.access.portal.backend.jpa.domain.model.Patient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {PatientPortalBackendApplication.class})
public class PatientPortalBackendIT {

  private URL base;

  @LocalServerPort
  private int port;

  @Autowired
  private PatientRepository patientRepository;

  @Autowired
  private TestRestTemplate template;

  @BeforeEach
  public void setUp() throws Exception {
    base = new URL("http://localhost:" + port + "/");
  }

  @Test
  public void persistMinmalPatientAndRetrieveThroughApi() {
    String title = "Mr";
    String firstName = "Gavindeep";
    String middleName = "Singh";
    String surname = "Sidhu";
    Gender gender = Gender.MALE;
    MaritalStatus maritalStatus = MaritalStatus.SINGLE;
    String bloodType = "AB";
    Date dateOfBirth = generateDate(2004, 12, 6);

    Patient patient = Patient.builder()
        .title(title)
        .firstName(firstName)
        .middleName(middleName)
        .surname(surname)
        .gender(gender)
        .maritalStatus(maritalStatus)
        .bloodType(bloodType)
        .dateOfBirth(dateOfBirth)
        .build();

    String addressLine1 = "18 Cockcroft Avenue";
    String district = "Wyken";
    String town = "Coventry";
    String county = "West Midlands";
    String postcode = "CV2 3QP";
    Date fromDate = generateDate(2020, 10, 12);
    CurrentAddress currentAddress = CurrentAddress.builder()
        .addressLine1(addressLine1)
        .district(district)
        .town(town)
        .county(county)
        .postcode(postcode)
        .fromDate(fromDate)
        .build();
    patient.setCurrentAddress(currentAddress);

    Patient savedPatient = patientRepository.save(patient);

    ResponseEntity<Patient> responseEntity = template.getForEntity(base + "/api/patients/{id}",
        Patient.class, savedPatient.getId());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

    Patient returnedPatient = responseEntity.getBody();
    assertNotNull(returnedPatient);
    assertEquals(title, returnedPatient.getTitle());
    assertEquals(firstName, returnedPatient.getFirstName());
    assertEquals(middleName, returnedPatient.getMiddleName());
    assertEquals(surname, returnedPatient.getSurname());
    assertEquals(gender, returnedPatient.getGender());
    assertEquals(maritalStatus, returnedPatient.getMaritalStatus());
    assertEquals(bloodType, returnedPatient.getBloodType());
    assertEquals(dateOfBirth, returnedPatient.getDateOfBirth());

    assertNotNull(returnedPatient.getCurrentAddress());
    assertEquals(addressLine1, returnedPatient.getCurrentAddress().getAddressLine1());
    assertEquals(district, returnedPatient.getCurrentAddress().getDistrict());
    assertEquals(town, returnedPatient.getCurrentAddress().getTown());
    assertEquals(county, returnedPatient.getCurrentAddress().getCounty());
    assertEquals(postcode, returnedPatient.getCurrentAddress().getPostcode());
    // assertEquals(fromDate, returnedPatient.getCurrentAddress().getFromDate());
  }

  private Date generateDate(final int year, final int month, final int dayOfMonth) {
    LocalDate dateToConvert = LocalDate.of(year, month, dayOfMonth);
    return Date.from(dateToConvert.atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant());
  }
}
