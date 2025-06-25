package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Basket;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CertificatesServiceImpl implements DataService<CertificatesData, CertificatesSpec> {

    @Autowired
    public CertificatesRepository certificatesRepository;

    @Autowired
    public BasketRepository basketRepository;

    @Autowired
    public AddressService addressService;

    @Autowired
    public RandomService randomService;

    @Override
    public CertificatesData create(CertificatesSpec spec) throws DataException {
        List<ItemOptionsSpec> optionsList = spec.getItemOptions();
        List<CertificatesData.CertificateEntry> certificateEntries = new ArrayList<>();
        List<Basket.Item> basketItems = new ArrayList<>();

        for (ItemOptionsSpec optionSpec : optionsList) {
            Long firstPart = randomService.getNumber(6);
            Long secondPart = randomService.getNumber(6);
            var randomId = "CRT-" + firstPart + "-" + secondPart;
            var certificate = getCertificates(spec, optionSpec, randomId);
            certificatesRepository.save(certificate);
            var now = getCurrentDateTime().toString();
            certificateEntries.add(new CertificatesData.CertificateEntry(
                certificate.getId(), now, now
            ));

            // Add to basket items
            var item = new Basket.Item();
            item.setItemUri(certificate.getLinksSelf());
            basketItems.add(item);
        }

        if (spec.getBasketSpec() != null) {
            var basket = createBasket(spec, basketItems);
            basketRepository.save(basket);
        }

        return new CertificatesData(certificateEntries);
    }

    private Certificates getCertificates(CertificatesSpec spec, ItemOptionsSpec optionsSpec, String randomId) {
        var itemOptions = new ItemOptions();
        itemOptions.setCertificateType(optionsSpec.getCertificateType());
        itemOptions.setDeliveryTimescale(optionsSpec.getDeliveryTimescale());
        itemOptions.setIncludeEmailCopy(optionsSpec.getIncludeEmailCopy());
        itemOptions.setCompanyType(optionsSpec.getCompanyType());
        itemOptions.setCompanyStatus(optionsSpec.getCompanyStatus());

        var certificates = new Certificates();
        var currentDate = getCurrentDateTime().toString();

        certificates.setId(randomId);
        certificates.setCreatedAt(currentDate);
        certificates.setUpdatedAt(currentDate);
        certificates.setDataId(randomId);
        certificates.setCompanyName(spec.getCompanyName());
        certificates.setCompanyNumber(spec.getCompanyNumber());
        certificates.setDescription(spec.getDescription());
        certificates.setDescriptionIdentifier(spec.getDescriptionIdentifier());
        certificates.setDescriptionCompanyNumber(spec.getDescriptionCompanyNumber());
        certificates.setDescriptionCertificate(spec.getDescriptionCertificate());
        certificates.setItemOptions(itemOptions);
        certificates.setEtag(randomService.getEtag());
        certificates.setKind(spec.getKind());
        certificates.setLinksSelf("/orderable/certificates/" + randomId);
        certificates.setPostalDelivery(spec.isPostalDelivery());
        certificates.setQuantity(spec.getQuantity());
        certificates.setUserId(spec.getUserId());

        return certificates;
    }

    private Basket createBasket(CertificatesSpec spec, List<Basket.Item> items) {
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
        var certificate = certificatesRepository.findById(certificateId);
        if (certificate.isPresent()) {
            var userId = certificate.get().getUserId();
            var basket = basketRepository.findById(userId);

            if (basket.isPresent()) {
                deleteBasket(userId);
            }

            certificatesRepository.delete(certificate.get());
            return true;
        }
        return false;
    }

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}