package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedCompanySpec;
import uk.gov.companieshouse.api.testdata.repository.*;
import uk.gov.companieshouse.api.testdata.service.CombinedTdgCompanyService;

import java.util.List;

@Service
public class CombinedTdgCompanyServiceImpl implements CombinedTdgCompanyService {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private CompanyAuthCodeRepository authCodeRepository;

    @Autowired
    FilingHistoryRepository filingHistoryRepository;

    @Autowired
    AppointmentsDataRepository appointmentsDataRepository;

    @Autowired
    AppointmentsRepository appointmentRepository;

    @Autowired
    private CompanyMetricsRepository companyMetricsRepository;

    @Autowired
    private CompanyPscStatementRepository companyPscStatementRepository;

    @Autowired
    private CompanyPscsRepository companyPscsRepository;

    @Autowired
    private CompanyRegistersRepository companyRegistersRepository;

    @Autowired
    private DisqualificationsRepository disqualificationsRepository;

    @Autowired
    private OfficerRepository officerRepository;

    @Override
    public void createCombinedCompany(CombinedCompanySpec companySpec) {

        var companyProfile = companySpec.getCompanyProfile();
        companyProfileRepository.save(companyProfile);

        var companyAuthCode = companySpec.getCompanyAuthCode();
        if (companyAuthCode != null) {
            authCodeRepository.save(companyAuthCode);
        }

        if (companySpec.getFilingHistory() != null) {
            filingHistoryRepository.save(companySpec.getFilingHistory());
        }

        var appointmentsData = companySpec.getAppointmentsData().getAppointmentsData();
        for (AppointmentsData data : appointmentsData) {
            appointmentsDataRepository.save(data);
        }

        var appointments = companySpec.getAppointmentsData().getAppointment();
        for (var appointment : appointments) {
            appointmentRepository.save(appointment);
        }

        var officerAppointments = companySpec.getAppointmentsData().getOfficerAppointment();
        if (officerAppointments != null) {
            for (var officerAppointment : officerAppointments) {
                officerRepository.save(officerAppointment);
            }
        }

        if (companySpec.getCompanyMetrics() != null) {
            companyMetricsRepository.save(companySpec.getCompanyMetrics());
        }

        var companyPscStatements = companySpec.getCompanyPscStatement();
        if (companyPscStatements != null) {
            for (CompanyPscStatement companyPscStatement : companyPscStatements) {
                companyPscStatementRepository.save(companyPscStatement);
            }
        }

        var companyPscsList = companySpec.getCompanyPscs();
        if (companyPscsList != null) {
            for (CompanyPscs companyPscs : companyPscsList) {
                companyPscsRepository.save(companyPscs);
            }
        }
        if (companySpec.getCompanyRegisters() != null) {
            companyRegistersRepository.save(companySpec.getCompanyRegisters());
        }

        if (companySpec.getDisqualifications() != null) {
            disqualificationsRepository.save(companySpec.getDisqualifications());
        }

    }
}

