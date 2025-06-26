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

import java.util.List;

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
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.BasketSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
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
        itemOptionsSpec.setDeliveryTimescale("standard");
        itemOptionsSpec.setIncludeEmailCopy(false);
        itemOptionsSpec.setCompanyType("ltd");
        itemOptionsSpec.setCompanyStatus("active");

        certificatesSpec = new CertificatesSpec();
        certificatesSpec.setCompanyName("Test Company");
        certificatesSpec.setCompanyNumber(companyNumber);
        certificatesSpec.setDescription("certificate for company " + companyNumber);
        certificatesSpec.setDescriptionIdentifier("certificate");
        certificatesSpec.setDescriptionCompanyNumber(companyNumber);
        certificatesSpec.setDescriptionCertificate("Test Certificate");
        certificatesSpec.setItemOptions(List.of(itemOptionsSpec));
        certificatesSpec.setKind("certificate-kind");
        certificatesSpec.setPostalDelivery(true);
        certificatesSpec.setQuantity(1);
        certificatesSpec.setUserId("user123");

        basket = new Basket();
        basket.setId(certificatesSpec.getUserId()); // Set basket ID to user ID
    }

    @Test
    void createCertificates() throws DataException {
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
        assertEquals(certificatesSpec.getDescriptionCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals(certificatesSpec.getDescriptionCertificate(), captured.getDescriptionCertificate());
        assertEquals(expectedOptions.getCertificateType(), capturedOptions.getCertificateType());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());
        assertEquals(expectedOptions.getIncludeEmailCopy(), capturedOptions.getIncludeEmailCopy());
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
        assertEquals(certificatesSpec.getDescriptionCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals(certificatesSpec.getDescriptionCertificate(), captured.getDescriptionCertificate());
        assertEquals(expectedOptions.getCertificateType(), capturedOptions.getCertificateType());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());
        assertEquals(expectedOptions.getIncludeEmailCopy(), capturedOptions.getIncludeEmailCopy());
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
            assertEquals(certificatesSpec.getDescriptionCompanyNumber(), cert.getDescriptionCompanyNumber());
            assertEquals(certificatesSpec.getDescriptionCertificate(), cert.getDescriptionCertificate());

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
