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
import uk.gov.companieshouse.api.testdata.model.rest.request.BasketRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CapitalRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.FilingHistoryDescriptionValuesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.FilingHistoryDocumentsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.ItemCostsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.ItemOptionsRequest;
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
    private CertifiedCopiesRequest certifiedCopiesRequest;

    private Basket basket;

    @BeforeEach
    void setUp() {
        certifiedCopies = new CertifiedCopies();
        certifiedCopies.setId("CCD-123456-789012");
        var companyNumber = "12345678";

        FilingHistoryDocumentsRequest filingHistoryDocument = getFilingHistoryDocumentsSpec();

        ItemOptionsRequest itemOptionsRequest = new ItemOptionsRequest();
        itemOptionsRequest.setDeliveryMethod("standard");
        itemOptionsRequest.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocument));

        ItemCostsRequest itemCostsRequest = new ItemCostsRequest();
        itemCostsRequest.setDiscountApplied("0");
        itemCostsRequest.setItemCost("15");
        itemCostsRequest.setCalculatedCost("15");
        itemCostsRequest.setProductType("certified-copy");

        certifiedCopiesRequest = new CertifiedCopiesRequest();
        certifiedCopiesRequest.setCompanyName("Test Company");
        certifiedCopiesRequest.setCompanyNumber(companyNumber);
        certifiedCopiesRequest.setItemCosts(List.of(itemCostsRequest));
        certifiedCopiesRequest.setItemOptions(List.of(itemOptionsRequest));
        certifiedCopiesRequest.setKind("certified-copy-kind");
        certifiedCopiesRequest.setPostalDelivery(true);
        certifiedCopiesRequest.setQuantity(1);
        certifiedCopiesRequest.setUserId("user123");
        certifiedCopiesRequest.setPostageCost("0");
        certifiedCopiesRequest.setTotalItemCost("30");

        basket = new Basket();
        basket.setId("user123");
    }

    private static FilingHistoryDocumentsRequest getFilingHistoryDocumentsSpec() {
        FilingHistoryDocumentsRequest filingHistoryDocument = new FilingHistoryDocumentsRequest();
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

        CertificatesResponse result = service.create(certifiedCopiesRequest);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesRequest.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesRequest.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesRequest.getCompanyNumber(), captured.getDescription());
        assertEquals("certified-copy", captured.getDescriptionIdentifier());
        assertEquals(certifiedCopiesRequest.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesRequest.getCompanyNumber(), captured.getDescriptionCertifiedCopy());
        assertItemCosts(captured.getItemCosts(), certifiedCopiesRequest.getItemCosts().getFirst());
        assertItemOptionsAndFilingHistory(captured.getItemOptions(), certifiedCopiesRequest.getItemOptions().getFirst());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certifiedCopiesRequest.getKind(), captured.getKind());
        assertEquals("/orderable/certified-copies/CCD-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certifiedCopiesRequest.getQuantity(), captured.getQuantity());
        assertEquals(certifiedCopiesRequest.getUserId(), captured.getUserId());
        assertEquals(certifiedCopiesRequest.getPostageCost(), captured.getPostageCost());
        assertEquals(certifiedCopiesRequest.getTotalItemCost(), captured.getTotalItemCost());
    }

    @Test
    void createCertifiedCopiesWithOptionalValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CapitalRequest capitalSpec = new CapitalRequest();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesRequest filingHistoryDescriptionValuesSpec = new FilingHistoryDescriptionValuesRequest();
        filingHistoryDescriptionValuesSpec.setCapital(List.of(capitalSpec));
        filingHistoryDescriptionValuesSpec.setChargeNumber("029231400009");
        filingHistoryDescriptionValuesSpec.setDate("2019-11-10");
        filingHistoryDescriptionValuesSpec.setMadeUpDate("2017-12-31");
        filingHistoryDescriptionValuesSpec.setOfficerName("Officer Test");

        FilingHistoryDocumentsRequest filingHistoryDocumentsSpec = new FilingHistoryDocumentsRequest();
        filingHistoryDocumentsSpec.setFilingHistoryDescriptionValues(filingHistoryDescriptionValuesSpec);

        ItemOptionsRequest itemOptionsRequest = new ItemOptionsRequest();
        itemOptionsRequest.setCollectionLocation("wales");
        itemOptionsRequest.setContactNumber("844740192");
        itemOptionsRequest.setDeliveryTimescale("same-day");
        itemOptionsRequest.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocumentsSpec));
        itemOptionsRequest.setForeName("John");
        itemOptionsRequest.setSurName("Test");

        certifiedCopiesRequest.setCustomerReference("CustomerReference");
        certifiedCopiesRequest.setItemOptions(List.of(itemOptionsRequest));

        CertificatesResponse result = service.create(certifiedCopiesRequest);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesRequest.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesRequest.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesRequest.getCompanyNumber(), captured.getDescription());
        assertEquals("certified-copy", captured.getDescriptionIdentifier());
        assertEquals(certifiedCopiesRequest.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesRequest.getCompanyNumber(), captured.getDescriptionCertifiedCopy());
        assertItemCosts(captured.getItemCosts(), certifiedCopiesRequest.getItemCosts().getFirst());
        assertItemOptionsAndFilingHistory(captured.getItemOptions(), certifiedCopiesRequest.getItemOptions().getFirst());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certifiedCopiesRequest.getKind(), captured.getKind());
        assertEquals("/orderable/certified-copies/CCD-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certifiedCopiesRequest.getQuantity(), captured.getQuantity());
        assertEquals(certifiedCopiesRequest.getUserId(), captured.getUserId());
        assertEquals(certifiedCopiesRequest.getPostageCost(), captured.getPostageCost());
        assertEquals(certifiedCopiesRequest.getTotalItemCost(), captured.getTotalItemCost());

        var expectedItemOptions = certifiedCopiesRequest.getItemOptions().getFirst();
        var capturedItemOptions = captured.getItemOptions();
        var expectedFilingHistoryDescriptionValues = expectedItemOptions.getFilingHistoryDocuments().getFirst().getFilingHistoryDescriptionValues();
        var capturedFilingHistoryDescriptionValues = capturedItemOptions.getFilingHistoryDocuments().getFirst().getFilingHistoryDescriptionValues();
        var expectedCapital = expectedFilingHistoryDescriptionValues.getCapital().getFirst();
        var capturedCapital = capturedFilingHistoryDescriptionValues.getCapital().getFirst();
        assertOptionalItems(expectedItemOptions, capturedItemOptions, expectedCapital, capturedCapital, expectedFilingHistoryDescriptionValues, capturedFilingHistoryDescriptionValues);

    }

    private void assertOptionalItems(ItemOptionsRequest expectedItemOptions, ItemOptions capturedItemOptions, CapitalRequest expectedCapital, Capital capturedCapital, FilingHistoryDescriptionValuesRequest expectedFilingHistoryDescriptionValues, FilingHistoryDescriptionValues capturedFilingHistoryDescriptionValues) {
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

    private void assertItemCosts(List<ItemCosts> capturedCosts, ItemCostsRequest expectedCosts) {
        assertEquals(expectedCosts.getDiscountApplied(), capturedCosts.getFirst().getDiscountApplied());
        assertEquals(expectedCosts.getItemCost(), capturedCosts.getFirst().getItemCost());
        assertEquals(expectedCosts.getCalculatedCost(), capturedCosts.getFirst().getCalculatedCost());
        assertEquals(expectedCosts.getProductType(), capturedCosts.getFirst().getProductType());
    }

    private void assertItemOptionsAndFilingHistory(ItemOptions capturedOptions, ItemOptionsRequest expectedOptions) {
        assertEquals(expectedOptions.getDeliveryMethod(), capturedOptions.getDeliveryMethod());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());

        List<FilingHistoryDocument> capturedFilingHistoryDocument = capturedOptions.getFilingHistoryDocuments();
        List<FilingHistoryDocumentsRequest> expectedFilingHistoryDocument = expectedOptions.getFilingHistoryDocuments();

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

        BasketRequest basketRequest = new BasketRequest();
        basketRequest.setForename("John");
        basketRequest.setSurname("Doe");
        basketRequest.setEnrolled(true);
        certifiedCopiesRequest.setBasketSpec(basketRequest);

        Basket basket = new Basket();
        basket.setForename(basketRequest.getForename());
        basket.setSurname(basketRequest.getSurname());
        basket.setEnrolled(true);

        when(repository.save(any(CertifiedCopies.class))).thenAnswer(invocation -> {
            CertifiedCopies cert = invocation.getArgument(0);
            cert.setBasket(basket);
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(certificatesService.createBasket(any(CertificatesRequest.class), anyList())).thenReturn(basket);

        CertificatesResponse result = service.create(certifiedCopiesRequest);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsRequest expectedOptions = certifiedCopiesRequest.getItemOptions().getFirst();

        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesRequest.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesRequest.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("certified copy for company " + certifiedCopiesRequest.getCompanyNumber(), captured.getDescription());
        assertEquals("certified-copy", captured.getDescriptionIdentifier());
        assertEquals("CCD-123456-789012", captured.getDataId());
        assertEquals(expectedOptions.getCertificateType(), capturedOptions.getCertificateType());
        assertEquals(expectedOptions.getDeliveryTimescale(), capturedOptions.getDeliveryTimescale());
        assertEquals(expectedOptions.getCompanyType(), capturedOptions.getCompanyType());
        assertEquals(expectedOptions.getCompanyStatus(), capturedOptions.getCompanyStatus());
        assertEquals("etag123", captured.getEtag());
        assertEquals(certifiedCopiesRequest.getKind(), captured.getKind());
        assertEquals("/orderable/certified-copies/CCD-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(certifiedCopiesRequest.getQuantity(), captured.getQuantity());
        assertEquals(certifiedCopiesRequest.getUserId(), captured.getUserId());

        assertNotNull(captured.getBasket());
        Basket capturedBasket = captured.getBasket();

        assertEquals(basketRequest.getForename(), capturedBasket.getForename());  // Now should pass
        assertEquals(basketRequest.getSurname(), capturedBasket.getSurname());
        assertTrue(capturedBasket.isEnrolled());
    }

    @Test
    void createMultipleCertifiedCopiesFromMultipleItemOptions() throws DataException {
        when(randomService.getNumber(6))
            .thenReturn(699255L, 990509L, 582923L, 900231L);
        when(randomService.getEtag())
            .thenReturn("etag1", "etag2");

        CapitalRequest capitalSpec = new CapitalRequest();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesRequest descriptionValuesSpec = new FilingHistoryDescriptionValuesRequest();
        descriptionValuesSpec.setDate("2019-11-10");
        descriptionValuesSpec.setCapital(List.of(capitalSpec));

        FilingHistoryDocumentsRequest filingHistoryDocument1 = getFilingHistoryDocumentsSpec();

        ItemOptionsRequest itemOption1 = new ItemOptionsRequest();
        itemOption1.setDeliveryTimescale("postal");
        itemOption1.setDeliveryMethod("standard");
        itemOption1.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocument1));

        FilingHistoryDocumentsRequest filingHistoryDocument2 = new FilingHistoryDocumentsRequest();
        filingHistoryDocument1.setFilingHistoryDate("2019-11-23");
        filingHistoryDocument1.setFilingHistoryDescription("incorporation-company");
        filingHistoryDocument1.setFilingHistoryId("MzE0OTM3MTQxNmFkaXF6a2N4");
        filingHistoryDocument1.setFilingHistoryType("NEWINC");
        filingHistoryDocument1.setFilingHistoryCost("30");

        ItemOptionsRequest itemOption2 = new ItemOptionsRequest();
        itemOption1.setDeliveryTimescale("postal");
        itemOption1.setDeliveryMethod("standard");
        itemOption1.setFilingHistoryDocumentsSpec(List.of(filingHistoryDocument1));

        certifiedCopiesRequest.setItemOptions(List.of(itemOption1, itemOption2));

        BasketRequest basketRequest = new BasketRequest();
        basketRequest.setForename("John");
        basketRequest.setSurname("Doe");
        basketRequest.setEnrolled(true);
        certifiedCopiesRequest.setBasketSpec(basketRequest);

        // Capture each certificate saved
        when(repository.save(any(CertifiedCopies.class))).thenAnswer(invocation -> {
            CertifiedCopies cert = invocation.getArgument(0);
            cert.setBasket(new Basket());
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(new Basket());
        when(certificatesService.createBasket(any(CertificatesRequest.class), anyList())).thenReturn(basket);

        CertificatesResponse results = service.create(certifiedCopiesRequest);

        assertEquals(2, results.getCertificates().size());

        verify(repository, times(2)).save(certifiedCopiesCaptor.capture());
        List<CertifiedCopies> capturedCertificates = certifiedCopiesCaptor.getAllValues();

        assertEquals(2, capturedCertificates.size());

        for (int i = 0; i < capturedCertificates.size(); i++) {
            CertifiedCopies cert = capturedCertificates.get(i);
            ItemOptionsRequest expectedOptions = certifiedCopiesRequest.getItemOptions().get(i);

            assertEquals(certifiedCopiesRequest.getCompanyName(), cert.getCompanyName());
            assertEquals(certifiedCopiesRequest.getCompanyNumber(), cert.getCompanyNumber());
            assertEquals("certified copy for company " + certifiedCopiesRequest.getCompanyNumber(), cert.getDescription());
            assertEquals("certified-copy", cert.getDescriptionIdentifier());
            assertEquals(certifiedCopiesRequest.getCompanyNumber(), cert.getDescriptionCompanyNumber());
            assertEquals("certified copy for company " + certifiedCopiesRequest.getCompanyNumber(), cert.getDescriptionCertifiedCopy());

            assertEquals(expectedOptions.getCertificateType(), cert.getItemOptions().getCertificateType());
            assertEquals(expectedOptions.getDeliveryTimescale(), cert.getItemOptions().getDeliveryTimescale());
            assertEquals(expectedOptions.getCompanyType(), cert.getItemOptions().getCompanyType());
            assertEquals(expectedOptions.getCompanyStatus(), cert.getItemOptions().getCompanyStatus());
            assertEquals(certifiedCopiesRequest.getKind(), cert.getKind());
            assertEquals(certifiedCopiesRequest.getQuantity(), cert.getQuantity());
            assertEquals(certifiedCopiesRequest.getUserId(), cert.getUserId());
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

        CertificatesResponse result = service.create(certifiedCopiesRequest);

        assertNotNull(result);
        assertEquals(certifiedCopies.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(certifiedCopiesCaptor.capture());
        CertifiedCopies captured = certifiedCopiesCaptor.getValue();

        assertEquals("CCD-123456-789012", captured.getId());
        assertEquals(certifiedCopiesRequest.getCompanyName(), captured.getCompanyName());
        assertEquals(certifiedCopiesRequest.getCompanyNumber(), captured.getCompanyNumber());
    }

    @Test
    void validateCaptialIsNotPresent() {
        FilingHistoryDescriptionValuesRequest spec = new FilingHistoryDescriptionValuesRequest();
        spec.setDate("2024-05-01");
        spec.setCapital(null);

        FilingHistoryDescriptionValues result = service.mapToEntity(spec);

        assertEquals("2024-05-01", result.getDate());
        assertNull(result.getCapital()); // No capital should be set
    }

    @Test
    void validateFilingHistoryDescriptionValuesIsNull() throws DataException {
        FilingHistoryDocumentsRequest docSpec = new FilingHistoryDocumentsRequest();
        docSpec.setFilingHistoryId("abc123");
        docSpec.setFilingHistoryDescription("desc");
        docSpec.setFilingHistoryDescriptionValues(null); // NULL branch
        docSpec.setFilingHistoryDate("2024-01-01");
        docSpec.setFilingHistoryType("type");
        docSpec.setFilingHistoryCost("1");

        ItemOptionsRequest itemOptionsRequest = new ItemOptionsRequest();
        itemOptionsRequest.setFilingHistoryDocumentsSpec(List.of(docSpec));

        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        spec.setItemOptions(List.of(itemOptionsRequest));
        spec.setCompanyName("Test Ltd");
        spec.setCompanyNumber("12345678");
        spec.setUserId("user123");
        spec.setKind("kind");
        spec.setQuantity(1);
        spec.setPostalDelivery(true);

        when(randomService.getNumber(6)).thenReturn(111111L, 222222L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CertificatesResponse result = service.create(spec);

        assertNotNull(result);
        assertFalse(result.getCertificates().isEmpty());
    }

    @Test
    void validateItemCostsIsNull() throws DataException {
        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        spec.setCompanyName("Null Cost Co");
        spec.setCompanyNumber("00011122");
        spec.setUserId("user123");
        spec.setItemOptions(List.of(new ItemOptionsRequest()));
        spec.setItemCosts(null); // 👈 This is the key path
        spec.setKind("certified-copy");
        spec.setQuantity(1);
        spec.setPostalDelivery(false);

        when(randomService.getNumber(6)).thenReturn(111111L, 222222L);
        when(randomService.getEtag()).thenReturn("etag-test");

        when(repository.save(any(CertifiedCopies.class))).thenReturn(certifiedCopies);

        CertificatesResponse result = service.create(spec);

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
