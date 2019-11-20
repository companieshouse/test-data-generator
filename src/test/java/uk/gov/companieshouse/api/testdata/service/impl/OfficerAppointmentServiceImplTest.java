package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class OfficerAppointmentServiceImplTest {

    private static final String ETAG = "etag";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String OFFICER_ID = "OFFICER_ID";
    private static final String APPOINTMENT_ID = "APPOINTMENT_ID";

    @Mock
    private RandomService randomService;

    @Mock
    private OfficerRepository repository;

    @InjectMocks
    private OfficerAppointmentServiceImpl officerListService;

    @Test
    void create() throws DataException {
        when(randomService.getEtag()).thenReturn(ETAG);
        OfficerAppointment savedOfficerAppointment = new OfficerAppointment();
        when(repository.save(Mockito.any())).thenReturn(savedOfficerAppointment);
        
        OfficerAppointment returnedOfficerAppointment = this.officerListService.create(COMPANY_NUMBER, OFFICER_ID, APPOINTMENT_ID);
        
        assertEquals(savedOfficerAppointment, returnedOfficerAppointment);
        
        ArgumentCaptor<OfficerAppointment> officerCaptor = ArgumentCaptor.forClass(OfficerAppointment.class);
        verify(repository).save(officerCaptor.capture());
        OfficerAppointment officerAppointment = officerCaptor.getValue();

        assertEquals(OFFICER_ID, officerAppointment.getId());
        assertNotNull(officerAppointment.getCreatedAt());
        assertNotNull(officerAppointment.getUpdatedAt());
        assertEquals(1, officerAppointment.getTotalResults().intValue());
        assertEquals(1, officerAppointment.getActiveCount().intValue());
        assertEquals(0, officerAppointment.getInactiveCount().intValue());
        assertEquals(1, officerAppointment.getResignedCount().intValue());
        assertFalse(officerAppointment.getCorporateOfficer());
        assertEquals("/officers/" + OFFICER_ID + "/appointments", officerAppointment.getLinks().getSelf());
        assertEquals(ETAG, officerAppointment.getEtag());
        assertEquals(1990, officerAppointment.getDateOfBirthYear().intValue());
        assertEquals(3, officerAppointment.getDateOfBirthMonth().intValue());
        assertEquals("Test DIRECTOR", officerAppointment.getName());

        assertOfficerItem(officerAppointment.getOfficerAppointmentItems().get(0));

    }

    private void assertOfficerItem(OfficerAppointmentItem item) {

        assertEquals("Director", item.getOccupation());
        assertNotNull(item.getAddress());
        assertEquals("Test", item.getForename());
        assertEquals("Director", item.getSurname());
        assertEquals("director", item.getOfficerRole());
        assertEquals("/company/" + COMPANY_NUMBER + "/appointments/" + APPOINTMENT_ID,
                item.getLinks().getSelf());
        assertEquals("/company/" + COMPANY_NUMBER, item.getLinks().getCompany());
        assertEquals("Wales", item.getCountryOfResidence());
        assertNotNull(item.getAppointedOn());
        assertEquals("British", item.getNationality());
        assertNotNull(item.getUpdatedAt());
        assertEquals("Test DIRECTOR", item.getName());
        assertEquals("Company " + COMPANY_NUMBER, item.getCompanyName());
        assertEquals(COMPANY_NUMBER, item.getCompanyNumber());
        assertEquals("active", item.getCompanyStatus());
    }

    @Test
    void createDuplicateKeyException() {
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.officerListService.create(COMPANY_NUMBER, OFFICER_ID, APPOINTMENT_ID)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoException() {
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.officerListService.create(COMPANY_NUMBER, OFFICER_ID, APPOINTMENT_ID)
        );
        assertEquals("failed to insert", exception.getMessage());
    }
}