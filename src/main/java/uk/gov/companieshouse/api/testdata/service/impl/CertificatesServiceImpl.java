package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.Basket;
import uk.gov.companieshouse.api.testdata.model.entity.Basket.Item;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.repository.BasketRepository;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;

@Service
public class CertificatesServiceImpl implements DataService<CertificatesData, CertificatesSpec> {

    @Autowired
    public CertificatesRepository repository;

    @Autowired
    public BasketRepository basketRepository;

    @Autowired
    public AddressService addressService;

    @Autowired
    public RandomService randomService;

    @Override
    public CertificatesData create(CertificatesSpec certificatesSpec) throws DataException {
        Long firstPart = randomService.getNumber(6);
        Long secondPart = randomService.getNumber(6);
        var randomId = "CRT-" + firstPart + "-" + secondPart;
        var certificates = getCertificates(certificatesSpec, randomId);

        repository.save(certificates);

        var basket = createBasket(certificatesSpec, certificates);
        System.out.println("Basket ID: " + basket.getId());
        basketRepository.save(basket);

        return new CertificatesData(
                certificates.getId(),
                certificates.getCreatedAt(),
                certificates.getUpdatedAt()
        );
    }

    private Certificates getCertificates(CertificatesSpec spec, String randomId) {
        var optionsSpec = spec.getItemOptions();
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
        certificates.setDescription("certificate for company " + spec.getCompanyNumber());
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

    private Basket createBasket(CertificatesSpec spec, Certificates certificates) {
        Address address = addressService.getAddress(Jurisdiction.UNITED_KINGDOM);

        Item item = new Item();
        item.setItemUri(certificates.getLinksSelf());

        Basket basket = new Basket();
        Instant now = getCurrentDateTime();

        basket.setId(spec.getUserId()); // Basket ID = User ID
        basket.setCreatedAt(now);
        basket.setUpdatedAt(now);
        basket.setDeliveryDetails(address);
        basket.setForeName(spec.getBasketSpec().getForename());
        basket.setSurName(spec.getBasketSpec().getSurname());
        basket.setItems(List.of(item));
        basket.setEnrolled(spec.getBasketSpec().getEnrolled());

        return basket;
    }

//    public boolean deleteBasket(String basketId) {
//        var basket = basketRepository.findById(basketId);
//        basket.ifPresent(basketRepository::delete);
//        return basket.isPresent();
//    }

    @Override
    public boolean delete(String certificateId) {
        var certificate = repository.findById(certificateId);
        if (certificate.isPresent()) {
            repository.delete(certificate.get());
//            return deleteBasket(certificate.get().getUserId());
        }
        return false;
    }


    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}
