package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Basket;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.DirectorDetails;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.entity.RegisteredOfficeAddressDetails;
import uk.gov.companieshouse.api.testdata.model.entity.SecretaryDetails;
import uk.gov.companieshouse.api.testdata.model.rest.BasketSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.DirectorDetailsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.RegisteredOfficeAddressDetailsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.SecretaryDetailsSpec;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CertificatesServiceImplTest {

    @Mock
    private CertificatesRepository repository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private CertificatesServiceImpl service;

    @Captor
    private ArgumentCaptor<Certificates> certificatesCaptor;

    private Certificates certificates;
    private CertificatesSpec certificatesSpec;

    private Basket basket;

    @BeforeEach
    void setUp() {
        var companyNumber = "12345678";
        certificates = new Certificates();
        certificates.setId("CRT-123456-789012");

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setCertificateType("incorporation-with-all-name-changes");

        certificatesSpec = new CertificatesSpec();
        certificatesSpec.setCompanyName("Test Company");
        certificatesSpec.setCompanyNumber(companyNumber);
        certificatesSpec.setDescription("certificate for company " + companyNumber);
        certificatesSpec.setDescriptionIdentifier("certificate");
        certificatesSpec.setItemOptions(List.of(itemOptionsSpec));
        certificatesSpec.setKind("certificate-kind");
        certificatesSpec.setPostalDelivery(true);
        certificatesSpec.setQuantity(1);
        certificatesSpec.setUserId("user123");

        basket = new Basket();
        basket.setId(certificatesSpec.getUserId()); // Set basket ID to user ID
    }

    @Test
    void createCertificatesWithMandatoryValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(Certificates.class))).thenReturn(certificates);

        CertificatesData result = service.create(certificatesSpec);

        assertNotNull(result);
        assertEquals(certificates.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certificatesCaptor.capture());
        Certificates captured = certificatesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsSpec expectedOptions = certificatesSpec.getItemOptions().getFirst();

        assertEquals("CRT-123456-789012", captured.getId());
        assertEquals(certificatesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certificatesSpec.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certificate for company " + certificatesSpec.getCompanyNumber(), captured.getDescription());
        assertEquals(certificatesSpec.getDescriptionIdentifier(), captured.getDescriptionIdentifier());
        assertEquals(certificatesSpec.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("certificate for company " + certificatesSpec.getCompanyNumber(), captured.getDescriptionCertificate());
        assertEquals(expectedOptions.getCertificateType(), capturedOptions.getCertificateType());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());
        assertEquals(expectedOptions.getCompanyType(), capturedOptions.getCompanyType());
        assertEquals(expectedOptions.getCompanyStatus(), capturedOptions.getCompanyStatus());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certificatesSpec.getKind(), captured.getKind());
        assertEquals("/orderable/certificates/CRT-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certificatesSpec.getQuantity(), captured.getQuantity());
        assertEquals(certificatesSpec.getUserId(), captured.getUserId());
    }

    @Test
    void createCertificatesWithOptionalValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(Certificates.class))).thenReturn(certificates);

        DirectorDetailsSpec directorDetailsSpec = new DirectorDetailsSpec();
        directorDetailsSpec.setIncludeAddress(true);
        directorDetailsSpec.setIncludeAppointmentDate(true);
        directorDetailsSpec.setIncludeBasicInformation(true);
        directorDetailsSpec.setIncludeCountryOfResidence(true);
        directorDetailsSpec.setIncludeDobType("partial");
        directorDetailsSpec.setIncludeNationality(true);
        directorDetailsSpec.setIncludeOccupation(true);

        SecretaryDetailsSpec secretaryDetailsSpec = new SecretaryDetailsSpec();
        secretaryDetailsSpec.setIncludeAddress(true);
        secretaryDetailsSpec.setIncludeAppointmentDate(true);
        secretaryDetailsSpec.setIncludeBasicInformation(true);
        secretaryDetailsSpec.setIncludeCountryOfResidence(true);
        secretaryDetailsSpec.setIncludeDobType("partial");
        secretaryDetailsSpec.setIncludeNationality(true);
        secretaryDetailsSpec.setIncludeOccupation(true);

        RegisteredOfficeAddressDetailsSpec registeredOfficeAddressDetailsSpec = new RegisteredOfficeAddressDetailsSpec();
        registeredOfficeAddressDetailsSpec.setIncludeAddressRecordsType("all");
        registeredOfficeAddressDetailsSpec.setIncludeDates(false);

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setCompanyStatus("active");
        itemOptionsSpec.setCompanyType("ltd");
        itemOptionsSpec.setDeliveryMethod("postal");
        itemOptionsSpec.setDeliveryTimescale("standard");
        itemOptionsSpec.setDirectorDetails(directorDetailsSpec);
        itemOptionsSpec.setForeName("test");
        itemOptionsSpec.setIncludeCompanyObjectsInformation(true);
        itemOptionsSpec.setIncludeEmailCopy(false);
        itemOptionsSpec.setIncludeGoodStandingInformation(true);
        itemOptionsSpec.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetailsSpec);
        itemOptionsSpec.setSecretaryDetails(secretaryDetailsSpec);

        certificatesSpec.setItemOptions(List.of(itemOptionsSpec));

        CertificatesData result = service.create(certificatesSpec);

        assertNotNull(result);
        assertEquals(certificates.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certificatesCaptor.capture());
        Certificates captured = certificatesCaptor.getValue();

        assertCoreCertificateFields(certificatesSpec, captured);
        assertItemOptionFields(itemOptionsSpec, captured.getItemOptions());
        assertDirectorFields(directorDetailsSpec, captured.getItemOptions().getDirectorDetails());
        assertSecretaryFields(secretaryDetailsSpec, captured.getItemOptions().getSecretaryDetails());
        assertROAddressFields(registeredOfficeAddressDetailsSpec, captured.getItemOptions().getRegisteredOfficeAddressDetails());
    }

    private void assertCoreCertificateFields(CertificatesSpec spec, Certificates cert) {
        assertEquals("CRT-123456-789012", cert.getId());
        assertEquals(spec.getCompanyName(), cert.getCompanyName());
        assertEquals(spec.getCompanyNumber(), cert.getCompanyNumber());
        assertEquals("certificate for company " + spec.getCompanyNumber(), cert.getDescription());
        assertEquals(spec.getDescriptionIdentifier(), cert.getDescriptionIdentifier());
        assertEquals(spec.getCompanyNumber(), cert.getDescriptionCompanyNumber());
        assertEquals("certificate for company " + spec.getCompanyNumber(), cert.getDescriptionCertificate());
        assertEquals("etag123", cert.getEtag());
        assertEquals(spec.getKind(), cert.getKind());
        assertEquals("/orderable/certificates/CRT-123456-789012", cert.getLinksSelf());
        assertTrue(cert.isPostalDelivery());
        assertEquals(spec.getQuantity(), cert.getQuantity());
        assertEquals(spec.getUserId(), cert.getUserId());
    }

    private void assertItemOptionFields(ItemOptionsSpec expected, ItemOptions actual) {
        assertEquals(expected.getCertificateType(), actual.getCertificateType());
        assertEquals(expected.getDeliveryTimescale(), actual.getDeliveryTimescale());
        assertEquals(expected.getIncludeEmailCopy(), actual.getIncludeEmailCopy());
        assertEquals(expected.getCompanyType(), actual.getCompanyType());
        assertEquals(expected.getCompanyStatus(), actual.getCompanyStatus());
        assertEquals(expected.getDeliveryMethod(), actual.getDeliveryMethod());
        assertEquals(expected.getForeName(), actual.getForeName());
        assertEquals(expected.getIncludeCompanyObjectsInformation(), actual.getIncludeCompanyObjectsInformation());
        assertEquals(expected.getIncludeGoodStandingInformation(), actual.getIncludeGoodStandingInformation());
    }

    private void assertDirectorFields(DirectorDetailsSpec expected, DirectorDetails actual) {
        assertEquals(expected.getIncludeAddress(), actual.getIncludeAddress());
        assertEquals(expected.getIncludeAppointmentDate(), actual.getIncludeAppointmentDate());
        assertEquals(expected.getIncludeBasicInformation(), actual.getIncludeBasicInformation());
        assertEquals(expected.getIncludeCountryOfResidence(), actual.getIncludeCountryOfResidence());
        assertEquals(expected.getIncludeDobType(), actual.getIncludeDobType());
        assertEquals(expected.getIncludeNationality(), actual.getIncludeNationality());
        assertEquals(expected.getIncludeOccupation(), actual.getIncludeOccupation());
    }

    private void assertSecretaryFields(SecretaryDetailsSpec expected, SecretaryDetails actual) {
        assertEquals(expected.getIncludeAddress(), actual.getIncludeAddress());
        assertEquals(expected.getIncludeAppointmentDate(), actual.getIncludeAppointmentDate());
        assertEquals(expected.getIncludeBasicInformation(), actual.getIncludeBasicInformation());
        assertEquals(expected.getIncludeCountryOfResidence(), actual.getIncludeCountryOfResidence());
        assertEquals(expected.getIncludeDobType(), actual.getIncludeDobType());
        assertEquals(expected.getIncludeNationality(), actual.getIncludeNationality());
        assertEquals(expected.getIncludeOccupation(), actual.getIncludeOccupation());
    }

    private void assertROAddressFields(RegisteredOfficeAddressDetailsSpec expected, RegisteredOfficeAddressDetails actual) {
        assertEquals(expected.getIncludeAddressRecordsType(), actual.getIncludeAddressRecordsType());
        assertEquals(expected.getIncludeDates(), actual.getIncludeDates());
    }

    @Test
    void createCertificatesWithBasket() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");

        BasketSpec basketSpec = new BasketSpec();
        basketSpec.setForename("John");
        basketSpec.setSurname("Doe");
        basketSpec.setEnrolled(true);
        certificatesSpec.setBasketSpec(basketSpec);

        Basket basket = new Basket();
        basket.setForename(basketSpec.getForename());
        basket.setSurname(basketSpec.getSurname());
        basket.setEnrolled(true);

        when(repository.save(any(Certificates.class))).thenAnswer(invocation -> {
            Certificates cert = invocation.getArgument(0);
            cert.setBasket(basket);
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        CertificatesData result = service.create(certificatesSpec);

        assertNotNull(result);
        assertEquals(certificates.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certificatesCaptor.capture());
        Certificates captured = certificatesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsSpec expectedOptions = certificatesSpec.getItemOptions().getFirst();

        assertEquals("CRT-123456-789012", captured.getId());
        assertEquals(certificatesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certificatesSpec.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certificate for company " + certificatesSpec.getCompanyNumber(), captured.getDescription());
        assertEquals(certificatesSpec.getDescriptionIdentifier(), captured.getDescriptionIdentifier());
        assertEquals(certificatesSpec.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("certificate for company " + certificatesSpec.getCompanyNumber(), captured.getDescriptionCertificate());
        assertEquals(expectedOptions.getCertificateType(), capturedOptions.getCertificateType());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());
        assertEquals(expectedOptions.getCompanyType(), capturedOptions.getCompanyType());
        assertEquals(expectedOptions.getCompanyStatus(), capturedOptions.getCompanyStatus());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certificatesSpec.getKind(), captured.getKind());
        assertEquals("/orderable/certificates/CRT-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certificatesSpec.getQuantity(), captured.getQuantity());
        assertEquals(certificatesSpec.getUserId(), captured.getUserId());

        assertNotNull(captured.getBasket());
        Basket capturedBasket = captured.getBasket();

        assertEquals(basketSpec.getForename(), capturedBasket.getForename());  // Now should pass
        assertEquals(basketSpec.getSurname(), capturedBasket.getSurname());
        assertTrue(capturedBasket.isEnrolled());
    }

    @Test
    void createBasketWhenBasketAlreadyExists() throws DataException {
        var existingBasket = new Basket();
        existingBasket.setId("user-123");
        existingBasket.setItems(new ArrayList<>(List.of(new Basket.Item())));

        when(basketRepository.findById("user-123")).thenReturn(Optional.of(existingBasket));
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");

        var itemOptions = new ItemOptionsSpec();
        itemOptions.setCertificateType("incorporation");
        var spec = new CertificatesSpec();
        spec.setUserId("user-123");
        spec.setCompanyName("Test Company");
        spec.setCompanyNumber("12345678");
        spec.setKind("item#certificate");
        spec.setQuantity(1);
        spec.setPostalDelivery(true);
        spec.setItemOptions(List.of(itemOptions));
        spec.setBasketSpec(new BasketSpec());

        CertificatesData result = service.create(spec);

        ArgumentCaptor<Basket> basketCaptor = ArgumentCaptor.forClass(Basket.class);
        verify(basketRepository).save(basketCaptor.capture());

        Basket savedBasket = basketCaptor.getValue();
        assertEquals(2, savedBasket.getItems().size());
        assertEquals(1, result.getCertificates().size());
    }

    @Test
    void createMultipleCertificatesFromMultipleItemOptions() throws DataException {
        when(randomService.getNumber(6))
            .thenReturn(699255L, 990509L, 582923L, 900231L);
        when(randomService.getEtag())
            .thenReturn("etag1", "etag2");

        ItemOptionsSpec itemOption1 = new ItemOptionsSpec();
        itemOption1.setCertificateType("incorporation-with-all-name-changes");
        itemOption1.setDeliveryTimescale("standard");
        itemOption1.setIncludeEmailCopy(true);
        itemOption1.setCompanyType("ltd");
        itemOption1.setCompanyStatus("active");

        ItemOptionsSpec itemOption2 = new ItemOptionsSpec();
        itemOption2.setCertificateType("incorporation-with-all-name-changes");
        itemOption2.setDeliveryTimescale("standard");
        itemOption2.setIncludeEmailCopy(false);
        itemOption2.setCompanyType("ltd");
        itemOption2.setCompanyStatus("active");

        certificatesSpec.setItemOptions(List.of(itemOption1, itemOption2));

        BasketSpec basketSpec = new BasketSpec();
        basketSpec.setForename("John");
        basketSpec.setSurname("Doe");
        basketSpec.setEnrolled(true);
        certificatesSpec.setBasketSpec(basketSpec);

        // Capture each certificate saved
        when(repository.save(any(Certificates.class))).thenAnswer(invocation -> {
            Certificates cert = invocation.getArgument(0);
            cert.setBasket(new Basket());
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(new Basket());

        CertificatesData results = service.create(certificatesSpec);

        assertEquals(2, results.getCertificates().size());

        verify(repository, times(2)).save(certificatesCaptor.capture());
        List<Certificates> capturedCertificates = certificatesCaptor.getAllValues();

        assertEquals(2, capturedCertificates.size());

        for (int i = 0; i < capturedCertificates.size(); i++) {
            Certificates cert = capturedCertificates.get(i);
            ItemOptionsSpec expectedOptions = certificatesSpec.getItemOptions().get(i);

            assertEquals(certificatesSpec.getCompanyName(), cert.getCompanyName());
            assertEquals(certificatesSpec.getCompanyNumber(), cert.getCompanyNumber());
            assertEquals("certificate for company " + certificatesSpec.getCompanyNumber(), cert.getDescription());
            assertEquals(certificatesSpec.getDescriptionIdentifier(), cert.getDescriptionIdentifier());
            assertEquals(certificatesSpec.getCompanyNumber(), cert.getDescriptionCompanyNumber());
            assertEquals("certificate for company " + certificatesSpec.getCompanyNumber(), cert.getDescriptionCertificate());

            assertEquals(expectedOptions.getCertificateType(), cert.getItemOptions().getCertificateType());
            assertEquals(expectedOptions.getDeliveryTimescale(), cert.getItemOptions().getDeliveryTimescale());
            assertEquals(expectedOptions.getIncludeEmailCopy(), cert.getItemOptions().getIncludeEmailCopy());
            assertEquals(expectedOptions.getCompanyType(), cert.getItemOptions().getCompanyType());
            assertEquals(expectedOptions.getCompanyStatus(), cert.getItemOptions().getCompanyStatus());

            assertEquals(certificatesSpec.getKind(), cert.getKind());
            assertEquals(certificatesSpec.getQuantity(), cert.getQuantity());
            assertEquals(certificatesSpec.getUserId(), cert.getUserId());
        }
    }

    @Test
    void deleteCertificates() {
        when(repository.findById("CRT-123456-789012")).thenReturn(java.util.Optional.of(certificates));

        boolean result = service.delete("CRT-123456-789012");
        assertTrue(result);
        verify(repository).delete(certificates);
    }

    @Test
    void createCertificatesWithDefaultValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(Certificates.class))).thenReturn(certificates);

        CertificatesData result = service.create(certificatesSpec);

        assertNotNull(result);
        assertEquals(certificates.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certificatesCaptor.capture());
        Certificates captured = certificatesCaptor.getValue();

        assertEquals("CRT-123456-789012", captured.getId());
        assertEquals(certificatesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certificatesSpec.getCompanyNumber(), captured.getCompanyNumber());
    }

    @Test
    void deleteCertificatesBasketNotFound() {
        String certificateId = "CRT-123456-789012";

        when(repository.findById(certificateId)).thenReturn(java.util.Optional.empty());
        boolean result = service.delete(certificateId);

        assertFalse(result);  // Should return false when not found
        verify(repository, never()).delete(any(Certificates.class));
        verify(basketRepository, never()).delete(any(Basket.class));
    }

    @Test
    void deleteCertificatesBasket() {
        String certificateId = "CRT-123456-789012";
        certificates.setUserId("user123");
        basket.setId(certificates.getUserId());

        when(repository.findById(certificateId)).thenReturn(java.util.Optional.of(certificates));
        when(basketRepository.findById(certificates.getUserId())).thenReturn(java.util.Optional.of(basket));

        boolean result = service.delete(certificateId);

        assertTrue(result);

        verify(repository).delete(certificates);
        verify(basketRepository).delete(basket);
    }
}
