package io.aftersound.service.runtime;

import java.util.concurrent.atomic.AtomicBoolean;

public class ConfigUpdateStrategy {

    private enum Strategy {
        AutoRefresh,
        Ondemand
    }

    private final Strategy updateStrategy;

    private final long autoRefreshInterval;
    private final AtomicBoolean autoRefreshStartGate = new AtomicBoolean(false);

    private ConfigUpdateStrategy(Strategy updateStrategy, long autoRefreshInterval) {
        this.updateStrategy = updateStrategy;
        this.autoRefreshInterval = autoRefreshInterval;
    }

    public static ConfigUpdateStrategy ondemand() {
        return new ConfigUpdateStrategy(Strategy.Ondemand, -1L);
    }

    public static ConfigUpdateStrategy autoRefresh(long autoRefreshInterval) {
        return new ConfigUpdateStrategy(Strategy.AutoRefresh, autoRefreshInterval);
    }

    public void openAutoRefreshStartGate() {
        autoRefreshStartGate.compareAndSet(false, true);
    }

    public boolean isAutoRefresh() {
        return Strategy.AutoRefresh == updateStrategy;
    }

    boolean couldAutoRefreshStart() {
        return autoRefreshStartGate.get();
    }

    long getAutoRefreshInterval() {
        return autoRefreshInterval;
    }

}
