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
import uk.gov.companieshouse.api.testdata.model.rest.BasketSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CapitalSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDescriptionValuesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemCostsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.MissingImageDeliveriesSpec;
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
    private MissingImageDeliveriesSpec missingImageDeliveriesSpec;

    private Basket basket;

    @BeforeEach
    void setUp() {
        missingImageDeliveries = new MissingImageDeliveries();
        missingImageDeliveries.setId("MID-123456-789012");
        var companyNumber = "12345678";

        FilingHistoryDescriptionValuesSpec descriptionValuesSpec = new FilingHistoryDescriptionValuesSpec();
        descriptionValuesSpec.setMadeUpDate("2019-11-10");

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setFilingHistoryCategory("accounts");
        itemOptionsSpec.setFilingHistoryDate("2018-04-06");
        itemOptionsSpec.setFilingHistoryDescription("accounts-with-accounts-type-small");
        itemOptionsSpec.setFilingHistoryId("MzIwMTkzODk1NGFkaXF6a2N6");
        itemOptionsSpec.setFilingHistoryType("AA");
        itemOptionsSpec.setFilingHistoryDescriptionValues(descriptionValuesSpec);

        ItemCostsSpec itemCostsSpec = new ItemCostsSpec();
        itemCostsSpec.setDiscountApplied("0");
        itemCostsSpec.setItemCost("15");
        itemCostsSpec.setCalculatedCost("15");
        itemCostsSpec.setProductType("missing-image-delivery-accounts");

        missingImageDeliveriesSpec = new MissingImageDeliveriesSpec();
        missingImageDeliveriesSpec.setCompanyName("Test Company");
        missingImageDeliveriesSpec.setCompanyNumber(companyNumber);
        missingImageDeliveriesSpec.setItemCosts(List.of(itemCostsSpec));
        missingImageDeliveriesSpec.setItemOptions(List.of(itemOptionsSpec));
        missingImageDeliveriesSpec.setKind("certified-copy-kind");
        missingImageDeliveriesSpec.setPostalDelivery(true);
        missingImageDeliveriesSpec.setQuantity(1);
        missingImageDeliveriesSpec.setUserId("user123");
        missingImageDeliveriesSpec.setPostageCost("0");
        missingImageDeliveriesSpec.setTotalItemCost("30");

        basket = new Basket();
        basket.setId("user123");
    }

    @Test
    void createMissingImageDeliveriesWithMandatoryValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        CertificatesData result = service.create(missingImageDeliveriesSpec);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        assertMandatoryFields(missingImageDeliveriesSpec, captured);
    }

    @Test
    void createMissingImageDeliveriesWithOptionalValues() throws DataException {
        when(randomService.getNumber(6)).thenReturn(123456L, 789012L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        missingImageDeliveriesSpec.setCustomerReference("Test");

        CapitalSpec capitalSpec = new CapitalSpec();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesSpec descriptionValuesSpec = new FilingHistoryDescriptionValuesSpec();
        descriptionValuesSpec.setDate("2019-11-10");
        descriptionValuesSpec.setCapital(List.of(capitalSpec));
        descriptionValuesSpec.setOfficerName("John Test");
        descriptionValuesSpec.setChargeNumber("21321312");

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setFilingHistoryBarcode("L72QXI0Y");
        itemOptionsSpec.setFilingHistoryDescriptionValues(descriptionValuesSpec);

        missingImageDeliveriesSpec.setItemOptions(List.of(itemOptionsSpec));

        CertificatesData result = service.create(missingImageDeliveriesSpec);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        ItemOptions capturedOptions = captured.getItemOptions();
        ItemOptionsSpec expectedOptions = missingImageDeliveriesSpec.getItemOptions().getFirst();
        FilingHistoryDescriptionValues caturedFilingHistoryDescriptionValues = capturedOptions.getFilingHistoryDescriptionValues();
        FilingHistoryDescriptionValuesSpec expectedFilingHistoryDescriptionValues = expectedOptions.getFilingHistoryDescriptionValues();
        List<Capital> capturedCapital = caturedFilingHistoryDescriptionValues.getCapital();
        List<CapitalSpec> expectedCapital = expectedFilingHistoryDescriptionValues.getCapital();

        assertEquals(missingImageDeliveriesSpec.getCustomerReference(), captured.getCustomerReference());
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

        BasketSpec basketSpec = new BasketSpec();
        basketSpec.setForename("John");
        basketSpec.setSurname("Doe");
        basketSpec.setEnrolled(true);
        missingImageDeliveriesSpec.setBasketSpec(basketSpec);

        basket = new Basket();
        basket.setForename(basketSpec.getForename());
        basket.setSurname(basketSpec.getSurname());
        basket.setEnrolled(true);

        when(repository.save(any(MissingImageDeliveries.class))).thenAnswer(invocation -> {
            MissingImageDeliveries cert = invocation.getArgument(0);
            cert.setBasket(basket);
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(basket);
        when(certificatesService.createBasket(any(CertificatesSpec.class), anyList())).thenReturn(basket);

        CertificatesData result = service.create(missingImageDeliveriesSpec);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        assertMandatoryFields(missingImageDeliveriesSpec, captured);

        assertNotNull(captured.getBasket());
        Basket capturedBasket = captured.getBasket();

        assertEquals(basketSpec.getForename(), capturedBasket.getForename());  // Now should pass
        assertEquals(basketSpec.getSurname(), capturedBasket.getSurname());
        assertTrue(capturedBasket.isEnrolled());
    }

    @Test
    void createMultipleMissingImageDeliveriesFromMultipleItemOptions() throws DataException {
        when(randomService.getNumber(6))
            .thenReturn(699255L, 990509L, 582923L, 900231L);
        when(randomService.getEtag())
            .thenReturn("etag1", "etag2");

        CapitalSpec capitalSpec = new CapitalSpec();
        capitalSpec.setFigure("34,253,377");
        capitalSpec.setCurrency("GBP");

        FilingHistoryDescriptionValuesSpec descriptionValuesSpec1 = new FilingHistoryDescriptionValuesSpec();
        descriptionValuesSpec1.setOfficerName("John test");

        ItemOptionsSpec itemOption1 = new ItemOptionsSpec();
        itemOption1.setFilingHistoryDate("2019-11-23");
        itemOption1.setFilingHistoryDescription("appoint-person-director-company-with-name");
        itemOption1.setFilingHistoryDescriptionValues(descriptionValuesSpec1);
        itemOption1.setFilingHistoryId("MzE0OTM3MTQxNmFkaXF6a2N4");
        itemOption1.setFilingHistoryType("AP01");
        itemOption1.setFilingHistoryCategory("officers");

        FilingHistoryDescriptionValuesSpec descriptionValuesSpec2 = new FilingHistoryDescriptionValuesSpec();
        descriptionValuesSpec2.setMadeUpDate("2017-12-31");

        ItemOptionsSpec itemOption2 = new ItemOptionsSpec();
        itemOption2.setFilingHistoryDate("2016-11-23");
        itemOption2.setFilingHistoryDescription("accounts-with-accounts-type-small");
        itemOption2.setFilingHistoryDescriptionValues(descriptionValuesSpec2);
        itemOption2.setFilingHistoryId("MzE0OTM3MTQxNmFkaXF6a2N4");
        itemOption2.setFilingHistoryType("AA");
        itemOption2.setFilingHistoryCategory("accounts");
        itemOption2.setFilingHistoryBarcode("L72QXI0Y");

        missingImageDeliveriesSpec.setItemOptions(List.of(itemOption1, itemOption2));

        BasketSpec basketSpec = new BasketSpec();
        basketSpec.setForename("John");
        basketSpec.setSurname("Doe");
        basketSpec.setEnrolled(true);
        missingImageDeliveriesSpec.setBasketSpec(basketSpec);

        // Capture each certificate saved
        when(repository.save(any(MissingImageDeliveries.class))).thenAnswer(invocation -> {
            MissingImageDeliveries cert = invocation.getArgument(0);
            cert.setBasket(new Basket());
            return cert;
        });

        when(basketRepository.save(any(Basket.class))).thenReturn(new Basket());
        when(certificatesService.createBasket(any(CertificatesSpec.class), anyList())).thenReturn(basket);

        CertificatesData results = service.create(missingImageDeliveriesSpec);

        assertEquals(2, results.getCertificates().size());

        verify(repository, times(2)).save(missingImageDeliveriesCaptor.capture());
        List<MissingImageDeliveries> capturedCertificates = missingImageDeliveriesCaptor.getAllValues();

        assertEquals(2, capturedCertificates.size());

        for (int i = 0; i < capturedCertificates.size(); i++) {
            MissingImageDeliveries cert = capturedCertificates.get(i);
            ItemOptionsSpec expectedOptions = missingImageDeliveriesSpec.getItemOptions().get(i);

            assertEquals(missingImageDeliveriesSpec.getCompanyName(), cert.getCompanyName());
            assertEquals(missingImageDeliveriesSpec.getCompanyNumber(), cert.getCompanyNumber());
            assertEquals("missing image delivery for company " + missingImageDeliveriesSpec.getCompanyNumber(), cert.getDescription());
            assertEquals("missing-image-delivery", cert.getDescriptionIdentifier());
            assertEquals(missingImageDeliveriesSpec.getCompanyNumber(), cert.getDescriptionCompanyNumber());
            assertEquals("missing image delivery for company " + missingImageDeliveriesSpec.getCompanyNumber(), cert.getDescriptionMissingImageDelivery());

            assertEquals(expectedOptions.getCertificateType(), cert.getItemOptions().getCertificateType());
            assertEquals(expectedOptions.getCompanyType(), cert.getItemOptions().getCompanyType());
            assertEquals(expectedOptions.getCompanyStatus(), cert.getItemOptions().getCompanyStatus());
            assertEquals(missingImageDeliveriesSpec.getKind(), cert.getKind());
            assertEquals(missingImageDeliveriesSpec.getQuantity(), cert.getQuantity());
            assertEquals(missingImageDeliveriesSpec.getUserId(), cert.getUserId());
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

        CertificatesData result = service.create(missingImageDeliveriesSpec);

        assertNotNull(result);
        assertEquals(missingImageDeliveries.getId(), result.getCertificates().getFirst().getId());

        verify(repository).save(missingImageDeliveriesCaptor.capture());
        MissingImageDeliveries captured = missingImageDeliveriesCaptor.getValue();

        assertEquals("MID-123456-789012", captured.getId());
        assertEquals(missingImageDeliveriesSpec.getCompanyName(), captured.getCompanyName());
        assertEquals(missingImageDeliveriesSpec.getCompanyNumber(), captured.getCompanyNumber());
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

        ItemOptionsSpec itemOptionsSpec = new ItemOptionsSpec();
        itemOptionsSpec.setFilingHistoryId("abc123");
        itemOptionsSpec.setFilingHistoryDescription("desc");
        itemOptionsSpec.setFilingHistoryDescriptionValues(null); // NULL branch
        itemOptionsSpec.setFilingHistoryDate("2024-01-01");
        itemOptionsSpec.setFilingHistoryType("type");


        MissingImageDeliveriesSpec spec = new MissingImageDeliveriesSpec();
        spec.setItemOptions(List.of(itemOptionsSpec));
        spec.setCompanyName("Test Ltd");
        spec.setCompanyNumber("12345678");
        spec.setUserId("user123");
        spec.setKind("kind");
        spec.setQuantity(1);
        spec.setPostalDelivery(true);

        when(randomService.getNumber(6)).thenReturn(111111L, 222222L);
        when(randomService.getEtag()).thenReturn("etag123");
        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        CertificatesData result = service.create(spec);

        assertNotNull(result);
        assertFalse(result.getCertificates().isEmpty());
    }

    @Test
    void validateItemCostsIsNull() throws DataException {
        MissingImageDeliveriesSpec spec = new MissingImageDeliveriesSpec();
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

        when(repository.save(any(MissingImageDeliveries.class))).thenReturn(missingImageDeliveries);

        CertificatesData result = service.create(spec);

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
        basket.setId(null);

        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));

        service.deleteBasket(basketId);

        verify(basketRepository, never()).delete(any());
    }

    private void assertMandatoryFields(MissingImageDeliveriesSpec spec, MissingImageDeliveries captured) {
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

    private void assertItemCosts(List<ItemCosts> capturedCosts, ItemCostsSpec expectedCosts) {
        assertEquals(expectedCosts.getDiscountApplied(), capturedCosts.getFirst().getDiscountApplied());
        assertEquals(expectedCosts.getItemCost(), capturedCosts.getFirst().getItemCost());
        assertEquals(expectedCosts.getCalculatedCost(), capturedCosts.getFirst().getCalculatedCost());
        assertEquals(expectedCosts.getProductType(), capturedCosts.getFirst().getProductType());
    }

    private void assertItemOptionsAndFilingHistory(ItemOptions capturedOptions, ItemOptionsSpec expectedOptions) {

        assertEquals(expectedOptions.getFilingHistoryCategory(), capturedOptions.getFilingHistoryCategory());
        assertEquals(expectedOptions.getFilingHistoryDate(), capturedOptions.getFilingHistoryDate());
        assertEquals(expectedOptions.getFilingHistoryDescription(), capturedOptions.getFilingHistoryDescription());
        assertEquals(expectedOptions.getFilingHistoryId(), capturedOptions.getFilingHistoryId());
        assertEquals(expectedOptions.getFilingHistoryType(), capturedOptions.getFilingHistoryType());
        assertEquals(expectedOptions.getFilingHistoryDescriptionValues().getMadeUpDate(), capturedOptions.getFilingHistoryDescriptionValues().getMadeUpDate());
    }
}
