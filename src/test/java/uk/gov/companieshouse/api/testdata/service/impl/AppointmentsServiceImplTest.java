package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.model.rest.request.AppointmentCreationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.enums.OfficerType;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsDataRepository;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class AppointmentsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ENCODED_VALUE = "ENCODED";
    private static final Long GENERATED_ID = 123456789L;
    private static final String ENCODED_INTERNAL_ID = "ENCODED_2";
    private static final String ETAG = "ETAG";
    private static final int INTERNAL_ID_LENGTH = 9;
    private static final String INTERNAL_ID_PREFIX = "8";
    private static final String OFFICER_ID = "ODI5NjY4Nzg2NExOSVRNSEYx";
    private static final String COUNTRY = "England";

    @Mock
    private AddressService addressService;
    @Mock
    private AppointmentsRepository appointmentsRepository;
    @Mock
    private AppointmentsDataRepository appointmentsDataRepository;
    @Mock
    private OfficerRepository officerRepository;
    @Mock
    private RandomService randomService;

    @InjectMocks
    private AppointmentsServiceImpl appointmentsService;

    @Mock
    private Appointment commonAppointment;

    @Test
    void create() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(JurisdictionType.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(JurisdictionType.ENGLAND_WALES))
                .thenReturn("Wales");

        when(appointmentsRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentsDataRepository.save(any())).thenReturn(new AppointmentsData());

        appointmentsService.createAppointment(spec);

        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentsRepository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(ENCODED_VALUE, appointment.getId());
        assertEquals(ENCODED_VALUE, appointment.getAppointmentId());
        assertNotNull(appointment.getCreated());
        assertEquals(INTERNAL_ID_PREFIX + GENERATED_ID, appointment.getInternalId());
        assertEquals(ENCODED_INTERNAL_ID, appointment.getOfficerId());
        assertEquals(COMPANY_NUMBER, appointment.getCompanyNumber());
        assertNotNull(appointment.getUpdated());
        assertEquals("Company " + COMPANY_NUMBER, appointment.getCompanyName());
        assertNotNull(appointment.getUpdatedAt());
        assertTrue(appointment.getForename().startsWith("Test"));
        assertNotNull(appointment.getAppointedOn());
        assertEquals(OfficerType.DIRECTOR.getValue(), appointment.getOfficerRole());
        assertEquals(ETAG, appointment.getEtag());
        assertEquals(mockServiceAddress, appointment.getServiceAddress());
        assertEquals(COMPANY_NUMBER, appointment.getDataCompanyNumber());

        Links links = appointment.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/appointments/" + ENCODED_VALUE, links.getSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID, links.getOfficerSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID + "/appointments", links.getOfficerAppointments());

        assertEquals("Director", appointment.getSurname());
        assertNotNull(appointment.getDateOfBirth());
    }

    @Test
    void createScottish() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setJurisdiction(JurisdictionType.SCOTLAND);
        spec.setOfficerRoles(Collections.singletonList(OfficerType.DIRECTOR));

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(JurisdictionType.SCOTLAND)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(JurisdictionType.SCOTLAND))
                .thenReturn("Scotland");

        Appointment savedApt = new Appointment();
        when(appointmentsRepository.save(any())).thenReturn(savedApt);
        when(appointmentsDataRepository.save(any())).thenReturn(new AppointmentsData());

        appointmentsService.createAppointment(spec);

        ArgumentCaptor<Appointment> aptCaptor = ArgumentCaptor.forClass(Appointment.class);
        verify(appointmentsRepository).save(aptCaptor.capture());

        Appointment appointment = aptCaptor.getValue();
        assertNotNull(appointment);
        assertEquals(ENCODED_VALUE, appointment.getId());
        assertEquals(ENCODED_VALUE, appointment.getAppointmentId());
        assertNotNull(appointment.getCreated());
        assertEquals(INTERNAL_ID_PREFIX + GENERATED_ID, appointment.getInternalId());
        assertEquals(ENCODED_INTERNAL_ID, appointment.getOfficerId());
        assertEquals(COMPANY_NUMBER, appointment.getCompanyNumber());
        assertNotNull(appointment.getUpdated());

        assertEquals("Company " + COMPANY_NUMBER, appointment.getCompanyName());
        assertEquals("active", appointment.getCompanyStatus());
        assertTrue(appointment.getForename().startsWith("Test"));
        assertNotNull(appointment.getAppointedOn());
        assertEquals(OfficerType.DIRECTOR.getValue(), appointment.getOfficerRole());
        assertEquals(ETAG, appointment.getEtag());
        assertEquals(mockServiceAddress, appointment.getServiceAddress());
        assertEquals(COMPANY_NUMBER, appointment.getDataCompanyNumber());

        Links links = appointment.getLinks();
        assertEquals("/company/" + COMPANY_NUMBER + "/appointments/" + ENCODED_VALUE, links.getSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID, links.getOfficerSelf());
        assertEquals("/officers/" + ENCODED_INTERNAL_ID + "/appointments", links.getOfficerAppointments());

        assertEquals("Director", appointment.getSurname());
        assertNotNull(appointment.getDateOfBirth());
    }

    @Test
    void createWithInvalidOfficerRole() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        OfficerType invalidRole = Mockito.spy(OfficerType.DIRECTOR);
        when(invalidRole.getValue()).thenReturn("invalid_role");
        spec.setOfficerRoles(Collections.singletonList(invalidRole));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentsService.createAppointment(spec);
        });
        assertEquals("Invalid officer role: invalid_role", exception.getMessage());
    }

    @Test
    void createWithDefaultOfficerRole() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfAppointments(2);

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(JurisdictionType.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(JurisdictionType.ENGLAND_WALES))
                .thenReturn("Wales");

        Appointment savedApt = new Appointment();
        when(appointmentsRepository.save(any())).thenReturn(savedApt);
        when(appointmentsDataRepository.save(any())).thenReturn(new AppointmentsData());

        appointmentsService.createAppointment(spec);

        verify(appointmentsRepository, times(2)).save(any(Appointment.class));
    }

    @Test
    void createWithMultipleAppointments() {
        final Address mockServiceAddress = new Address("", "", "", "", "", "");
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfAppointments(3);
        spec.setOfficerRoles(Collections.singletonList(OfficerType.DIRECTOR));

        when(randomService.getNumber(INTERNAL_ID_LENGTH)).thenReturn(GENERATED_ID);
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(INTERNAL_ID_PREFIX + GENERATED_ID, 8))
                .thenReturn(ENCODED_INTERNAL_ID);
        when(randomService.getEtag()).thenReturn(ETAG);

        when(addressService.getAddress(JurisdictionType.ENGLAND_WALES)).thenReturn(mockServiceAddress);
        when(addressService.getCountryOfResidence(JurisdictionType.ENGLAND_WALES))
                .thenReturn("Wales");

        Appointment savedApt = new Appointment();
        when(appointmentsRepository.save(any())).thenReturn(savedApt);
        when(appointmentsDataRepository.save(any())).thenReturn(new AppointmentsData());

        appointmentsService.createAppointment(spec);

        verify(appointmentsRepository, times(3)).save(any(Appointment.class));
    }

    @Test
    void delete() {
        Appointment apt = new Appointment();
        apt.setOfficerId("TEST_OFFICER_ID");

        OfficerAppointment officerAppointment = new OfficerAppointment();
        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Collections.singletonList(apt));
        when(officerRepository.findById("TEST_OFFICER_ID"))
                .thenReturn(Optional.of(officerAppointment));

        boolean result = appointmentsService.deleteAllAppointments(COMPANY_NUMBER);

        assertTrue(result);
        verify(officerRepository).findById("TEST_OFFICER_ID");
        verify(officerRepository).delete(officerAppointment);
        verify(appointmentsRepository).deleteAll(Collections.singletonList(apt));
    }

    @Test
    void deleteNoAppointmentData() {
        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());

        boolean result = appointmentsService.deleteAllAppointments(COMPANY_NUMBER);
        assertFalse(result);

        verify(officerRepository, never()).delete(any());
        verify(appointmentsRepository, never()).deleteAll(anyList());
    }

    @Test
    void deleteNoOfficerData() {
        Appointment apt = new Appointment();
        apt.setOfficerId("UNKNOWN_OFFICER_ID");

        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(Collections.singletonList(apt));
        when(officerRepository.findById("UNKNOWN_OFFICER_ID"))
                .thenReturn(Optional.empty());

        boolean result = appointmentsService.deleteAllAppointments(COMPANY_NUMBER);
        assertTrue(result);
        verify(officerRepository, never()).delete(any());
        verify(appointmentsRepository).deleteAll(Collections.singletonList(apt));
    }

    @Test
    void deleteAppointments_shouldDeleteAppointmentsAndOfficers() {
        Appointment appointment = new Appointment();
        appointment.setOfficerId(OFFICER_ID);
        List<Appointment> appointments = List.of(appointment);

        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(appointments);
        OfficerAppointment officer = new OfficerAppointment();
        when(officerRepository.findById(OFFICER_ID)).thenReturn(Optional.of(officer));

        boolean result = appointmentsService.deleteAllAppointments(COMPANY_NUMBER);

        assertTrue(result);
        verify(officerRepository).delete(officer);
        verify(appointmentsRepository).deleteAll(appointments);
    }

    @Test
    void deleteAppointments_shouldReturnFalseIfNoAppointmentsFound() {
        when(appointmentsRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Collections.emptyList());

        boolean result = appointmentsService.deleteAllAppointments(COMPANY_NUMBER);

        assertFalse(result);
        verify(appointmentsRepository, never()).deleteAll(anyList());
    }

    @Test
    void deleteAppointmentsData_shouldDeleteAppointmentsData() {
        AppointmentsData data = new AppointmentsData();
        List<AppointmentsData> dataList = List.of(data);

        when(appointmentsDataRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(dataList);

        boolean result = appointmentsService.deleteAllAppointments(COMPANY_NUMBER);

        assertTrue(result);
        verify(appointmentsDataRepository).deleteAll(dataList);
    }

    @Test
    void deleteAppointmentsData_shouldReturnFalseIfNoDataFound() {
        when(appointmentsDataRepository.findAllByCompanyNumber(COMPANY_NUMBER)).thenReturn(Collections.emptyList());

        boolean result = appointmentsService.deleteAllAppointments(COMPANY_NUMBER);

        assertFalse(result);
        verify(appointmentsDataRepository, never()).deleteAll(anyList());
    }

    @Test
    void setRoleName_shouldCapitalizeFirstLetter() {
        String result = invokeSetRoleName("director");
        assertEquals("Director", result);
    }

    @Test
    void validateOfficerRole_shouldNotThrowForValidRole() {
        assertDoesNotThrow(() -> invokeValidateOfficerRole("director"));
    }

    @Test
    void validateOfficerRole_shouldThrowForInvalidRole() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> invokeValidateOfficerRole("invalid_role"));
        assertEquals("Invalid officer role: invalid_role", ex.getMessage());
    }

    @Test
    void createAppointment_shouldHandleMultipleOfficerRoles() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setOfficerRoles(List.of(OfficerType.DIRECTOR, OfficerType.SECRETARY));
        spec.setNumberOfAppointments(2);

        when(randomService.getNumber(anyInt())).thenReturn(123L);
        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(anyString(), anyInt())).thenReturn("ENCODED_ID");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn(COUNTRY);
        when(appointmentsRepository.save(any())).thenReturn(new Appointment());
        when(appointmentsDataRepository.save(any())).thenReturn(new AppointmentsData());

        appointmentsService.createAppointment(spec);

        verify(appointmentsRepository, times(2)).save(any(Appointment.class));
    }

    @Test
    void createAppointment_shouldNotCreateAppointmentIfNoDefaultOfficerTrue() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber("12345678");
        spec.setNoDefaultOfficer(true);

        appointmentsService.createAppointment(spec);

        verify(appointmentsRepository, never()).save(any());
    }

    @Test
    void createOfficerAppointmentItems_shouldSetSecureOfficerTrue() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber("12345678");
        spec.setSecureOfficer(true);

        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn("England");

        List<OfficerAppointmentItem> items = invokeCreateOfficerAppointmentItems(spec, "APPT_ID", Instant.now(), Instant.now(), "director");
        assertTrue(items.getFirst().isSecureOfficer());
    }

    @Test
    void createOfficerAppointmentItems_shouldSetSecureOfficerFalse() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber("12345678");
        spec.setSecureOfficer(false);

        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn("England");

        List<OfficerAppointmentItem> items = invokeCreateOfficerAppointmentItems(spec, "APPT_ID", Instant.now(), Instant.now(), "director");
        assertFalse(items.getFirst().isSecureOfficer());
    }

    @Test
    void createAppointment_shouldCreateAppointmentIfNoDefaultOfficerFalse() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNoDefaultOfficer(false);

        when(randomService.getNumber(anyInt())).thenReturn(123L);
        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(anyString(), anyInt())).thenReturn("ENCODED_ID");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn(COUNTRY);
        when(appointmentsRepository.save(any())).thenReturn(new Appointment());
        when(appointmentsDataRepository.save(any())).thenReturn(new AppointmentsData());

        appointmentsService.createAppointment(spec);

        verify(appointmentsRepository).save(any());
        verify(appointmentsDataRepository).save(any());
    }

    @Test
    void createBaseAppointment_shouldSetSecureOfficerTrue() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setSecureOfficer(true);

        var request = buildAppointmentCreationRequest(spec);
        Appointment appointment = invokeCreateBaseAppointment(request);

        assertTrue(appointment.isSecureOfficer());
    }

    @Test
    void createBaseAppointment_shouldSetSecureOfficerFalse() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setSecureOfficer(false);

        var request = buildAppointmentCreationRequest(spec);
        Appointment appointment = invokeCreateBaseAppointment(request);

        assertFalse(appointment.isSecureOfficer());
    }

    @Test
    void createBaseAppointment_shouldSetSecureOfficerFalseWhenNull() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setSecureOfficer(null);

        var request = buildAppointmentCreationRequest(spec);
        Appointment appointment = invokeCreateBaseAppointment(request);

        assertFalse(appointment.isSecureOfficer());
    }

    @Test
    void createOfficerAppointmentItems_shouldSetSecureOfficerTrueAndReturnSingleItem() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber("12345678");
        spec.setSecureOfficer(true);

        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn("England");

        List<OfficerAppointmentItem> items = invokeCreateOfficerAppointmentItems(spec, "APPT_ID", Instant.now(), Instant.now(), "director");
        assertEquals(1, items.size());
        assertTrue(items.getFirst().isSecureOfficer());
    }

    @Test
    void createOfficerAppointmentItems_shouldSetSecureOfficerFalseAndReturnSingleItem() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber("12345678");
        spec.setSecureOfficer(false);

        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn("England");

        List<OfficerAppointmentItem> items = invokeCreateOfficerAppointmentItems(spec, "APPT_ID", Instant.now(), Instant.now(), "director");
        assertEquals(1, items.size());
        assertFalse(items.getFirst().isSecureOfficer());
    }

    @Test
    void createOfficerAppointmentItems_shouldSetSecureOfficerFalseWhenNullAndReturnSingleItem() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(false);
        spec.setCompanyNumber("12345678");
        spec.setSecureOfficer(null);

        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn("England");

        List<OfficerAppointmentItem> items = invokeCreateOfficerAppointmentItems(spec, "APPT_ID", Instant.now(), Instant.now(), "director");
        assertEquals(1, items.size());
        assertFalse(items.getFirst().isSecureOfficer());
    }

    @Test
    void createAppointment_shouldNotSaveEntitiesWhenCompanyWithPopulatedStructureIsTrue() {
        CompanyRequest spec = new CompanyRequest();
        spec.setCompanyWithPopulatedStructureOnly(true);
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(randomService.getNumber(anyInt())).thenReturn(123L);
        when(randomService.getEncodedIdWithSalt(anyInt(), anyInt())).thenReturn(ENCODED_VALUE);
        when(randomService.addSaltAndEncode(anyString(), anyInt())).thenReturn("ENCODED_ID");
        when(randomService.getEtag()).thenReturn(ETAG);
        when(addressService.getAddress(any())).thenReturn(new Address("", "", "", "", "", ""));
        when(addressService.getCountryOfResidence(any())).thenReturn(COUNTRY);

        var result = appointmentsService.createAppointment(spec);

        verify(appointmentsRepository, never()).save(any());
        verify(appointmentsDataRepository, never()).save(any());
        assertNotNull(result);
        assertNotNull(result.getAppointment());
        assertNotNull(result.getAppointmentsData());
        assertNotNull(result.getOfficerAppointment());
    }

    private AppointmentCreationRequest buildAppointmentCreationRequest(CompanyRequest spec) {
        return AppointmentCreationRequest.builder()
                .spec(spec)
                .companyNumber(COMPANY_NUMBER)
                .countryOfResidence(COUNTRY)
                .internalId("INTERNAL_ID")
                .officerId(OFFICER_ID)
                .dateTimeNow(Instant.now())
                .appointedOn(Instant.now())
                .appointmentId("APPT_ID")
                .build();
    }

    private Appointment invokeCreateBaseAppointment(AppointmentCreationRequest request) {
        try {
            var method = AppointmentsServiceImpl.class.getDeclaredMethod("createBaseAppointment", AppointmentCreationRequest.class);
            method.setAccessible(true);
            return (Appointment) method.invoke(appointmentsService, request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<OfficerAppointmentItem> invokeCreateOfficerAppointmentItems(
            CompanyRequest spec, String appointmentId, Instant dayNow, Instant dayTimeNow, String role) {
        try {
            var method = AppointmentsServiceImpl.class.getDeclaredMethod(
                    "createOfficerAppointmentItems", CompanyRequest.class, String.class, Instant.class, Instant.class, String.class);
            method.setAccessible(true);
            return (List<OfficerAppointmentItem>) method.invoke(appointmentsService, spec, appointmentId, dayNow, dayTimeNow, role);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String invokeSetRoleName(String role) {
        try {
            var method = AppointmentsServiceImpl.class.getDeclaredMethod("setRoleName", String.class);
            method.setAccessible(true);
            return (String) method.invoke(appointmentsService, role);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeValidateOfficerRole(String role) {
        try {
            var method = AppointmentsServiceImpl.class.getDeclaredMethod("validateOfficerRole", String.class);
            method.setAccessible(true);
            method.invoke(appointmentsService, role);
        } catch (Exception e) {
            if (e.getCause() instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }
}
