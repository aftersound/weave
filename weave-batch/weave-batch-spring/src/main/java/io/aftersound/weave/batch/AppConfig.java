package io.aftersound.weave.batch;

import java.util.List;
import java.util.Map;

class AppConfig {

    private Map<String, String> springDataSourceConfig;

    // Controls
    private List<String> jsonSpecTypes;
    private List<String> dataSourceControlTypes;
    private List<String> extractControlTypes;
    private List<String> transformControlTypes;
    private List<String> loadControlTypes;

    // Actors, each acts in according to pairing Control
    private List<String> dataClientFactoryTypes;
    private List<String> fileHandlerTypes;
    private List<String> fileFilterTypes;

    public Map<String, String> getSpringDataSourceConfig() {
        return springDataSourceConfig;
    }

    public void setSpringDataSourceConfig(Map<String, String> springDataSourceConfig) {
        this.springDataSourceConfig = springDataSourceConfig;
    }

    public List<String> getJsonSpecTypes() {
        return jsonSpecTypes;
    }

    public void setJsonSpecTypes(List<String> jsonSpecTypes) {
        this.jsonSpecTypes = jsonSpecTypes;
    }

    public List<String> getDataSourceControlTypes() {
        return dataSourceControlTypes;
    }

    public void setDataSourceControlTypes(List<String> dataSourceControlTypes) {
        this.dataSourceControlTypes = dataSourceControlTypes;
    }

    public List<String> getExtractControlTypes() {
        return extractControlTypes;
    }

    public void setExtractControlTypes(List<String> extractControlTypes) {
        this.extractControlTypes = extractControlTypes;
    }

    public List<String> getTransformControlTypes() {
        return transformControlTypes;
    }

    public void setTransformControlTypes(List<String> transformControlTypes) {
        this.transformControlTypes = transformControlTypes;
    }

    public List<String> getLoadControlTypes() {
        return loadControlTypes;
    }

    public void setLoadControlTypes(List<String> loadControlTypes) {
        this.loadControlTypes = loadControlTypes;
    }

    public List<String> getDataClientFactoryTypes() {
        return dataClientFactoryTypes;
    }

    public void setDataClientFactoryTypes(List<String> dataClientFactoryTypes) {
        this.dataClientFactoryTypes = dataClientFactoryTypes;
    }

    public List<String> getFileHandlerTypes() {
        return fileHandlerTypes;
    }

    public void setFileHandlerTypes(List<String> fileHandlerTypes) {
        this.fileHandlerTypes = fileHandlerTypes;
    }

    public List<String> getFileFilterTypes() {
        return fileFilterTypes;
    }

    public void setFileFilterTypes(List<String> fileFilterTypes) {
        this.fileFilterTypes = fileFilterTypes;
    }
}
