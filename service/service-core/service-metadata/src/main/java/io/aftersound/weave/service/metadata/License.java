package io.aftersound.weave.service.metadata;

/**
 * Provides metadata about the service runtime spec and services.
 * The model is based on OpenAPI Specification 3.1.0.
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification 'License Object'</a>
 */
public class License {

    private String name;
    private String identifier;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
