package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "delta_appointments")
public class DeltaAppointment {

    @Id
    @Field("id")
    private String id;

    @Field("data.person_number")
    private String personNumber;

    @Field("data.etag")
    private String etag;

    @Field("data.service_address")
    private Address serviceAddress;

    @Field("data.service_address_is_same_as_registered_office_address")
    private Boolean serviceAddressIsSameAsRegisteredOfficeAddress;

    @Field("data.country_of_residence")
    private String countryOfResidence;

    @Field("data.appointed_on")
    private Instant appointedOn;

    @Field("data.appointed_before")
    private Instant appointedBefore;

    @Field("data.is_pre_1992_appointment")
    private Boolean isPre1992Appointment;

    @Field("data.links.self")
    private String self;

    @Field("data.links.officer.self")
    private String officerSelf;

    @Field("data.links.officer.appointments")
    private String officerAppointments;

    @Field("data.nationality")
    private String nationality;

    @Field("data.occupation")
    private String occupation;

    @Field("data.officer_role")
    private String officerRole;

    @Field("data.is_secure_officer")
    private Boolean isSecureOfficer;

    @Field("data.identification.identification_type")
    private String identificationType;

    @Field("data.identification.legal_authority")
    private String legalAuthority;

    @Field("data.identification.legal_form")
    private String legalForm;

    @Field("data.identification.place_registered")
    private String placeRegistered;

    @Field("data.identification.registration_number")
    private String registrationNumber;

    @Field("data.company_name")
    private String dataCompanyName;

    @Field("data.surname")
    private String surname;

    @Field("data.forename")
    private String forename;

    @Field("data.honours")
    private String honours;

    @Field("data.other_forenames")
    private String otherForenames;

    @Field("data.title")
    private String title;

    @Field("data.company_number")
    private String dataCompanyNumber;

    @Field("data.contact_details.contact_name")
    private String contactName;

    @Field("data.principal_office_address")
    private Address principalOfficeAddress;

    @Field("data.resigned_on")
    private Instant resignedOn;

    @Field("data.responsibilities")
    private String responsibilities;

    @Field("data.former_names.forenames")
    private String formerNamesForenames;

    @Field("data.former_names.surname")
    private String formerNamesSurname;

    @Field("sensitive_data.usual_residential_address.address_line_1")
    private String addressLine1;

    @Field("sensitive_data.usual_residential_address.address_line_2")
    private String addressLine2;

    @Field("sensitive_data.usual_residential_address.care_of")
    private String careOf;

    @Field("sensitive_data.usual_residential_address.country")
    private String country;

    @Field("sensitive_data.usual_residential_address.locality")
    private String locality;

    @Field("sensitive_data.usual_residential_address.po_box")
    private String poBox;

    @Field("sensitive_data.usual_residential_address.postal_code")
    private String postalCode;

    @Field("sensitive_data.usual_residential_address.premises")
    private String premises;

    @Field("sensitive_data.usual_residential_address.region")
    private String region;

    @Field("sensitive_data.residential_address_is_same_as_service_address")
    private Boolean residentialAddressIsSameAsServiceAddress;

    @Field("sensitive_data.date_of_birth")
    private Instant dateOfBirth;

    @Field("internal_id")
    private String internalId;

    @Field("appointment_id")
    private String appointmentId;

    @Field("officer_id")
    private String officerId;

    @Field("previous_officer_id")
    private String previousOfficerId;

    @Field("company_number")
    private String companyNumber;

    @Field("updated.at")
    private Instant updated;

    @Field("updated_by")
    private String updatedBy;

    @Field("created.at")
    private Instant created;

    @Field("delta_at")
    private Instant deltaAt;

    @Field("officer_role_sort_order")
    private int officerRoleSortOrder;

    @Field("company_name")
    private String companyName;

    @Field("company_status")
    private String companyStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Address getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public Boolean getServiceAddressIsSameAsRegisteredOfficeAddress() {
        return serviceAddressIsSameAsRegisteredOfficeAddress;
    }

    public void setServiceAddressIsSameAsRegisteredOfficeAddress(Boolean serviceAddressIsSameAsRegisteredOfficeAddress) {
        this.serviceAddressIsSameAsRegisteredOfficeAddress = serviceAddressIsSameAsRegisteredOfficeAddress;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public Instant getAppointedOn() {
        return appointedOn;
    }

    public void setAppointedOn(Instant appointedOn) {
        this.appointedOn = appointedOn;
    }

    public Instant getAppointedBefore() {
        return appointedBefore;
    }

    public void setAppointedBefore(Instant appointedBefore) {
        this.appointedBefore = appointedBefore;
    }

    public Boolean getPre1992Appointment() {
        return isPre1992Appointment;
    }

    public void setPre1992Appointment(Boolean pre1992Appointment) {
        isPre1992Appointment = pre1992Appointment;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getOfficerSelf() {
        return officerSelf;
    }

    public void setOfficerSelf(String officerSelf) {
        this.officerSelf = officerSelf;
    }

    public String getOfficerAppointments() {
        return officerAppointments;
    }

    public void setOfficerAppointments(String officerAppointments) {
        this.officerAppointments = officerAppointments;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOfficerRole() {
        return officerRole;
    }

    public void setOfficerRole(String officerRole) {
        this.officerRole = officerRole;
    }

    public Boolean getSecureOfficer() {
        return isSecureOfficer;
    }

    public void setSecureOfficer(Boolean secureOfficer) {
        isSecureOfficer = secureOfficer;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getLegalAuthority() {
        return legalAuthority;
    }

    public void setLegalAuthority(String legalAuthority) {
        this.legalAuthority = legalAuthority;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getPlaceRegistered() {
        return placeRegistered;
    }

    public void setPlaceRegistered(String placeRegistered) {
        this.placeRegistered = placeRegistered;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDataCompanyName() {
        return dataCompanyName;
    }

    public void setDataCompanyName(String dataCompanyName) {
        this.dataCompanyName = dataCompanyName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getHonours() {
        return honours;
    }

    public void setHonours(String honours) {
        this.honours = honours;
    }

    public String getOtherForenames() {
        return otherForenames;
    }

    public void setOtherForenames(String otherForenames) {
        this.otherForenames = otherForenames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataCompanyNumber() {
        return dataCompanyNumber;
    }

    public void setDataCompanyNumber(String dataCompanyNumber) {
        this.dataCompanyNumber = dataCompanyNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Address getPrincipalOfficeAddress() {
        return principalOfficeAddress;
    }

    public void setPrincipalOfficeAddress(Address principalOfficeAddress) {
        this.principalOfficeAddress = principalOfficeAddress;
    }

    public Instant getResignedOn() {
        return resignedOn;
    }

    public void setResignedOn(Instant resignedOn) {
        this.resignedOn = resignedOn;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getFormerNamesForenames() {
        return formerNamesForenames;
    }

    public void setFormerNamesForenames(String formerNamesForenames) {
        this.formerNamesForenames = formerNamesForenames;
    }

    public String getFormerNamesSurname() {
        return formerNamesSurname;
    }

    public void setFormerNamesSurname(String formerNamesSurname) {
        this.formerNamesSurname = formerNamesSurname;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCareOf() {
        return careOf;
    }

    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Boolean getResidentialAddressIsSameAsServiceAddress() {
        return residentialAddressIsSameAsServiceAddress;
    }

    public void setResidentialAddressIsSameAsServiceAddress(Boolean residentialAddressIsSameAsServiceAddress) {
        this.residentialAddressIsSameAsServiceAddress = residentialAddressIsSameAsServiceAddress;
    }

    public Instant getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Instant dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getOfficerId() {
        return officerId;
    }

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public String getPreviousOfficerId() {
        return previousOfficerId;
    }

    public void setPreviousOfficerId(String previousOfficerId) {
        this.previousOfficerId = previousOfficerId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getDeltaAt() {
        return deltaAt;
    }

    public void setDeltaAt(Instant deltaAt) {
        this.deltaAt = deltaAt;
    }

    public int getOfficerRoleSortOrder() {
        return officerRoleSortOrder;
    }

    public void setOfficerRoleSortOrder(int officerRoleSortOrder) {
        this.officerRoleSortOrder = officerRoleSortOrder;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }


    public static final class Builder {
        private String id;
        private String personNumber;
        private String etag;
        private Address serviceAddress;
        private Boolean serviceAddressIsSameAsRegisteredOfficeAddress;
        private String countryOfResidence;
        private Instant appointedOn;
        private Instant appointedBefore;
        private Boolean isPre1992Appointment;
        private String self;
        private String officerSelf;
        private String officerAppointments;
        private String nationality;
        private String occupation;
        private String officerRole;
        private Boolean isSecureOfficer;
        private String identificationType;
        private String legalAuthority;
        private String legalForm;
        private String placeRegistered;
        private String registrationNumber;
        private String dataCompanyName;
        private String surname;
        private String forename;
        private String honours;
        private String otherForenames;
        private String title;
        private String dataCompanyNumber;
        private String contactName;
        private Address principalOfficeAddress;
        private Instant resignedOn;
        private String responsibilities;
        private String formerNamesForenames;
        private String formerNamesSurname;

        // TODO: Rename these address vars to include ura? Or just set ura fields with an address object itself?
        private String addressLine1;
        private String addressLine2;
        private String careOf;
        private String country;
        private String locality;
        private String poBox;
        private String postalCode;
        private String premises;
        private String region;
        private Boolean residentialAddressIsSameAsServiceAddress;
        private Instant dateOfBirth;
        private String internalId;
        private String appointmentId;
        private String officerId;
        private String previousOfficerId;
        private String companyNumber;
        private Instant updated;
        private String updatedBy;
        private Instant created;
        private Instant deltaAt;
        private int officerRoleSortOrder;
        private String companyName;
        private String companyStatus;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder fromAppointment(Appointment appointment) {
            return new Builder()
                .id(appointment.getId())
                .internalId(appointment.getInternalId())
                .appointmentId(appointment.getAppointmentId())
                .nationality(appointment.getNationality())
                .occupation(appointment.getOccupation())
                .serviceAddressIsSameAsRegisteredOfficeAddress(appointment.isServiceAddressIsSameAsRegisteredOfficeAddress())
                .countryOfResidence(appointment.getCountryOfResidence())
                .forename(appointment.getForename())
                .appointedOn(appointment.getAppointedOn())
                .officerRole(appointment.getOfficerRole())
                .etag(appointment.getEtag())
                .serviceAddress(appointment.getServiceAddress())
                .dataCompanyNumber(appointment.getDataCompanyNumber())
                .surname(appointment.getSurname())
                .dateOfBirth(appointment.getDateOfBirth())
                .companyName(appointment.getCompanyName())
                .companyStatus(appointment.getCompanyStatus())
                .officerId(appointment.getOfficerId())
                .companyNumber(appointment.getCompanyNumber());
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder personNumber(String personNumber) {
            this.personNumber = personNumber;
            return this;
        }

        public Builder etag(String etag) {
            this.etag = etag;
            return this;
        }

        public Builder serviceAddress(Address serviceAddress) {
            this.serviceAddress = serviceAddress;
            return this;
        }

        public Builder serviceAddressIsSameAsRegisteredOfficeAddress(Boolean serviceAddressIsSameAsRegisteredOfficeAddress) {
            this.serviceAddressIsSameAsRegisteredOfficeAddress = serviceAddressIsSameAsRegisteredOfficeAddress;
            return this;
        }

        public Builder countryOfResidence(String countryOfResidence) {
            this.countryOfResidence = countryOfResidence;
            return this;
        }

        public Builder appointedOn(Instant appointedOn) {
            this.appointedOn = appointedOn;
            return this;
        }

        public Builder appointedBefore(Instant appointedBefore) {
            this.appointedBefore = appointedBefore;
            return this;
        }

        public Builder isPre1992Appointment(Boolean isPre1992Appointment) {
            this.isPre1992Appointment = isPre1992Appointment;
            return this;
        }

        public Builder self(String self) {
            this.self = self;
            return this;
        }

        public Builder officerSelf(String officerSelf) {
            this.officerSelf = officerSelf;
            return this;
        }

        public Builder officerAppointments(String appointments) {
            this.officerAppointments = appointments;
            return this;
        }

        public Builder nationality(String nationality) {
            this.nationality = nationality;
            return this;
        }

        public Builder occupation(String occupation) {
            this.occupation = occupation;
            return this;
        }

        public Builder officerRole(String officerRole) {
            this.officerRole = officerRole;
            return this;
        }

        public Builder isSecureOfficer(Boolean isSecureOfficer) {
            this.isSecureOfficer = isSecureOfficer;
            return this;
        }

        public Builder identificationType(String identificationType) {
            this.identificationType = identificationType;
            return this;
        }

        public Builder legalAuthority(String legalAuthority) {
            this.legalAuthority = legalAuthority;
            return this;
        }

        public Builder legalForm(String legalForm) {
            this.legalForm = legalForm;
            return this;
        }

        public Builder placeRegistered(String placeRegistered) {
            this.placeRegistered = placeRegistered;
            return this;
        }

        public Builder registrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
            return this;
        }

        public Builder dataCompanyName(String dataCompanyName) {
            this.dataCompanyName = dataCompanyName;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public Builder forename(String forename) {
            this.forename = forename;
            return this;
        }

        public Builder honours(String honours) {
            this.honours = honours;
            return this;
        }

        public Builder otherForenames(String otherForenames) {
            this.otherForenames = otherForenames;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder dataCompanyNumber(String dataCompanyNumber) {
            this.dataCompanyNumber = dataCompanyNumber;
            return this;
        }

        public Builder contactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public Builder principalOfficeAddress(Address principalOfficeAddress) {
            this.principalOfficeAddress = principalOfficeAddress;
            return this;
        }

        public Builder resignedOn(Instant resignedOn) {
            this.resignedOn = resignedOn;
            return this;
        }

        public Builder responsibilities(String responsibilities) {
            this.responsibilities = responsibilities;
            return this;
        }

        public Builder formerNamesForenames(String formerNamesForenames) {
            this.formerNamesForenames = formerNamesForenames;
            return this;
        }

        public Builder formerNamesSurname(String formerNamesSurname) {
            this.formerNamesSurname = formerNamesSurname;
            return this;
        }

        public Builder addressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
            return this;
        }

        public Builder addressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
            return this;
        }

        public Builder careOf(String careOf) {
            this.careOf = careOf;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder locality(String locality) {
            this.locality = locality;
            return this;
        }

        public Builder poBox(String poBox) {
            this.poBox = poBox;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder premises(String premises) {
            this.premises = premises;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder residentialAddressIsSameAsServiceAddress(Boolean residentialAddressIsSameAsServiceAddress) {
            this.residentialAddressIsSameAsServiceAddress = residentialAddressIsSameAsServiceAddress;
            return this;
        }

        public Builder dateOfBirth(Instant dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder internalId(String internalId) {
            this.internalId = internalId;
            return this;
        }

        public Builder appointmentId(String appointmentId) {
            this.appointmentId = appointmentId;
            return this;
        }

        public Builder officerId(String officerId) {
            this.officerId = officerId;
            return this;
        }

        public Builder previousOfficerId(String previousOfficerId) {
            this.previousOfficerId = previousOfficerId;
            return this;
        }

        public Builder companyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public Builder updated(Instant updated) {
            this.updated = updated;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder created(Instant created) {
            this.created = created;
            return this;
        }

        public Builder deltaAt(Instant deltaAt) {
            this.deltaAt = deltaAt;
            return this;
        }

        public Builder officerRoleSortOrder(int officerRoleSortOrder) {
            this.officerRoleSortOrder = officerRoleSortOrder;
            return this;
        }

        public Builder companyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder companyStatus(String companyStatus) {
            this.companyStatus = companyStatus;
            return this;
        }

        public DeltaAppointment build() {
            DeltaAppointment deltaAppointment = new DeltaAppointment();
            deltaAppointment.setId(id);
            deltaAppointment.setPersonNumber(personNumber);
            deltaAppointment.setEtag(etag);
            deltaAppointment.setServiceAddress(serviceAddress);
            deltaAppointment.setServiceAddressIsSameAsRegisteredOfficeAddress(serviceAddressIsSameAsRegisteredOfficeAddress);
            deltaAppointment.setCountryOfResidence(countryOfResidence);
            deltaAppointment.setAppointedOn(appointedOn);
            deltaAppointment.setAppointedBefore(appointedBefore);
            deltaAppointment.setSelf(self);
            deltaAppointment.setOfficerSelf(officerSelf);
            deltaAppointment.setOfficerAppointments(officerAppointments);
            deltaAppointment.setNationality(nationality);
            deltaAppointment.setOccupation(occupation);
            deltaAppointment.setOfficerRole(officerRole);
            deltaAppointment.setIdentificationType(identificationType);
            deltaAppointment.setLegalAuthority(legalAuthority);
            deltaAppointment.setLegalForm(legalForm);
            deltaAppointment.setPlaceRegistered(placeRegistered);
            deltaAppointment.setRegistrationNumber(registrationNumber);
            deltaAppointment.setDataCompanyName(dataCompanyName);
            deltaAppointment.setSurname(surname);
            deltaAppointment.setForename(forename);
            deltaAppointment.setHonours(honours);
            deltaAppointment.setOtherForenames(otherForenames);
            deltaAppointment.setTitle(title);
            deltaAppointment.setDataCompanyNumber(dataCompanyNumber);
            deltaAppointment.setContactName(contactName);
            deltaAppointment.setPrincipalOfficeAddress(principalOfficeAddress);
            deltaAppointment.setResignedOn(resignedOn);
            deltaAppointment.setResponsibilities(responsibilities);
            deltaAppointment.setFormerNamesForenames(formerNamesForenames);
            deltaAppointment.setFormerNamesSurname(formerNamesSurname);
            deltaAppointment.setAddressLine1(addressLine1);
            deltaAppointment.setAddressLine2(addressLine2);
            deltaAppointment.setCareOf(careOf);
            deltaAppointment.setCountry(country);
            deltaAppointment.setLocality(locality);
            deltaAppointment.setPoBox(poBox);
            deltaAppointment.setPostalCode(postalCode);
            deltaAppointment.setPremises(premises);
            deltaAppointment.setRegion(region);
            deltaAppointment.setResidentialAddressIsSameAsServiceAddress(residentialAddressIsSameAsServiceAddress);
            deltaAppointment.setDateOfBirth(dateOfBirth);
            deltaAppointment.setInternalId(internalId);
            deltaAppointment.setAppointmentId(appointmentId);
            deltaAppointment.setOfficerId(officerId);
            deltaAppointment.setPreviousOfficerId(previousOfficerId);
            deltaAppointment.setCompanyNumber(companyNumber);
            deltaAppointment.setUpdated(updated);
            deltaAppointment.setUpdatedBy(updatedBy);
            deltaAppointment.setCreated(created);
            deltaAppointment.setDeltaAt(deltaAt);
            deltaAppointment.setOfficerRoleSortOrder(officerRoleSortOrder);
            deltaAppointment.setCompanyName(companyName);
            deltaAppointment.setCompanyStatus(companyStatus);
            deltaAppointment.isPre1992Appointment = this.isPre1992Appointment;
            deltaAppointment.isSecureOfficer = this.isSecureOfficer;
            return deltaAppointment;
        }
    }
}
