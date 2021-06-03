package agata.bol.dataholder;

import agata.bol.enums.CompanyType;
import net.corda.core.identity.Party;

import java.util.HashMap;
import java.util.Map;

public class Company {
    private final String companyName;
    private final CompanyType companyType;
    private final String firstName;
    private final String lastName;

    private final Address companyAddress;

    private final String phoneNumber;
    private final String email;

    private final Map<String, String> licenceMap;

    private final Party cordaParty;

    public Company(String companyName, CompanyType companyType, String firstName, String lastName, Address companyAddress, String phoneNumber, String email, Party cordaParty) {
        this.companyName = companyName;
        this.companyType = companyType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyAddress = companyAddress;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.cordaParty = cordaParty;
        this.licenceMap = new HashMap<>();
    }

    public String getCompanyName() {
        return companyName;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getCompanyAddress() {
        return companyAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Party getCordaParty() {
        return cordaParty;
    }

    public String getLicence(String countryCode) {
        return licenceMap.get(countryCode);
    }

    public void putLicence(String countryCode, String licence) {
        licenceMap.put(countryCode, licence);
    }


}
