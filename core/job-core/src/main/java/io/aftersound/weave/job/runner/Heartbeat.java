package io.aftersound.weave.job.runner;

public class Heartbeat {

    private Instance instance;
    private Capacity capacity;

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Capacity getCapacity() {
        return capacity;
    }

    public void setCapacity(Capacity capacity) {
        this.capacity = capacity;
    }

}
