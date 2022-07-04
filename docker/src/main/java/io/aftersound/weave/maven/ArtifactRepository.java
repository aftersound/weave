package io.aftersound.weave.maven;

public class ArtifactRepository {

    private String id;
    private String type;
    private String url;

    public ArtifactRepository() {
    }

    public ArtifactRepository(String id, String type, String url) {
        this.id = id;
        this.type = type;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
