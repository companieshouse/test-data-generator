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
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertifiedCopiesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDescriptionValuesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.FilingHistoryDocumentsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.CertifiedCopiesRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CertifiedCopiesServiceImpl implements DataService<CertificatesData, CertifiedCopiesSpec> {

    private final CertifiedCopiesRepository certifiedCopiesRepository;

    private final BasketRepository basketRepository;

    private final RandomService randomService;

    private final CertificatesServiceImpl certificatesService;

    @Autowired
    public CertifiedCopiesServiceImpl(CertifiedCopiesRepository certifiedCopiesRepository, BasketRepository basketRepository, 
            RandomService randomService, CertificatesServiceImpl certificatesService) {
        super();
        this.certifiedCopiesRepository = certifiedCopiesRepository;
        this.basketRepository = basketRepository;
        this.randomService = randomService;
        this.certificatesService = certificatesService;
    }

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

    protected FilingHistoryDescriptionValues mapToEntity(FilingHistoryDescriptionValuesSpec spec) {
        var filingHistoryDescriptionValues = new FilingHistoryDescriptionValues();
        Optional.ofNullable(spec.getDate()).ifPresent(filingHistoryDescriptionValues::setDate);
        Optional.ofNullable(spec.getOfficerName()).ifPresent(filingHistoryDescriptionValues::setOfficerName);
        Optional.ofNullable(spec.getMadeUpDate()).ifPresent(filingHistoryDescriptionValues::setMadeUpDate);
        Optional.ofNullable(spec.getChargeNumber()).ifPresent(filingHistoryDescriptionValues::setChargeNumber);

        if (spec.getCapital() != null) {
            List<Capital> capitalList = spec.getCapital().stream()
                .map(c -> {
                    var capital = new Capital();
                    capital.setCurrency(c.getCurrency());
                    capital.setFigure(c.getFigure());
                    return capital;
                })
                .toList();
            filingHistoryDescriptionValues.setCapital(capitalList);
        }

        return filingHistoryDescriptionValues;
    }

    private CertifiedCopies getCertifiedCopies(CertifiedCopiesSpec spec, ItemOptionsSpec optionsSpec, String randomId) {
        var itemOptions = new ItemOptions();
        Optional.ofNullable(optionsSpec.getCollectionLocation()).ifPresent(itemOptions::setCollectionLocation);
        Optional.ofNullable(optionsSpec.getContactNumber()).ifPresent(itemOptions::setContactNumber);
        Optional.ofNullable(optionsSpec.getDeliveryTimescale()).ifPresent(itemOptions::setDeliveryTimescale);
        itemOptions.setDeliveryMethod(optionsSpec.getDeliveryMethod());
        Optional.ofNullable(optionsSpec.getForeName()).ifPresent(itemOptions::setForeName);
        Optional.ofNullable(optionsSpec.getSurName()).ifPresent(itemOptions::setSurName);
        if (optionsSpec.getFilingHistoryDocuments() != null) {
            List<FilingHistoryDocument> filingHistoryDocumentList = new ArrayList<>();
            for (FilingHistoryDocumentsSpec filingHistoryDocumentsSpec : optionsSpec.getFilingHistoryDocuments() ) {
                var filingHistoryDocument = new FilingHistoryDocument();
                filingHistoryDocument.setFilingHistoryDate(filingHistoryDocumentsSpec.getFilingHistoryDate());
                filingHistoryDocument.setFilingHistoryDescription(filingHistoryDocumentsSpec.getFilingHistoryDescription());
                if (filingHistoryDocumentsSpec.getFilingHistoryDescriptionValues() != null) {
                    filingHistoryDocument.setFilingHistoryDescriptionValues(
                        mapToEntity(filingHistoryDocumentsSpec.getFilingHistoryDescriptionValues()));
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
        var url = "/orderable/certified-copies/";
        var certifiedCopies = new CertifiedCopies();
        var currentDate = getCurrentDateTime().toString();

        certifiedCopies.setId(randomId);
        certifiedCopies.setCreatedAt(currentDate);
        certifiedCopies.setUpdatedAt(currentDate);
        certifiedCopies.setDataId(randomId);
        certifiedCopies.setCompanyName(spec.getCompanyName());
        certifiedCopies.setCompanyNumber(spec.getCompanyNumber());
        Optional.ofNullable(spec.getCustomerReference()).ifPresent(certifiedCopies::setCustomerReference);
        certifiedCopies.setDescription("certified copy for company " + spec.getCompanyNumber());
        certifiedCopies.setDescriptionIdentifier("certified-copy");
        certifiedCopies.setDescriptionCompanyNumber(spec.getCompanyNumber());
        certifiedCopies.setDescriptionCertifiedCopy("certified copy for company " + spec.getCompanyNumber());
        certifiedCopies.setEtag(randomService.getEtag());
        if (spec.getItemCosts() != null) {
            List<ItemCosts> itemCostsList = spec.getItemCosts().stream()
                    .map(itemCostsSpec -> {
                        var itemCosts = new ItemCosts();
                        itemCosts.setDiscountApplied(itemCostsSpec.getDiscountApplied());
                        itemCosts.setItemCost(itemCostsSpec.getItemCost());
                        itemCosts.setCalculatedCost(itemCostsSpec.getCalculatedCost());
                        itemCosts.setProductType(itemCostsSpec.getProductType());
                        return itemCosts;
                    })
                    .toList();
            certifiedCopies.setItemCosts(itemCostsList);
        }
        certifiedCopies.setItemOptions(itemOptions);
        certifiedCopies.setKind(spec.getKind());
        certifiedCopies.setLinksSelf(url + randomId);
        certifiedCopies.setPostalDelivery(spec.isPostalDelivery());
        certifiedCopies.setQuantity(spec.getQuantity());
        certifiedCopies.setUserId(spec.getUserId());
        certifiedCopies.setPostageCost(spec.getPostageCost());
        certifiedCopies.setTotalItemCost(spec.getTotalItemCost());

        return certifiedCopies;
    }

    private Basket createBasket(CertifiedCopiesSpec spec, List<Basket.Item> items) {
        var certSpec = mapCertifiedCopiesToCertificatesSpec(spec);
        return certificatesService.createBasket(certSpec, items);
    }

    private CertificatesSpec mapCertifiedCopiesToCertificatesSpec(CertifiedCopiesSpec spec) {
        var certificatesSpec = new CertificatesSpec();
        certificatesSpec.setUserId(spec.getUserId());
        certificatesSpec.setBasketSpec(spec.getBasketSpec());
        return certificatesSpec;
    }

    protected void deleteBasket(String basketId) {
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