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
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.DirectorDetails;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.entity.RegisteredOfficeAddressDetails;
import uk.gov.companieshouse.api.testdata.model.entity.SecretaryDetails;
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
        List<CertificatesData.CertificateEntry> certificateEntries = new ArrayList<>(optionsList.size());
        List<Basket.Item> basketItems = new ArrayList<>(optionsList.size());

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
        Optional.ofNullable(optionsSpec.getCompanyStatus()).ifPresent(itemOptions::setCompanyStatus);
        Optional.ofNullable(optionsSpec.getCompanyType()).ifPresent(itemOptions::setCompanyType);
        Optional.ofNullable(optionsSpec.getDeliveryMethod()).ifPresent(itemOptions::setDeliveryMethod);
        Optional.ofNullable(optionsSpec.getDeliveryTimescale()).ifPresent(itemOptions::setDeliveryTimescale);
        if (optionsSpec.getDirectorDetails() != null) {
            var directorDetails = new DirectorDetails();
            Optional.ofNullable(optionsSpec.getDirectorDetails().getIncludeAddress()).ifPresent(directorDetails::setIncludeAddress);
            Optional.ofNullable(optionsSpec.getDirectorDetails().getIncludeAppointmentDate()).ifPresent(directorDetails::setIncludeAppointmentDate);
            Optional.ofNullable(optionsSpec.getDirectorDetails().getIncludeBasicInformation()).ifPresent(directorDetails::setIncludeBasicInformation);
            Optional.ofNullable(optionsSpec.getDirectorDetails().getIncludeCountryOfResidence()).ifPresent(directorDetails::setIncludeCountryOfResidence);
            Optional.ofNullable(optionsSpec.getDirectorDetails().getIncludeDobType()).ifPresent(directorDetails::setIncludeDobType);
            Optional.ofNullable(optionsSpec.getDirectorDetails().getIncludeNationality()).ifPresent(directorDetails::setIncludeNationality);
            Optional.ofNullable(optionsSpec.getDirectorDetails().getIncludeOccupation()).ifPresent(directorDetails::setIncludeOccupation);
            itemOptions.setDirectorDetails(directorDetails);
        }
        Optional.ofNullable(optionsSpec.getForeName()).ifPresent(itemOptions::setForeName);
        Optional.ofNullable(optionsSpec.getIncludeCompanyObjectsInformation()).ifPresent(itemOptions::setIncludeCompanyObjectsInformation);
        Optional.ofNullable(optionsSpec.getIncludeEmailCopy()).ifPresent(itemOptions::setIncludeEmailCopy);
        Optional.ofNullable(optionsSpec.getIncludeGoodStandingInformation()).ifPresent(itemOptions::setIncludeGoodStandingInformation);
        if (optionsSpec.getRegisteredOfficeAddressDetails() != null) {
            var registeredOfficeAddressDetails = new RegisteredOfficeAddressDetails();
            Optional.ofNullable(optionsSpec.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType()).ifPresent(registeredOfficeAddressDetails::setIncludeAddressRecordsType);
            Optional.ofNullable(optionsSpec.getRegisteredOfficeAddressDetails().getIncludeDates()).ifPresent(registeredOfficeAddressDetails::setIncludeDates);
            itemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        }
        if (optionsSpec.getSecretaryDetails() != null) {
            var secretaryDetails = new SecretaryDetails();
            Optional.ofNullable(optionsSpec.getSecretaryDetails().getIncludeAddress()).ifPresent(secretaryDetails::setIncludeAddress);
            Optional.ofNullable(optionsSpec.getSecretaryDetails().getIncludeAppointmentDate()).ifPresent(secretaryDetails::setIncludeAppointmentDate);
            Optional.ofNullable(optionsSpec.getSecretaryDetails().getIncludeBasicInformation()).ifPresent(secretaryDetails::setIncludeBasicInformation);
            Optional.ofNullable(optionsSpec.getSecretaryDetails().getIncludeCountryOfResidence()).ifPresent(secretaryDetails::setIncludeCountryOfResidence);
            Optional.ofNullable(optionsSpec.getSecretaryDetails().getIncludeDobType()).ifPresent(secretaryDetails::setIncludeDobType);
            Optional.ofNullable(optionsSpec.getSecretaryDetails().getIncludeNationality()).ifPresent(secretaryDetails::setIncludeNationality);
            Optional.ofNullable(optionsSpec.getSecretaryDetails().getIncludeOccupation()).ifPresent(secretaryDetails::setIncludeOccupation);
            itemOptions.setSecretaryDetails(secretaryDetails);
        }
        Optional.ofNullable(optionsSpec.getSurName()).ifPresent(itemOptions::setSurName);

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
        certificates.setDescriptionCompanyNumber(spec.getCompanyNumber());
        certificates.setDescriptionCertificate("certificate for company " + spec.getCompanyNumber());
        certificates.setItemOptions(itemOptions);
        certificates.setEtag(randomService.getEtag());
        certificates.setKind(spec.getKind());
        certificates.setLinksSelf("/orderable/certificates/" + randomId);
        certificates.setPostalDelivery(spec.isPostalDelivery());
        certificates.setQuantity(spec.getQuantity());
        certificates.setUserId(spec.getUserId());

        return certificates;
    }

    Basket createBasket(CertificatesSpec spec, List<Basket.Item> items) {
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
        var certificate = certificatesRepository.findById(certificateId);
        if (certificate.isPresent()) {
            var userId = certificate.get().getUserId();
            var basket = basketRepository.findById(userId);

            if (basket.isPresent()) {
                this.deleteBasket(userId);
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