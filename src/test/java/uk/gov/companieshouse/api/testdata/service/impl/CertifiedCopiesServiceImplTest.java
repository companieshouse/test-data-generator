package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.companieshouse.api.testdata.model.entity.Capital;
import uk.gov.companieshouse.api.testdata.model.entity.CertifiedCopies;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryDescriptionValues;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryDocument;
import uk.gov.companieshouse.api.testdata.model.entity.ItemCosts;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.BasketSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CapitalSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
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

    @Mock
    private CertificatesServiceImpl certificatesService;

    @Captor
    private ArgumentCaptor<CertifiedCopies> certifiedCopiesCaptor;

    @Captor
    private ArgumentCaptor<Basket> basketCaptor;

    private CertifiedCopies certifiedCopies;
    private CertifiedCopiesSpec certifiedCopiesSpec;

    private Basket basket;

    @BeforeEach
    void setUp() {
        certifiedCopies = new CertifiedCopies();
        certifiedCopies.setId("CCD-123456-789012");
        var companyNumber = "12345678";

        FilingHistoryDocumentsSpec filingHistoryDocument = getFilingHistoryDocumentsSpec();

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
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
        certifiedCopiesSpec.setPostageCost("0");
        certifiedCopiesSpec.setTotalItemCost("30");

        basket = new Basket();
        basket.setId("user123");
    }

    private static FilingHistoryDocumentsSpec getFilingHistoryDocumentsSpec() {
        FilingHistoryDocumentsSpec filingHistoryDocument = new FilingHistoryDocumentsSpec();
        filingHistoryDocument.setFilingHistoryDate("2019-11-23");
        filingHistoryDocument.setFilingHistoryDescription("capital-allotment-shares");
        filingHistoryDocument.setFilingHistoryId("OTAwMzQ1NjM2M2FkaXF6a6N4");
        filingHistoryDocument.setFilingHistoryType("SH01");
        filingHistoryDocument.setFilingHistoryCost("15");
        return filingHistoryDocument;
    }

    @Test
    void createCertifiedCopiesWithMandatoryValues() throws DataException {
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
        assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), captured.getDescription());
        assertEquals("certified-copy", captured.getDescriptionIdentifier());
        assertEquals(certifiedCopiesSpec.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), captured.getDescriptionCertifiedCopy());
        assertItemCosts(captured.getItemCosts(), certifiedCopiesSpec.getItemCosts().getFirst());
        assertItemOptionsAndFilingHistory(captured.getItemOptions(), certifiedCopiesSpec.getItemOptions().getFirst());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certifiedCopiesSpec.getKind(), captured.getKind());
        assertEquals("/orderable/certified-copies/CCD-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certifiedCopiesSpec.getQuantity(), captured.getQuantity());
        assertEquals(certifiedCopiesSpec.getUserId(), captured.getUserId());
        assertEquals(certifiedCopiesSpec.getPostageCost(), captured.getPostageCost());
        assertEquals(certifiedCopiesSpec.getTotalItemCost(), captured.getTotalItemCost());
    }

    @Test
    void createCertifiedCopiesWithOptionalValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CapitalSpec capitalSpec = new CapitalSpec();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesSpec filingHistoryDescriptionValuesSpec = new FilingHistoryDescriptionValuesSpec();
        filingHistoryDescriptionValuesSpec.setCapital(List.of(capitalSpec));
        filingHistoryDescriptionValuesSpec.setChargeNumber("029231400009");
        filingHistoryDescriptionValuesSpec.setDate("2019-11-10");
        filingHistoryDescriptionValuesSpec.setMadeUpDate("2017-12-31");
        filingHistoryDescriptionValuesSpec.setOfficerName("Officer Test");

        FilingHistoryDocumentsSpec filingHistoryDocumentsSpec = new FilingHistoryDocumentsSpec();
        filingHistoryDocumentsSpec.setFilingHistoryDescriptionValues(filingHistoryDescriptionValuesSpec);

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setCollectionLocation("wales");
        itemOptionsSpec.setContactNumber("844740192");
        itemOptionsSpec.setDeliveryTimescale("same-day");
        itemOptionsSpec.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocumentsSpec));
        itemOptionsSpec.setForeName("John");
        itemOptionsSpec.setSurName("Test");

        certifiedCopiesSpec.setCustomerReference("CustomerReference");
        certifiedCopiesSpec.setItemOptions(List.of(itemOptionsSpec));

        CertificatesData result = service.create(certifiedCopiesSpec);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesSpec.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), captured.getDescription());
        assertEquals("certified-copy", captured.getDescriptionIdentifier());
        assertEquals(certifiedCopiesSpec.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesSpec.getCompanyNumber(), captured.getDescriptionCertifiedCopy());
        assertItemCosts(captured.getItemCosts(), certifiedCopiesSpec.getItemCosts().getFirst());
        assertItemOptionsAndFilingHistory(captured.getItemOptions(), certifiedCopiesSpec.getItemOptions().getFirst());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certifiedCopiesSpec.getKind(), captured.getKind());
        assertEquals("/orderable/certified-copies/CCD-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certifiedCopiesSpec.getQuantity(), captured.getQuantity());
        assertEquals(certifiedCopiesSpec.getUserId(), captured.getUserId());
        assertEquals(certifiedCopiesSpec.getPostageCost(), captured.getPostageCost());
        assertEquals(certifiedCopiesSpec.getTotalItemCost(), captured.getTotalItemCost());

        var expectedItemOptions = certifiedCopiesSpec.getItemOptions().getFirst();
        var capturedItemOptions = captured.getItemOptions();
        var expectedFilingHistoryDescriptionValues = expectedItemOptions.getFilingHistoryDocuments().getFirst().getFilingHistoryDescriptionValues();
        var capturedFilingHistoryDescriptionValues = capturedItemOptions.getFilingHistoryDocuments().getFirst().getFilingHistoryDescriptionValues();
        var expectedCapital = expectedFilingHistoryDescriptionValues.getCapital().getFirst();
        var capturedCapital = expectedFilingHistoryDescriptionValues.getCapital().getFirst();

        assertEquals(expectedItemOptions.getCollectionLocation(), capturedItemOptions.getCollectionLocation());
        assertEquals(expectedItemOptions.getContactNumber(), capturedItemOptions.getContactNumber());
        assertEquals(expectedItemOptions.getDeliveryTimescale(), capturedItemOptions.getDeliveryTimescale());
        assertEquals(expectedCapital.getFigure(), capturedCapital.getFigure());
        assertEquals(expectedCapital.getCurrency(), capturedCapital.getCurrency());
        assertEquals(expectedFilingHistoryDescriptionValues.getChargeNumber(), capturedFilingHistoryDescriptionValues.getChargeNumber());
        assertEquals(expectedFilingHistoryDescriptionValues.getDate(), capturedFilingHistoryDescriptionValues.getDate());
        assertEquals(expectedFilingHistoryDescriptionValues.getMadeUpDate(), capturedFilingHistoryDescriptionValues.getMadeUpDate());
        assertEquals(expectedFilingHistoryDescriptionValues.getOfficerName(), capturedFilingHistoryDescriptionValues.getOfficerName());
        assertEquals(expectedItemOptions.getForeName(), capturedItemOptions.getForeName());
        assertEquals(expectedItemOptions.getSurName(), capturedItemOptions.getSurName());
    }

    private void assertItemCosts(List<ItemCosts> capturedCosts, ItemCostsSpec expectedCosts) {
        assertEquals(expectedCosts.getDiscountApplied(), capturedCosts.getFirst().getDiscountApplied());
        assertEquals(expectedCosts.getItemCost(), capturedCosts.getFirst().getItemCost());
        assertEquals(expectedCosts.getCalculatedCost(), capturedCosts.getFirst().getCalculatedCost());
        assertEquals(expectedCosts.getProductType(), capturedCosts.getFirst().getProductType());
    }

    private void assertItemOptionsAndFilingHistory(ItemOptions capturedOptions, ItemOptionsSpec expectedOptions) {
        assertEquals(expectedOptions.getDeliveryMethod(), capturedOptions.getDeliveryMethod());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());

        List<FilingHistoryDocument> capturedFilingHistoryDocument = capturedOptions.getFilingHistoryDocuments();
        List<FilingHistoryDocumentsSpec> expectedFilingHistoryDocument = expectedOptions.getFilingHistoryDocuments();

        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryDate(), capturedFilingHistoryDocument.getFirst().getFilingHistoryDate());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryDescription(), capturedFilingHistoryDocument.getFirst().getFilingHistoryDescription());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryId(), capturedFilingHistoryDocument.getFirst().getFilingHistoryId());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryType(), capturedFilingHistoryDocument.getFirst().getFilingHistoryType());
        assertEquals(expectedFilingHistoryDocument.getFirst().getFilingHistoryCost(), capturedFilingHistoryDocument.getFirst().getFilingHistoryCost());
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
        when(certificatesService.createBasket(any(CertificatesSpec.class), anyList())).thenReturn(basket);

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
        assertEquals("CCD-123456-789012", captured.getDataId());
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

        FilingHistoryDocumentsSpec filingHistoryDocument1 = getFilingHistoryDocumentsSpec();

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
        when(certificatesService.createBasket(any(CertificatesSpec.class), anyList())).thenReturn(basket);

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
    void validateCaptialIsNotPresent() {
        FilingHistoryDescriptionValuesSpec spec = new FilingHistoryDescriptionValuesSpec();
        spec.setDate("2024-05-01");
        spec.setCapital(null);

        FilingHistoryDescriptionValues result = service.mapToEntity(spec);

        assertEquals("2024-05-01", result.getDate());
        assertNull(result.getCapital()); // No capital should be set
    }

    @Test
    void validateFilingHistoryDescriptionValuesIsNull() throws DataException {
        FilingHistoryDocumentsSpec docSpec = new FilingHistoryDocumentsSpec();
        docSpec.setFilingHistoryId("abc123");
        docSpec.setFilingHistoryDescription("desc");
        docSpec.setFilingHistoryDescriptionValues(null); // NULL branch
        docSpec.setFilingHistoryDate("2024-01-01");
        docSpec.setFilingHistoryType("type");
        docSpec.setFilingHistoryCost("1");

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setFilingHistoryDocumentsSpec(List.of(docSpec));

        CertifiedCopiesSpec spec = new CertifiedCopiesSpec();
        spec.setItemOptions(List.of(itemOptionsSpec));
        spec.setCompanyName("Test Ltd");
        spec.setCompanyNumber("12345678");
        spec.setUserId("user123");
        spec.setKind("kind");
        spec.setQuantity(1);
        spec.setPostalDelivery(true);

        when(randomService.getNumber(6)).thenReturn(111111L, 222222L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CertificatesData result = service.create(spec);

        assertNotNull(result);
        assertFalse(result.getCertificates().isEmpty());
    }

    @Test
    void validateItemCostsIsNull() throws DataException {
        CertifiedCopiesSpec spec = new CertifiedCopiesSpec();
        spec.setCompanyName("Null Cost Co");
        spec.setCompanyNumber("00011122");
        spec.setUserId("user123");
        spec.setItemOptions(List.of(new ItemOptionsSpec()));
        spec.setItemCosts(null); // 👈 This is the key path
        spec.setKind("certified-copy");
        spec.setQuantity(1);
        spec.setPostalDelivery(false);

        when(randomService.getNumber(6)).thenReturn(111111L, 222222L);
        when(randomService.getEtag()).thenReturn("etag-test");

        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CertificatesData result = service.create(spec);

        assertNotNull(result);
        assertFalse(result.getCertificates().isEmpty());
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

    @Test
    void validateBasketNotDeletedWhenNull() {
        String basketId = "user123";
        Basket basket = new Basket();
        basket.setId(null);

        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));

        service.deleteBasket(basketId);

        verify(basketRepository, never()).delete(any());
    }
}
