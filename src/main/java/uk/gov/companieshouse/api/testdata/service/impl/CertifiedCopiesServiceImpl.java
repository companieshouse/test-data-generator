package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Basket;
import uk.gov.companieshouse.api.testdata.model.entity.Capital;
import uk.gov.companieshouse.api.testdata.model.entity.CertifiedCopies;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryDescriptionValues;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryDocument;
import uk.gov.companieshouse.api.testdata.model.entity.ItemCosts;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertifiedCopiesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDescriptionValuesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDocumentsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemCostsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.CertifiedCopiesRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CertifiedCopiesServiceImpl implements DataService<CertificatesData, CertifiedCopiesSpec> {

    @Autowired
    public CertifiedCopiesRepository certifiedCopiesRepository;

    @Autowired
    public BasketRepository basketRepository;

    @Autowired
    public AddressService addressService;

    @Autowired
    public RandomService randomService;

    @Override
    public CertificatesData create(CertifiedCopiesSpec spec) throws DataException {
        List<ItemOptionsSpec> optionsList = spec.getItemOptions();
        List<CertificatesData.CertificateEntry> certificateEntries = new ArrayList<>(optionsList.size());
        List<Basket.Item> basketItems = new ArrayList<>(optionsList.size());

        for (ItemOptionsSpec optionSpec : optionsList) {
            Long firstPart = randomService.getNumber(6);
            Long secondPart = randomService.getNumber(6);
            var randomId = "CCD-" + firstPart + "-" + secondPart;
            var certifiedCopies = getCertifiedCopies(spec, optionSpec, randomId);
            certifiedCopiesRepository.save(certifiedCopies);
            var now = getCurrentDateTime().toString();
            certificateEntries.add(new CertificatesData.CertificateEntry(
                certifiedCopies.getId(), now, now
            ));

            // Add to basket items
            var item = new Basket.Item();
            item.setItemUri(certifiedCopies.getLinksSelf());
            basketItems.add(item);
        }

        if (spec.getBasketSpec() != null) {
            var basket = createBasket(spec, basketItems);
            basketRepository.save(basket);
        }

        return new CertificatesData(certificateEntries);
    }

    private FilingHistoryDescriptionValues mapToEntity(FilingHistoryDescriptionValuesSpec spec) {
        FilingHistoryDescriptionValues entity = new FilingHistoryDescriptionValues();
        entity.setDate(spec.getDate());

        if (spec.getCapital() != null) {
            List<Capital> capitalList = spec.getCapital().stream().map(c -> {
                Capital capital = new Capital();
                capital.setCurrency(c.getCurrency());
                capital.setFigure(c.getFigure());
                return capital;
            }).toList();
            entity.setCapital(capitalList);
        }

        return entity;
    }

    private CertifiedCopies getCertifiedCopies(CertifiedCopiesSpec spec, ItemOptionsSpec optionsSpec, String randomId) {
        var itemOptions = new ItemOptions();
        Optional.ofNullable(optionsSpec.getCollectionLocation()).ifPresent(itemOptions::setCollectionLocation);
        Optional.ofNullable(optionsSpec.getContactNumber()).ifPresent(itemOptions::setContactNumber);
        itemOptions.setDeliveryTimescale(optionsSpec.getDeliveryTimescale());
        itemOptions.setDeliveryMethod(optionsSpec.getDeliveryMethod());
        Optional.ofNullable(optionsSpec.getForeName()).ifPresent(itemOptions::setForeName);
        Optional.ofNullable(optionsSpec.getSurName()).ifPresent(itemOptions::setSurName);
        if (optionsSpec.getFilingHistoryDocuments() != null) {
            List<FilingHistoryDocument> filingHistoryDocumentList = new ArrayList<>();
            for (FilingHistoryDocumentsSpec filingHistoryDocumentsSpec : optionsSpec.getFilingHistoryDocuments() ) {
                var filingHistoryDocument = new FilingHistoryDocument();
                filingHistoryDocument.setFilingHistoryDate(filingHistoryDocumentsSpec.getFilingHistoryDate());
                filingHistoryDocument.setFilingHistoryDescription(filingHistoryDocumentsSpec.getFilingHistoryDescription());
                FilingHistoryDescriptionValues values = null;
                if (filingHistoryDocumentsSpec.getFilingHistoryDescriptionValues() != null) {
                    values = mapToEntity(filingHistoryDocumentsSpec.getFilingHistoryDescriptionValues());
                    filingHistoryDocument.setFilingHistoryDescriptionValues(values);
                }
                filingHistoryDocument.setFilingHistoryId(filingHistoryDocumentsSpec.getFilingHistoryId());
                filingHistoryDocument.setFilingHistoryType(filingHistoryDocumentsSpec.getFilingHistoryType());
                filingHistoryDocument.setFilingHistoryCost(filingHistoryDocumentsSpec.getFilingHistoryCost());

                filingHistoryDocumentList.add(filingHistoryDocument);
            }
            itemOptions.setFilingHistoryDocuments(filingHistoryDocumentList);
        }

      return getCertifiedCopies(spec, randomId, itemOptions);
    }

    private CertifiedCopies getCertifiedCopies(CertifiedCopiesSpec spec, String randomId,
        ItemOptions itemOptions) {
        var certifiedCopies = new CertifiedCopies();
        var currentDate = getCurrentDateTime().toString();

        certifiedCopies.setId(randomId);
        certifiedCopies.setCreatedAt(currentDate);
        certifiedCopies.setUpdatedAt(currentDate);
        certifiedCopies.setDataId(randomId);
        certifiedCopies.setCompanyName(spec.getCompanyName());
        certifiedCopies.setCompanyNumber(spec.getCompanyNumber());
        certifiedCopies.setDescription("certified copy for company " + spec.getCompanyNumber());
        certifiedCopies.setDescriptionIdentifier("certified-copy");
        certifiedCopies.setDescriptionCompanyNumber(spec.getCompanyNumber());
        certifiedCopies.setDescriptionCertifiedCopy("certified copy for company " + spec.getCompanyNumber());
        certifiedCopies.setEtag(randomService.getEtag());
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
            certifiedCopies.setItemCosts(itemCostsList);
        }
        certifiedCopies.setItemOptions(itemOptions);
        certifiedCopies.setKind(spec.getKind());
        certifiedCopies.setLinksSelf("/orderable/certified-copies/" + randomId);
        certifiedCopies.setPostalDelivery(spec.isPostalDelivery());
        certifiedCopies.setQuantity(spec.getQuantity());
        certifiedCopies.setUserId(spec.getUserId());
        certifiedCopies.setPostageCost(spec.getPostageCost());
        certifiedCopies.setTotalItemCost(spec.getTotalItemCost());

        return certifiedCopies;
    }

    private Basket createBasket(CertifiedCopiesSpec spec, List<Basket.Item> items) {
        var address = addressService.getAddress(Jurisdiction.UNITED_KINGDOM);
        Instant now = getCurrentDateTime();

        var basket = new Basket();
        basket.setId(spec.getUserId());
        basket.setCreatedAt(now);
        basket.setUpdatedAt(now);
        basket.setDeliveryDetails(address);
        basket.setForename(spec.getBasketSpec().getForename());
        basket.setSurname(spec.getBasketSpec().getSurname());
        basket.setEnrolled(spec.getBasketSpec().getEnrolled());
        basket.setItems(items);

        return basket;
    }

    private void deleteBasket(String basketId) {
        basketRepository.findById(basketId)
                .ifPresent(basket -> {
                    if (basket.getId() != null) {
                        basketRepository.delete(basket);
                    }
                });
    }

    @Override
    public boolean delete(String certificateId) {
        var certifiedCopies = certifiedCopiesRepository.findById(certificateId);
        if (certifiedCopies.isPresent()) {
            var userId = certifiedCopies.get().getUserId();
            var basket = basketRepository.findById(userId);

            if (basket.isPresent()) {
                deleteBasket(userId);
            }

            certifiedCopiesRepository.delete(certifiedCopies.get());
            return true;
        }
        return false;
    }

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}