package io.aftersound.service.metadata;

/**
 * Contact information of the service runtime spec and services.
 * The model is based on OpenAPI Specification 3.1.0.
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification 'Contact Object'</a>
 */
public class Contact {
    private String name;
    private String url;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
