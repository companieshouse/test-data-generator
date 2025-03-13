package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Register;
import uk.gov.companieshouse.api.testdata.model.entity.RegisterItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyRegistersRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyRegistersServiceImpl implements DataService<CompanyRegisters, CompanySpec> {
    @Autowired
    private RandomService randomService;

    @Autowired
    private CompanyRegistersRepository repository;

    private static final String LINK_STEM = "/company/";
    private static final String DIRECTORS_LINK = "/company/%s/officers?register_view=true&register_type=directors";
    private static final String SECRETARIES_LINK = "/company/%s/officers?register_view=true&register_type=secretaries";
    private static final String PSC_LINK = "/company/%s/persons-with-significant-control?register_view=true";
    private static final String DIRECTORS_TEXT = "directors";
    private static final String SECRETARIES_TEXT = "secretaries";
    private static final String PSC_TEXT = "persons-with-significant-control";
    private static final String MEMBERS_TEXT = "members";
    private static final String REGISTER_STEM = "/registers";

    @Override
    public CompanyRegisters create(CompanySpec companySpec) throws DataException {
        var now = LocalDate.now();
        var companyRegisters = new CompanyRegisters();
        companyRegisters.setId(companySpec.getCompanyNumber());
        companyRegisters.setCompanyNumber(companySpec.getCompanyNumber());
        companyRegisters.setCreatedAt(now);
        companyRegisters.setUpdatedAt(now);
        companyRegisters.setDeltaAt(now);
        companyRegisters.setKind("registers");
        companyRegisters.setEtag(randomService.getEtag());
        companyRegisters.setSelfLink(generateSelfLink(companySpec.getCompanyNumber()));
        companyRegisters.setRegisters(createRegisters(companySpec.getRegisters(), companySpec.getCompanyNumber()));

        return repository.save(companyRegisters);
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<CompanyRegisters> existingRegister = repository.deleteByCompanyNumber(companyNumber);
        return existingRegister.isPresent();
    }

    private String generateSelfLink(String companyNumber) {
        return LINK_STEM + companyNumber + REGISTER_STEM;
    }

    private Map<String, Register> createRegisters(List<RegistersSpec> registers, String companyNumber) throws DataException {
        for(RegistersSpec register : registers) {
            if (register.getRegisterType() == null || register.getRegisterType().isBlank() ) {
                throw new DataException("Register type must be provided");
            }
            if(register.getRegisterMovedTo() == null || register.getRegisterMovedTo().isBlank()) {
                throw new DataException("Register moved to must be provided");
            }
        }
        return registers.stream().collect(Collectors.toMap(
                reg -> RegisterType.getMappedType(reg.getRegisterType()),
                reg -> buildRegister(reg, companyNumber)
        ));
    }

    private Register buildRegister(RegistersSpec registerSpec, String companyNumber) {
        var registerItem = new RegisterItem();
        registerItem.setRegisterMovedTo(registerSpec.getRegisterMovedTo());
        registerItem.setMovedOn(LocalDate.now());
        var register = new Register();
        register.setRegisterType(registerSpec.getRegisterType());
        register.setItems(Collections.singletonList(registerItem));
        Map<String, String> registerLinks = generateRegisterLinks(registerSpec.getRegisterType(), companyNumber);
        if (!registerLinks.isEmpty()) {
            register.setLinks(registerLinks);
        }
        return register;
    }

    private Map<String, String> generateRegisterLinks(String registerType, String companyNumber) {
        return switch (registerType) {
            case DIRECTORS_TEXT -> Map.of("directors_register", DIRECTORS_LINK.formatted(companyNumber));
            case SECRETARIES_TEXT -> Map.of("secretaries_register", SECRETARIES_LINK.formatted(companyNumber));
            case PSC_TEXT -> Map.of("persons_with_significant_control_register", PSC_LINK.formatted(companyNumber));
            default -> Collections.emptyMap();
        };
    }

    private enum RegisterType {
        DIRECTORS(DIRECTORS_TEXT, DIRECTORS_TEXT),
        SECRETARIES(SECRETARIES_TEXT, SECRETARIES_TEXT),
        PSC(PSC_TEXT, "persons_with_significant_control"),
        USUAL_RESIDENTIAL_ADDRESS("usual-residential-address", "usual_residential_address"),
        MEMBERS(MEMBERS_TEXT, MEMBERS_TEXT),
        LLP_MEMBERS("llp-members", MEMBERS_TEXT),
        LLP_USUAL_RESIDENTIAL_ADDRESS("llp-usual-residential-address", "usual_residential_address");

        private static final Map<String, String> TYPE_MAP = Arrays.stream(values())
                .collect(Collectors.toUnmodifiableMap(rt -> rt.originalType, rt -> rt.mappedType));

        private final String originalType;
        private final String mappedType;

        RegisterType(String originalType, String mappedType) {
            this.originalType = originalType;
            this.mappedType = mappedType;
        }

        public static String getMappedType(String originalType) {
            return TYPE_MAP.getOrDefault(originalType, originalType);
        }
    }
}
