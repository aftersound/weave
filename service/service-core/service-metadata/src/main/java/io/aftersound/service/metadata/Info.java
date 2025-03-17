package io.aftersound.service.metadata;

/**
 * Provides metadata about the service runtime spec and services.
 * The model is based on OpenAPI Specification 3.1.0.
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification 'Info Object'</a>
 */
public class Info {

    private String title;
    private String description;
    private String summary;
    private String termOfService;
    private Contact contact;
    private License license;
    private String version;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTermOfService() {
        return termOfService;
    }

    public void setTermOfService(String termOfService) {
        this.termOfService = termOfService;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
