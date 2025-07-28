package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Basket;
import uk.gov.companieshouse.api.testdata.model.entity.Capital;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryDescriptionValues;
import uk.gov.companieshouse.api.testdata.model.entity.ItemCosts;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.entity.MissingImageDeliveries;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDescriptionValuesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemCostsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.MissingImageDeliveriesSpec;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.MissingImageDeliveriesRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class MissingImageDeliveriesImpl implements DataService<CertificatesData, MissingImageDeliveriesSpec> {

    @Autowired
    public MissingImageDeliveriesRepository missingImageDeliveriesRepository;

    @Autowired
    public BasketRepository basketRepository;

    @Autowired
    public AddressService addressService;

    @Autowired
    public RandomService randomService;

    @Autowired
    private CertificatesServiceImpl certificatesService;

    @Override
    public CertificatesData create(MissingImageDeliveriesSpec spec) throws DataException {
        List<ItemOptionsSpec> optionsList = spec.getItemOptions();
        List<CertificatesData.CertificateEntry> certificateEntries = new ArrayList<>(optionsList.size());
        List<Basket.Item> basketItems = new ArrayList<>(optionsList.size());

        for (ItemOptionsSpec optionSpec : optionsList) {
            Long firstPart = randomService.getNumber(6);
            Long secondPart = randomService.getNumber(6);
            var randomId = "MID-" + firstPart + "-" + secondPart;
            var missingImageDeliveries = getMissingImageDeliveries(spec, optionSpec, randomId);
            missingImageDeliveriesRepository.save(missingImageDeliveries);
            var now = getCurrentDateTime().toString();
            certificateEntries.add(new CertificatesData.CertificateEntry(
                missingImageDeliveries.getId(), now, now
            ));

            // Add to basket items
            var item = new Basket.Item();
            item.setItemUri(missingImageDeliveries.getLinksSelf());
            basketItems.add(item);
        }

        if (spec.getBasketSpec() != null) {
            var basket = createBasket(spec, basketItems);
            basketRepository.save(basket);
        }

        return new CertificatesData(certificateEntries);
    }

    protected FilingHistoryDescriptionValues mapToEntity(FilingHistoryDescriptionValuesSpec spec) {
        var filingHistoryDescriptionValues = new FilingHistoryDescriptionValues();
        Optional.ofNullable(spec.getDate()).ifPresent(filingHistoryDescriptionValues::setDate);
        Optional.ofNullable(spec.getChargeNumber()).ifPresent(filingHistoryDescriptionValues::setChargeNumber);
        Optional.ofNullable(spec.getMadeUpDate()).ifPresent(filingHistoryDescriptionValues::setMadeUpDate);
        Optional.ofNullable(spec.getOfficerName()).ifPresent(filingHistoryDescriptionValues::setOfficerName);

        if (spec.getCapital() != null) {
            List<Capital> capitalList = spec.getCapital().stream()
                .map(c -> {
                    var capital = new Capital();
                    capital.setCurrency(c.getCurrency());
                    capital.setFigure(c.getFigure());
                    return capital;
                })
                .collect(Collectors.toList());
            filingHistoryDescriptionValues.setCapital(capitalList);
        }

        return filingHistoryDescriptionValues;
    }

    private MissingImageDeliveries getMissingImageDeliveries(MissingImageDeliveriesSpec spec, ItemOptionsSpec optionsSpec, String randomId) {
        var itemOptions = new ItemOptions();
        itemOptions.setFilingHistoryCategory(optionsSpec.getFilingHistoryCategory());
        itemOptions.setFilingHistoryDate(optionsSpec.getFilingHistoryDate());
        itemOptions.setFilingHistoryDescription(optionsSpec.getFilingHistoryDescription());

        if (optionsSpec.getFilingHistoryDescriptionValues() != null) {
            FilingHistoryDescriptionValues values = mapToEntity(optionsSpec.getFilingHistoryDescriptionValues());
            itemOptions.setFilingHistoryDescriptionValues(values);
        }

        itemOptions.setFilingHistoryId(optionsSpec.getFilingHistoryId());
        itemOptions.setFilingHistoryType(optionsSpec.getFilingHistoryType());
        Optional.ofNullable(optionsSpec.getFilingHistoryBarcode()).ifPresent(itemOptions::setFilingHistoryBarcode);

        return getMissingImageDeliveries(spec, randomId, itemOptions);
    }

    private MissingImageDeliveries getMissingImageDeliveries(MissingImageDeliveriesSpec spec, String randomId,
        ItemOptions itemOptions) {
        var missingImageDeliveries = new MissingImageDeliveries();
        var url = "/orderable/missing-image-deliveries/";
        var currentDate = getCurrentDateTime().toString();

        missingImageDeliveries.setId(randomId);
        missingImageDeliveries.setCreatedAt(currentDate);
        missingImageDeliveries.setUpdatedAt(currentDate);
        missingImageDeliveries.setDataId(randomId);
        missingImageDeliveries.setCompanyName(spec.getCompanyName());
        missingImageDeliveries.setCompanyNumber(spec.getCompanyNumber());
        Optional.ofNullable(spec.getCustomerReference()).ifPresent(missingImageDeliveries::setCustomerReference);
        missingImageDeliveries.setDescription("missing image delivery for company " + spec.getCompanyNumber());
        missingImageDeliveries.setDescriptionIdentifier("missing-image-delivery");
        missingImageDeliveries.setDescriptionCompanyNumber(spec.getCompanyNumber());
        missingImageDeliveries.setDescriptionMissingImageDelivery("missing image delivery for company " + spec.getCompanyNumber());
        missingImageDeliveries.setEtag(randomService.getEtag());
        if (spec.getItemCosts() != null) {
            List<ItemCosts> itemCostsList = new ArrayList<>();
            for (ItemCostsSpec itemCostsSpec : spec.getItemCosts()) {
                var itemCosts = new ItemCosts();
                itemCosts.setDiscountApplied(itemCostsSpec.getDiscountApplied());
                itemCosts.setItemCost(itemCostsSpec.getItemCost());
                itemCosts.setCalculatedCost(itemCostsSpec.getCalculatedCost());
                itemCosts.setProductType(itemCostsSpec.getProductType());
                itemCostsList.add(itemCosts);
            }
            missingImageDeliveries.setItemCosts(itemCostsList);
        }
        missingImageDeliveries.setItemOptions(itemOptions);
        missingImageDeliveries.setKind(spec.getKind());
        missingImageDeliveries.setLinksSelf(url + randomId);
        missingImageDeliveries.setPostalDelivery(spec.isPostalDelivery());
        missingImageDeliveries.setQuantity(spec.getQuantity());
        missingImageDeliveries.setUserId(spec.getUserId());
        missingImageDeliveries.setPostageCost(spec.getPostageCost());
        missingImageDeliveries.setTotalItemCost(spec.getTotalItemCost());

        return missingImageDeliveries;
    }

    private Basket createBasket(MissingImageDeliveriesSpec spec, List<Basket.Item> items) {
        var certSpec = mapMissingImageDeliveriesToCertificatesSpec(spec);
        return certificatesService.createBasket(certSpec, items);
    }

    private CertificatesSpec mapMissingImageDeliveriesToCertificatesSpec(MissingImageDeliveriesSpec spec) {
        var certificatesSpec = new CertificatesSpec();
        certificatesSpec.setUserId(spec.getUserId());
        certificatesSpec.setBasketSpec(spec.getBasketSpec());
        return certificatesSpec;
    }

    void deleteBasket(String basketId) {
        basketRepository.findById(basketId)
            .ifPresent(basket -> {
                if (basket.getId() != null) {
                    basketRepository.delete(basket);
                }
            });
    }

    @Override
    public boolean delete(String certificateId) {
        var missingImageDeliveries = missingImageDeliveriesRepository.findById(certificateId);
        if (missingImageDeliveries.isPresent()) {
            var userId = missingImageDeliveries.get().getUserId();
            var basket = basketRepository.findById(userId);

            if (basket.isPresent()) {
                deleteBasket(userId);
            }

            missingImageDeliveriesRepository.delete(missingImageDeliveries.get());
            return true;
        }
        return false;
    }

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}