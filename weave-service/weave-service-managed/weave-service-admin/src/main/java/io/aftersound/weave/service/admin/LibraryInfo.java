package io.aftersound.weave.service.admin;

import java.util.List;
import java.util.Map;

public class LibraryInfo {

    private String path;

    private Map<String, String> description;

    private List<ExtensionInfo> extensions;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public List<ExtensionInfo> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<ExtensionInfo> extensions) {
        this.extensions = extensions;
    }
}
