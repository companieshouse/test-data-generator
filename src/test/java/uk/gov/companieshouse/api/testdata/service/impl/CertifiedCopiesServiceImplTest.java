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
import uk.gov.companieshouse.api.testdata.model.entity.Capital;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.CertifiedCopies;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryDocument;
import uk.gov.companieshouse.api.testdata.model.entity.ItemCosts;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.BasketSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CapitalSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertifiedCopiesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDescriptionValuesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDocumentsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemCostsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.CertifiedCopiesRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CertifiedCopiesServiceImplTest {

    @Mock
    private CertifiedCopiesRepository repository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private AddressService addressService;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private CertifiedCopiesServiceImpl service;

    @Captor
    private ArgumentCaptor<CertifiedCopies> certifiedCopiesCaptor;

    private CertifiedCopies certifiedCopies;
    private CertifiedCopiesSpec certifiedCopiesSpec;

    private Basket basket;

    @BeforeEach
    void setUp() {
        certifiedCopies = new CertifiedCopies();
        certifiedCopies.setId("CCD-123456-789012");
        var companyNumber = "12345678";

        CapitalSpec capitalSpec = new CapitalSpec();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesSpec descriptionValuesSpec = new FilingHistoryDescriptionValuesSpec();
        descriptionValuesSpec.setDate("2019-11-10");
        descriptionValuesSpec.setCapital(List.of(capitalSpec));

        FilingHistoryDocumentsSpec filingHistoryDocument = new FilingHistoryDocumentsSpec();
        filingHistoryDocument.setFilingHistoryDate("2019-11-23");
        filingHistoryDocument.setFilingHistoryDescription("capital-allotment-shares");
        filingHistoryDocument.setFilingHistoryDescriptionValues(descriptionValuesSpec);
        filingHistoryDocument.setFilingHistoryId("OTAwMzQ1NjM2M2FkaXF6a6N4");
        filingHistoryDocument.setFilingHistoryType("SH01");
        filingHistoryDocument.setFilingHistoryCost("15");

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setDeliveryTimescale("postal");
        itemOptionsSpec.setDeliveryMethod("standard");
        itemOptionsSpec.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocument));

        ItemCostsSpec itemCostsSpec = new ItemCostsSpec();
        itemCostsSpec.setDiscountApplied("0");
        itemCostsSpec.setItemCost("15");
        itemCostsSpec.setCalculatedCost("15");
        itemCostsSpec.setProductType("certified-copy");

        certifiedCopiesSpec = new CertifiedCopiesSpec();
        certifiedCopiesSpec.setCompanyName("Test Company");
        certifiedCopiesSpec.setCompanyNumber(companyNumber);
        certifiedCopiesSpec.setItemCosts(List.of(itemCostsSpec));
        certifiedCopiesSpec.setItemOptions(List.of(itemOptionsSpec));
        certifiedCopiesSpec.setKind("certified-copy-kind");
        certifiedCopiesSpec.setPostalDelivery(true);
        certifiedCopiesSpec.setQuantity(1);
        certifiedCopiesSpec.setUserId("user123");

        basket = new Basket();
        basket.setId(certifiedCopiesSpec.getUserId());
    }


    @Test
    void createCertifiedCopies() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CertificatesData result = service.create(certifiedCopiesSpec);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsSpec expectedOptions = certifiedCopiesSpec.getItemOptions().getFirst();

        List<FilingHistoryDocument> capturedFilingHistoryDocument = capturedOptions.getFilingHistoryDocuments();
        List<FilingHistoryDocumentsSpec> expectedFilingHistoryDocument = expectedOptions.getFilingHistoryDocuments();

        List<Capital> capturedCapital = capturedFilingHistoryDocument.getFirst().getFilingHistoryDescriptionValues().getCapital();
        List<CapitalSpec> expectedCapital = expectedFilingHistoryDocument.getFirst().getFilingHistoryDescriptionValues()
            .getCapital();

        List<ItemCosts> capturedCosts =  captured.getItemCosts();
        ItemCostsSpec expectedCosts = certifiedCopiesSpec.getItemCosts().getFirst();


        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesSpec.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), captured.getDescription());
        assertEquals("certified-copy", captured.getDescriptionIdentifier());
        assertEquals(certifiedCopiesSpec.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), captured.getDescriptionCertifiedCopy());
        assertEquals(expectedCosts.getDiscountApplied(), capturedCosts.getFirst().getDiscountApplied());
        assertEquals(expectedCosts.getItemCost(), capturedCosts.getFirst().getItemCost());
        assertEquals(expectedCosts.getCalculatedCost(), capturedCosts.getFirst().getCalculatedCost());
        assertEquals(expectedCosts.getProductType(), capturedCosts.getFirst().getProductType());
        assertEquals(expectedOptions.getDeliveryMethod(), capturedOptions.getDeliveryMethod());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryDate(), capturedFilingHistoryDocument.getFirst().getFilingHistoryDate());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryDescription(), capturedFilingHistoryDocument.getFirst().getFilingHistoryDescription());
        assertEquals(expectedCapital.getFirst().getFigure(), capturedCapital.getFirst().getFigure());
        assertEquals(expectedCapital.getFirst().getCurrency(), capturedCapital.getFirst().getCurrency());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryId(), capturedFilingHistoryDocument.getFirst().getFilingHistoryId());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryType(), capturedFilingHistoryDocument.getFirst().getFilingHistoryType());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryCost(), capturedFilingHistoryDocument.getFirst().getFilingHistoryCost());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certifiedCopiesSpec.getKind(), captured.getKind());
        assertEquals("/orderable/certified-copies/CCD-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certifiedCopiesSpec.getQuantity(), captured.getQuantity());
        assertEquals(certifiedCopiesSpec.getUserId(), captured.getUserId());
    }

    @Test
    void createCertifiedCopiesWithBasket() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");

        BasketSpec basketSpec = new BasketSpec();
        basketSpec.setForename("John");
        basketSpec.setSurname("Doe");
        basketSpec.setEnrolled(true);
        certifiedCopiesSpec.setBasketSpec(basketSpec);

        Basket basket = new Basket();
        basket.setForename(basketSpec.getForename());
        basket.setSurname(basketSpec.getSurname());
        basket.setEnrolled(true);

        when(repository.save(any(CertifiedCopies.class))).thenAnswer(invocation -> {
            CertifiedCopies cert = invocation.getArgument(0);
            cert.setBasket(basket);
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(basket);

        CertificatesData result = service.create(certifiedCopiesSpec);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsSpec expectedOptions = certifiedCopiesSpec.getItemOptions().getFirst();

        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesSpec.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), captured.getDescription());
        assertEquals("certified-copy", captured.getDescriptionIdentifier());
        assertEquals(expectedOptions.getCertificateType(), capturedOptions.getCertificateType());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());
        assertEquals(expectedOptions.getCompanyType(), capturedOptions.getCompanyType());
        assertEquals(expectedOptions.getCompanyStatus(), capturedOptions.getCompanyStatus());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certifiedCopiesSpec.getKind(), captured.getKind());
        assertEquals("/orderable/certified-copies/CCD-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certifiedCopiesSpec.getQuantity(), captured.getQuantity());
        assertEquals(certifiedCopiesSpec.getUserId(), captured.getUserId());

        assertNotNull(captured.getBasket());
        Basket capturedBasket = captured.getBasket();

        assertEquals(basketSpec.getForename(), capturedBasket.getForename());  // Now should pass
        assertEquals(basketSpec.getSurname(), capturedBasket.getSurname());
        assertTrue(capturedBasket.isEnrolled());
    }

    @Test
    void createMultipleCertifiedCopiesFromMultipleItemOptions() throws DataException {
        when(randomService.getNumber(6))
            .thenReturn(699255L, 990509L, 582923L, 900231L);
        when(randomService.getEtag())
            .thenReturn("etag1", "etag2");

        CapitalSpec capitalSpec = new CapitalSpec();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesSpec descriptionValuesSpec = new FilingHistoryDescriptionValuesSpec();
        descriptionValuesSpec.setDate("2019-11-10");
        descriptionValuesSpec.setCapital(List.of(capitalSpec));

        FilingHistoryDocumentsSpec filingHistoryDocument1 = new FilingHistoryDocumentsSpec();
        filingHistoryDocument1.setFilingHistoryDate("2019-11-23");
        filingHistoryDocument1.setFilingHistoryDescription("capital-allotment-shares");
        filingHistoryDocument1.setFilingHistoryDescriptionValues(descriptionValuesSpec);
        filingHistoryDocument1.setFilingHistoryId("OTAwMzQ1NjM2M2FkaXF6a6N4");
        filingHistoryDocument1.setFilingHistoryType("SH01");
        filingHistoryDocument1.setFilingHistoryCost("15");

        ItemOptionsSpec itemOption1 = new ItemOptionsSpec();
        itemOption1.setDeliveryTimescale("postal");
        itemOption1.setDeliveryMethod("standard");
        itemOption1.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocument1));

        FilingHistoryDocumentsSpec filingHistoryDocument2 = new FilingHistoryDocumentsSpec();
        filingHistoryDocument1.setFilingHistoryDate("2019-11-23");
        filingHistoryDocument1.setFilingHistoryDescription("incorporation-company");
        filingHistoryDocument1.setFilingHistoryId("MzE0OTM3MTQxNmFkaXF6a2N4");
        filingHistoryDocument1.setFilingHistoryType("NEWINC");
        filingHistoryDocument1.setFilingHistoryCost("30");

        ItemOptionsSpec itemOption2 = new ItemOptionsSpec();
        itemOption1.setDeliveryTimescale("postal");
        itemOption1.setDeliveryMethod("standard");
        itemOption1.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocument1));

        certifiedCopiesSpec.setItemOptions(List.of(itemOption1, itemOption2));

        BasketSpec basketSpec = new BasketSpec();
        basketSpec.setForename("John");
        basketSpec.setSurname("Doe");
        basketSpec.setEnrolled(true);
        certifiedCopiesSpec.setBasketSpec(basketSpec);

        // Capture each certificate saved
        when(repository.save(any(CertifiedCopies.class))).thenAnswer(invocation -> {
            CertifiedCopies cert = invocation.getArgument(0);
            cert.setBasket(new Basket());
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(new Basket());

        CertificatesData results = service.create(certifiedCopiesSpec);

        assertEquals(2, results.getCertificates().size());

        verify(repository, times(2)).save(certifiedCopiesCaptor.capture());
        List<CertifiedCopies> capturedCertificates = certifiedCopiesCaptor.getAllValues();

        assertEquals(2, capturedCertificates.size());

        for (int i = 0; i < capturedCertificates.size(); i++) {
            CertifiedCopies cert = capturedCertificates.get(i);
            ItemOptionsSpec expectedOptions = certifiedCopiesSpec.getItemOptions().get(i);

            assertEquals(certifiedCopiesSpec.getCompanyName(), cert.getCompanyName());
            assertEquals(certifiedCopiesSpec.getCompanyNumber(), cert.getCompanyNumber());
            assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), cert.getDescription());
            assertEquals("certified-copy", cert.getDescriptionIdentifier());
            assertEquals(certifiedCopiesSpec.getCompanyNumber(), cert.getDescriptionCompanyNumber());
            assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), cert.getDescriptionCertifiedCopy());

            assertEquals(expectedOptions.getCertificateType(), cert.getItemOptions().getCertificateType());
            assertEquals(expectedOptions.getDeliveryTimescale(), cert.getItemOptions().getDeliveryTimescale());
            assertEquals(expectedOptions.getCompanyType(), cert.getItemOptions().getCompanyType());
            assertEquals(expectedOptions.getCompanyStatus(), cert.getItemOptions().getCompanyStatus());
            assertEquals(certifiedCopiesSpec.getKind(), cert.getKind());
            assertEquals(certifiedCopiesSpec.getQuantity(), cert.getQuantity());
            assertEquals(certifiedCopiesSpec.getUserId(), cert.getUserId());
        }
    }

    @Test
    void deleteCertificates() {
        when(repository.findById("CCD-123456-789012")).thenReturn(java.util.Optional.of(certifiedCopies));

        boolean result = service.delete("CCD-123456-789012");
        assertTrue(result);
        verify(repository).delete(certifiedCopies);
    }

    @Test
    void createCertificatesWithDefaultValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CertificatesData result = service.create(certifiedCopiesSpec);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesSpec.getCompanyNumber(), captured.getCompanyNumber());
    }

    @Test
    void deleteCertificatesBasketNotFound() {
        String certificateId = "CCD-123456-789012";

        when(repository.findById(certificateId)).thenReturn(java.util.Optional.empty());
        boolean result = service.delete(certificateId);

        assertFalse(result);  // Should return false when not found
        verify(repository, never()).delete(any(CertifiedCopies.class));
        verify(basketRepository, never()).delete(any(Basket.class));
    }

    @Test
    void deleteCertificatesBasket() {
        String certificateId = "CCD-123456-789012";
        certifiedCopies.setUserId("user123");
        basket.setId(certifiedCopies.getUserId());

        when(repository.findById(certificateId)).thenReturn(java.util.Optional.of(certifiedCopies));
        when(basketRepository.findById(certifiedCopies.getUserId())).thenReturn(java.util.Optional.of(basket));

        boolean result = service.delete(certificateId);

        assertTrue(result);

        verify(repository).delete(certifiedCopies);
        verify(basketRepository).delete(basket);
    }
}
