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
            itemOptions.setDirectorDetails(buildDirectorDetails(optionsSpec));
        }
        Optional.ofNullable(optionsSpec.getForeName()).ifPresent(itemOptions::setForeName);
        Optional.ofNullable(optionsSpec.getIncludeCompanyObjectsInformation()).ifPresent(itemOptions::setIncludeCompanyObjectsInformation);
        Optional.ofNullable(optionsSpec.getIncludeEmailCopy()).ifPresent(itemOptions::setIncludeEmailCopy);
        Optional.ofNullable(optionsSpec.getIncludeGoodStandingInformation()).ifPresent(itemOptions::setIncludeGoodStandingInformation);
        if (optionsSpec.getRegisteredOfficeAddressDetails() != null) {
            itemOptions.setRegisteredOfficeAddressDetails(buildRegisteredOfficeAddressDetails(optionsSpec));
        }
        if (optionsSpec.getSecretaryDetails() != null) {
            itemOptions.setSecretaryDetails(buildSecretaryDetails(optionsSpec));
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

    private DirectorDetails buildDirectorDetails(ItemOptionsSpec optionsSpec) {
        var details = new DirectorDetails();
        var director = optionsSpec.getDirectorDetails();

        Optional.ofNullable(director.getIncludeAddress()).ifPresent(details::setIncludeAddress);
        Optional.ofNullable(director.getIncludeAppointmentDate()).ifPresent(details::setIncludeAppointmentDate);
        Optional.ofNullable(director.getIncludeBasicInformation()).ifPresent(details::setIncludeBasicInformation);
        Optional.ofNullable(director.getIncludeCountryOfResidence()).ifPresent(details::setIncludeCountryOfResidence);
        Optional.ofNullable(director.getIncludeDobType()).ifPresent(details::setIncludeDobType);
        Optional.ofNullable(director.getIncludeNationality()).ifPresent(details::setIncludeNationality);
        Optional.ofNullable(director.getIncludeOccupation()).ifPresent(details::setIncludeOccupation);

        return details;
    }

    private SecretaryDetails buildSecretaryDetails(ItemOptionsSpec optionsSpec) {
        var details = new SecretaryDetails();
        var secretary = optionsSpec.getSecretaryDetails();

        Optional.ofNullable(secretary.getIncludeAddress()).ifPresent(details::setIncludeAddress);
        Optional.ofNullable(secretary.getIncludeAppointmentDate()).ifPresent(details::setIncludeAppointmentDate);
        Optional.ofNullable(secretary.getIncludeBasicInformation()).ifPresent(details::setIncludeBasicInformation);
        Optional.ofNullable(secretary.getIncludeCountryOfResidence()).ifPresent(details::setIncludeCountryOfResidence);
        Optional.ofNullable(secretary.getIncludeDobType()).ifPresent(details::setIncludeDobType);
        Optional.ofNullable(secretary.getIncludeNationality()).ifPresent(details::setIncludeNationality);
        Optional.ofNullable(secretary.getIncludeOccupation()).ifPresent(details::setIncludeOccupation);

        return details;
    }

    private RegisteredOfficeAddressDetails buildRegisteredOfficeAddressDetails(ItemOptionsSpec optionsSpec) {
        var details = new RegisteredOfficeAddressDetails();
        var address = optionsSpec.getRegisteredOfficeAddressDetails();

        Optional.ofNullable(address.getIncludeAddressRecordsType()).ifPresent(details::setIncludeAddressRecordsType);
        Optional.ofNullable(address.getIncludeDates()).ifPresent(details::setIncludeDates);

        return details;
    }

    Basket createBasket(CertificatesSpec spec, List<Basket.Item> items) {
        Instant now = getCurrentDateTime();
        var address = addressService.getAddress(Jurisdiction.UNITED_KINGDOM);

        // Check if basket already exists for the user
        Optional<Basket> existingBasketOpt = basketRepository.findById(spec.getUserId());

        if (existingBasketOpt.isPresent()) {
            Basket existingBasket = existingBasketOpt.get();
            // Add new items to existing basket
            List<Basket.Item> existingItems = existingBasket.getItems();
            if (existingItems == null) {
                existingItems = new ArrayList<>();
                existingBasket.setItems(existingItems);
            }
            existingItems.addAll(items);

            existingBasket.setUpdatedAt(now);
            return existingBasket; // Will be saved by caller
        }

        // Create new basket if it does not exist
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