package io.aftersound.weave.service.metadata;

/**
 * Allows referencing an external resource for extended documentation.
 * The model is based on OpenAPI Specification 3.1.0.
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification 'License Object'</a>
 */
public class ExternalDocumentation {

    private String description;
    private String url;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
