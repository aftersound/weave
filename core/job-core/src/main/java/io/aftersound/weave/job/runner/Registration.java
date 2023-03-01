package io.aftersound.weave.job.runner;

import java.util.List;

public class Registration {

    private List<Capability> capabilities;

    private Capacity capacity;

    public List<Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<Capability> capabilities) {
        this.capabilities = capabilities;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

}
