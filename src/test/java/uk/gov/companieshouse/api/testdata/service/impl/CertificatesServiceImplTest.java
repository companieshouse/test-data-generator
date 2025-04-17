package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CertificatesServiceImplTest {

    @Mock
    private CertificatesRepository repository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private CertificatesServiceImpl service;

    @Captor
    private ArgumentCaptor<Certificates> certificatesCaptor;

    private Certificates certificates;
    private CertificatesSpec certificatesSpec;

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
        certificatesSpec.setItemOptions(itemOptionsSpec);
        certificatesSpec.setKind("certificate-kind");
        certificatesSpec.setPostalDelivery(true);
        certificatesSpec.setQuantity(1);
        certificatesSpec.setUserId("user123");
    }

    @Test
    void createCertificates() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(Certificates.class))).thenReturn(certificates);

        CertificatesData result = service.create(certificatesSpec);

        assertNotNull(result);
        assertEquals(certificates.getId(), result.getId());

        verify(repository).save(certificatesCaptor.capture());
        Certificates captured = certificatesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsSpec expectedOptions = certificatesSpec.getItemOptions();

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
        assertEquals(certificates.getId(), result.getId());

        verify(repository).save(certificatesCaptor.capture());
        Certificates captured = certificatesCaptor.getValue();

        assertEquals("CRT-123456-789012", captured.getId());
        assertEquals(certificatesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certificatesSpec.getCompanyNumber(), captured.getCompanyNumber());
    }

    @Test
    void deleteCertificatesNotFound() {
        String certificateId = "CRT-123456-789012";

        when(repository.findById(certificateId)).thenReturn(java.util.Optional.empty());
        boolean result = service.delete(certificateId);

        assertFalse(result);  // Should return false when not found
        verify(repository, never()).delete(any(Certificates.class));
    }
}
