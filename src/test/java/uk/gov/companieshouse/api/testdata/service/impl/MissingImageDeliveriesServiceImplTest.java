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
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryDescriptionValues;
import uk.gov.companieshouse.api.testdata.model.entity.ItemCosts;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.entity.MissingImageDeliveries;
import uk.gov.companieshouse.api.testdata.model.rest.request.BasketRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CapitalRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.FilingHistoryDescriptionValuesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.ItemCostsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.ItemOptionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.MissingImageDeliveriesRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class MissingImageDeliveriesServiceImplTest {

    @Mock
    private MissingImageDeliveriesRepository repository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private MissingImageDeliveriesImpl service;

    @Mock
    private CertificatesServiceImpl certificatesService;

    @Captor
    private ArgumentCaptor<MissingImageDeliveries> missingImageDeliveriesCaptor;

    private MissingImageDeliveries missingImageDeliveries;
    private MissingImageDeliveriesRequest missingImageDeliveriesRequest;

    private Basket basket;

    @BeforeEach
    void setUp() {
        missingImageDeliveries = new MissingImageDeliveries();
        missingImageDeliveries.setId("MID-123456-789012");
        var companyNumber = "12345678";

        FilingHistoryDescriptionValuesRequest descriptionValuesSpec = new FilingHistoryDescriptionValuesRequest();
        descriptionValuesSpec.setMadeUpDate("2019-11-10");

        ItemOptionsRequest itemOptionsRequest = new ItemOptionsRequest();
        itemOptionsRequest.setFilingHistoryCategory("accounts");
        itemOptionsRequest.setFilingHistoryDate("2018-04-06");
        itemOptionsRequest.setFilingHistoryDescription("accounts-with-accounts-type-small");
        itemOptionsRequest.setFilingHistoryId("MzIwMTkzODk1NGFkaXF6a2N6");
        itemOptionsRequest.setFilingHistoryType("AA");
        itemOptionsRequest.setFilingHistoryDescriptionValues(descriptionValuesSpec);

        ItemCostsRequest itemCostsRequest = new ItemCostsRequest();
        itemCostsRequest.setDiscountApplied("0");
        itemCostsRequest.setItemCost("15");
        itemCostsRequest.setCalculatedCost("15");
        itemCostsRequest.setProductType("missing-image-delivery-accounts");

        missingImageDeliveriesRequest = new MissingImageDeliveriesRequest();
        missingImageDeliveriesRequest.setCompanyName("Test Company");
        missingImageDeliveriesRequest.setCompanyNumber(companyNumber);
        missingImageDeliveriesRequest.setItemCosts(List.of(itemCostsRequest));
        missingImageDeliveriesRequest.setItemOptions(List.of(itemOptionsRequest));
        missingImageDeliveriesRequest.setKind("certified-copy-kind");
        missingImageDeliveriesRequest.setPostalDelivery(true);
        missingImageDeliveriesRequest.setQuantity(1);
        missingImageDeliveriesRequest.setUserId("user123");
        missingImageDeliveriesRequest.setPostageCost("0");
        missingImageDeliveriesRequest.setTotalItemCost("30");

        basket = new Basket();
        basket.setId("user123");
    }

    @Test
    void createMissingImageDeliveriesWithMandatoryValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        CertificatesResponse result = service.create(missingImageDeliveriesRequest);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        assertMandatoryFields(missingImageDeliveriesRequest, captured);
    }

    @Test
    void createMissingImageDeliveriesWithOptionalValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        MissingImageDeliveriesRequest missingImageDeliveriesRequest = new MissingImageDeliveriesRequest();
        missingImageDeliveriesRequest.setCustomerReference("Test");

        CapitalRequest capitalSpec = new CapitalRequest();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesRequest descriptionValuesSpec = new FilingHistoryDescriptionValuesRequest();
        descriptionValuesSpec.setDate("2019-11-10");
        descriptionValuesSpec.setCapital(List.of(capitalSpec));
        descriptionValuesSpec.setOfficerName("John Test");
        descriptionValuesSpec.setChargeNumber("21321312");

        ItemOptionsRequest itemOptionsRequest = new ItemOptionsRequest();
        itemOptionsRequest.setFilingHistoryBarcode("L72QXI0Y");
        itemOptionsRequest.setFilingHistoryDescriptionValues(descriptionValuesSpec);

        missingImageDeliveriesRequest.setItemOptions(List.of(itemOptionsRequest));

        CertificatesResponse result = service.create(missingImageDeliveriesRequest);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsRequest expectedOptions = missingImageDeliveriesRequest.getItemOptions().getFirst();
        FilingHistoryDescriptionValues caturedFilingHistoryDescriptionValues = capturedOptions.getFilingHistoryDescriptionValues();
        FilingHistoryDescriptionValuesRequest expectedFilingHistoryDescriptionValues = expectedOptions.getFilingHistoryDescriptionValues();
        List<Capital> capturedCapital = caturedFilingHistoryDescriptionValues.getCapital();
        List<CapitalRequest> expectedCapital = expectedFilingHistoryDescriptionValues.getCapital();

        assertEquals(missingImageDeliveriesRequest.getCustomerReference(), captured.getCustomerReference());
        assertEquals(expectedOptions.getFilingHistoryBarcode(), capturedOptions.getFilingHistoryBarcode());
        assertEquals(expectedCapital.getFirst().getCurrency(), capturedCapital.getFirst().getCurrency());
        assertEquals(expectedCapital.getFirst().getFigure(), capturedCapital.getFirst().getFigure());
        assertEquals(expectedFilingHistoryDescriptionValues.getChargeNumber(), caturedFilingHistoryDescriptionValues.getChargeNumber());
        assertEquals(expectedFilingHistoryDescriptionValues.getDate(), caturedFilingHistoryDescriptionValues.getDate());
        assertEquals(expectedFilingHistoryDescriptionValues.getOfficerName(), caturedFilingHistoryDescriptionValues.getOfficerName());
    }

    @Test
    void createMissingImageDeliveriesWithBasket() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");

        BasketRequest basketRequest = new BasketRequest();
        basketRequest.setForename("John");
        basketRequest.setSurname("Doe");
        basketRequest.setEnrolled(true);
        missingImageDeliveriesRequest.setBasketSpec(basketRequest);

        Basket basket = new Basket();
        basket.setForename(basketRequest.getForename());
        basket.setSurname(basketRequest.getSurname());
        basket.setEnrolled(true);

        when(repository.save(any(MissingImageDeliveries.class))).thenAnswer(invocation -> {
            MissingImageDeliveries cert = invocation.getArgument(0);
            cert.setBasket(basket);
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(certificatesService.createBasket(any(CertificatesRequest.class), anyList())).thenReturn(basket);

        CertificatesResponse result = service.create(missingImageDeliveriesRequest);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        assertMandatoryFields(missingImageDeliveriesRequest, captured);

        assertNotNull(captured.getBasket());
        Basket capturedBasket = captured.getBasket();

        assertEquals(basketRequest.getForename(), capturedBasket.getForename());  // Now should pass
        assertEquals(basketRequest.getSurname(), capturedBasket.getSurname());
        assertTrue(capturedBasket.isEnrolled());
    }

    @Test
    void createMultipleMissingImageDeliveriesFromMultipleItemOptions() throws DataException {
        when(randomService.getNumber(6))
            .thenReturn(699255L, 990509L, 582923L, 900231L);
        when(randomService.getEtag())
            .thenReturn("etag1", "etag2");

        CapitalRequest capitalSpec = new CapitalRequest();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesRequest descriptionValuesSpec1 = new FilingHistoryDescriptionValuesRequest();
        descriptionValuesSpec1.setOfficerName("John test");

        ItemOptionsRequest itemOption1 = new ItemOptionsRequest();
        itemOption1.setFilingHistoryDate("2019-11-23");
        itemOption1.setFilingHistoryDescription("appoint-person-director-company-with-name");
        itemOption1.setFilingHistoryDescriptionValues(descriptionValuesSpec1);
        itemOption1.setFilingHistoryId("MzE0OTM3MTQxNmFkaXF6a2N4");
        itemOption1.setFilingHistoryType("AP01");
        itemOption1.setFilingHistoryCategory("officers");

        FilingHistoryDescriptionValuesRequest descriptionValuesSpec2 = new FilingHistoryDescriptionValuesRequest();
        descriptionValuesSpec2.setMadeUpDate("2017-12-31");

        ItemOptionsRequest itemOption2 = new ItemOptionsRequest();
        itemOption2.setFilingHistoryDate("2016-11-23");
        itemOption2.setFilingHistoryDescription("accounts-with-accounts-type-small");
        itemOption2.setFilingHistoryDescriptionValues(descriptionValuesSpec2);
        itemOption2.setFilingHistoryId("MzE0OTM3MTQxNmFkaXF6a2N4");
        itemOption2.setFilingHistoryType("AA");
        itemOption2.setFilingHistoryCategory("accounts");
        itemOption2.setFilingHistoryBarcode("L72QXI0Y");

        missingImageDeliveriesRequest.setItemOptions(List.of(itemOption1, itemOption2));

        BasketRequest basketRequest = new BasketRequest();
        basketRequest.setForename("John");
        basketRequest.setSurname("Doe");
        basketRequest.setEnrolled(true);
        missingImageDeliveriesRequest.setBasketSpec(basketRequest);

        // Capture each certificate saved
        when(repository.save(any(MissingImageDeliveries.class))).thenAnswer(invocation -> {
            MissingImageDeliveries cert = invocation.getArgument(0);
            cert.setBasket(new Basket());
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(new Basket());
        when(certificatesService.createBasket(any(CertificatesRequest.class), anyList())).thenReturn(basket);

        CertificatesResponse results = service.create(missingImageDeliveriesRequest);

        assertEquals(2, results.getCertificates().size());

        verify(repository, times(2)).save(missingImageDeliveriesCaptor.capture());
        List<MissingImageDeliveries> capturedCertificates = missingImageDeliveriesCaptor.getAllValues();

        assertEquals(2, capturedCertificates.size());

        for (int i = 0; i < capturedCertificates.size(); i++) {
            MissingImageDeliveries cert = capturedCertificates.get(i);
            ItemOptionsRequest expectedOptions = missingImageDeliveriesRequest.getItemOptions().get(i);

            assertEquals(missingImageDeliveriesRequest.getCompanyName(), cert.getCompanyName());
            assertEquals(missingImageDeliveriesRequest.getCompanyNumber(), cert.getCompanyNumber());
            assertEquals("missing image delivery for company " + missingImageDeliveriesRequest.getCompanyNumber(), cert.getDescription());
            assertEquals("missing-image-delivery", cert.getDescriptionIdentifier());
            assertEquals(missingImageDeliveriesRequest.getCompanyNumber(), cert.getDescriptionCompanyNumber());
            assertEquals("missing image delivery for company " + missingImageDeliveriesRequest.getCompanyNumber(), cert.getDescriptionMissingImageDelivery());

            assertEquals(expectedOptions.getCertificateType(), cert.getItemOptions().getCertificateType());
            assertEquals(expectedOptions.getCompanyType(), cert.getItemOptions().getCompanyType());
            assertEquals(expectedOptions.getCompanyStatus(), cert.getItemOptions().getCompanyStatus());
            assertEquals(missingImageDeliveriesRequest.getKind(), cert.getKind());
            assertEquals(missingImageDeliveriesRequest.getQuantity(), cert.getQuantity());
            assertEquals(missingImageDeliveriesRequest.getUserId(), cert.getUserId());
        }
    }

    @Test
    void deleteCertificates() {
        when(repository.findById("MID-123456-789012")).thenReturn(Optional.of(missingImageDeliveries));

        boolean result = service.delete("MID-123456-789012");
        assertTrue(result);
        verify(repository).delete(missingImageDeliveries);
    }

    @Test
    void createCertificatesWithDefaultValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        CertificatesResponse result = service.create(missingImageDeliveriesRequest);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        assertEquals("MID-123456-789012", captured.getId());
        assertEquals(missingImageDeliveriesRequest.getCompanyName(), captured.getCompanyName());
        assertEquals(missingImageDeliveriesRequest.getCompanyNumber(), captured.getCompanyNumber());
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

        ItemOptionsRequest itemOptionsRequest = new ItemOptionsRequest();
        itemOptionsRequest.setFilingHistoryId("abc123");
        itemOptionsRequest.setFilingHistoryDescription("desc");
        itemOptionsRequest.setFilingHistoryDescriptionValues(null); // NULL branch
        itemOptionsRequest.setFilingHistoryDate("2024-01-01");
        itemOptionsRequest.setFilingHistoryType("type");


        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
        spec.setItemOptions(List.of(itemOptionsRequest));
        spec.setCompanyName("Test Ltd");
        spec.setCompanyNumber("12345678");
        spec.setUserId("user123");
        spec.setKind("kind");
        spec.setQuantity(1);
        spec.setPostalDelivery(true);

        when(randomService.getNumber(6)).thenReturn(111111L, 222222L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        CertificatesResponse result = service.create(spec);

        assertNotNull(result);
        assertFalse(result.getCertificates().isEmpty());
    }

    @Test
    void validateItemCostsIsNull() throws DataException {
        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
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

        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        CertificatesResponse result = service.create(spec);

        assertNotNull(result);
        assertFalse(result.getCertificates().isEmpty());
    }

    @Test
    void deleteCertificatesBasketNotFound() {
        String certificateId = "MID-123456-789012";

        when(repository.findById(certificateId)).thenReturn(Optional.empty());
        boolean result = service.delete(certificateId);

        assertFalse(result);  // Should return false when not found
        verify(repository, never()).delete(any(MissingImageDeliveries.class));
        verify(basketRepository, never()).delete(any(Basket.class));
    }

    @Test
    void deleteCertificatesBasket() {
        String certificateId = "MID-123456-789012";
        missingImageDeliveries.setUserId("user123");
        basket.setId(missingImageDeliveries.getUserId());

        when(repository.findById(certificateId)).thenReturn(Optional.of(missingImageDeliveries));
        when(basketRepository.findById(missingImageDeliveries.getUserId())).thenReturn(Optional.of(basket));

        boolean result = service.delete(certificateId);

        assertTrue(result);

        verify(repository).delete(missingImageDeliveries);
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

    private void assertMandatoryFields(MissingImageDeliveriesRequest spec, MissingImageDeliveries captured) {
        assertEquals("MID-123456-789012", captured.getId());
        assertEquals(spec.getCompanyName(), captured.getCompanyName());
        assertEquals(spec.getCompanyNumber(), captured.getCompanyNumber());
        assertEquals("missing image delivery for company " + spec.getCompanyNumber(), captured.getDescription());
        assertEquals("missing-image-delivery", captured.getDescriptionIdentifier());
        assertEquals(spec.getCompanyNumber(), captured.getDescriptionCompanyNumber());
        assertEquals("missing image delivery for company " + spec.getCompanyNumber(), captured.getDescriptionMissingImageDelivery());
        assertItemCosts(captured.getItemCosts(), spec.getItemCosts().getFirst());
        assertItemOptionsAndFilingHistory(captured.getItemOptions(), spec.getItemOptions().getFirst());
        assertEquals("etag123", captured.getEtag());
        assertEquals(spec.getKind(), captured.getKind());
        assertEquals("/orderable/missing-image-deliveries/MID-123456-789012", captured.getLinksSelf());
        assertTrue(captured.isPostalDelivery());
        assertEquals(spec.getQuantity(), captured.getQuantity());
        assertEquals(spec.getUserId(), captured.getUserId());
        assertEquals(spec.getPostageCost(), captured.getPostageCost());
        assertEquals(spec.getTotalItemCost(), captured.getTotalItemCost());
    }

    private void assertItemCosts(List<ItemCosts> capturedCosts, ItemCostsRequest expectedCosts) {
        assertEquals(expectedCosts.getDiscountApplied(), capturedCosts.getFirst().getDiscountApplied());
        assertEquals(expectedCosts.getItemCost(), capturedCosts.getFirst().getItemCost());
        assertEquals(expectedCosts.getCalculatedCost(), capturedCosts.getFirst().getCalculatedCost());
        assertEquals(expectedCosts.getProductType(), capturedCosts.getFirst().getProductType());
    }

    private void assertItemOptionsAndFilingHistory(ItemOptions capturedOptions, ItemOptionsRequest expectedOptions) {

        assertEquals(expectedOptions.getFilingHistoryCategory(), capturedOptions.getFilingHistoryCategory());
        assertEquals(expectedOptions.getFilingHistoryDate(), capturedOptions.getFilingHistoryDate());
        assertEquals(expectedOptions.getFilingHistoryDescription(), capturedOptions.getFilingHistoryDescription());
        assertEquals(expectedOptions.getFilingHistoryId(), capturedOptions.getFilingHistoryId());
        assertEquals(expectedOptions.getFilingHistoryType(), capturedOptions.getFilingHistoryType());
        assertEquals(expectedOptions.getFilingHistoryDescriptionValues().getMadeUpDate(), capturedOptions.getFilingHistoryDescriptionValues().getMadeUpDate());
    }
}
