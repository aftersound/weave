package io.aftersound.service.management;

import java.util.Date;
import java.util.Map;

public class Namespace {
    private String name;
    private String owner;
    private String ownerEmail;
    private String description;
    private Map<String, Object> attributes;
    private Date created;
    private Date updated;
    private Map<String, Object> trace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Map<String, Object> getTrace() {
        return trace;
    }

    public void setTrace(Map<String, Object> trace) {
        this.trace = trace;
    }

}
