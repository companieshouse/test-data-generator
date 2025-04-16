package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.ItemOptions;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import uk.gov.companieshouse.api.testdata.model.rest.ItemOptionsSpec;

@Service
public class CertificatesServiceImpl implements DataService<CertificatesData, CertificatesSpec> {

    @Autowired
    public CertificatesRepository repository;

    @Autowired
    public RandomService randomService;

    @Override
    public CertificatesData create(CertificatesSpec certificatesSpec) throws DataException {
        Long firstPart = randomService.getNumber(6);
        Long secondPart = randomService.getNumber(6);
        var randomId = "CRT-" + firstPart + "-" + secondPart;
        var certificates = getCertificates(certificatesSpec, randomId);

        repository.save(certificates);

        return new CertificatesData(
                certificates.getId(),
                certificates.getCreatedAt(),
                certificates.getUpdatedAt(),
                certificates.getDataId(),
                certificates.getCompanyName(),
                certificates.getCompanyNumber(),
                certificates.getUserId()
        );
    }

    private Certificates getCertificates(CertificatesSpec certificatesSpec, String randomId) {
        var certificates = new Certificates();
        var currentDate = getCurrentDateTime().toString();

        ItemOptionsSpec itemOptionsSpec = certificatesSpec.getItemOptions();

        ItemOptions itemOptions = new ItemOptions();
        itemOptions.setCertificateType(itemOptionsSpec.getCertificateType());
        itemOptions.setDeliveryTimescale(itemOptionsSpec.getDeliveryTimescale());
        itemOptions.setIncludeEmailCopy(itemOptionsSpec.getIncludeEmailCopy());
        itemOptions.setCompanyType(itemOptionsSpec.getCompanyType());
        itemOptions.setCompanyStatus(itemOptionsSpec.getCompanyStatus());


        certificates.setId(randomId);
        certificates.setCreatedAt(currentDate);
        certificates.setUpdatedAt(currentDate);
        certificates.setDataId(randomId);
        certificates.setCompanyName(certificatesSpec.getCompanyName());
        certificates.setCompanyNumber(certificatesSpec.getCompanyNumber());
        certificates.setDescription("certificate for company " + certificatesSpec.getCompanyNumber());
        certificates.setDescriptionIdentifier(certificatesSpec.getDescriptionIdentifier());
        certificates.setDescriptionCompanyNumber(certificatesSpec.getDescriptionCompanyNumber());
        certificates.setDescriptionCertificate(certificatesSpec.getDescriptionCertificate());
        certificates.setItemOptions(itemOptions);
        certificates.setEtag(randomService.getEtag());
        certificates.setKind(certificatesSpec.getKind());
        certificates.setLinksSelf(certificatesSpec.getLinksSelf());
        certificates.setPostalDelivery(certificatesSpec.isPostalDelivery());
        certificates.setQuantity(certificatesSpec.getQuantity());
        certificates.setUserId(certificatesSpec.getUserId());
        return certificates;
    }

    @Override
    public boolean delete(String certificateId) {
        var certificate = repository.findById(certificateId);
        System.out.println("Certificate not found with id: " + certificate);
        certificate.ifPresent(repository::delete);
        return certificate.isPresent();
    }

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}
