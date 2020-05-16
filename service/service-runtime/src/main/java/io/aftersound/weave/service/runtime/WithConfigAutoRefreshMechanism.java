package io.aftersound.weave.service.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

abstract class WithConfigAutoRefreshMechanism {

    private static final Logger LOGGER = LoggerFactory.getLogger(WithConfigAutoRefreshMechanism.class);

    private static final boolean TOLERATE_EXCEPTION = true;

    private final ExecutorService configPollThread = Executors.newSingleThreadExecutor();
    private final AtomicBoolean autoRefreshLoopStarted = new AtomicBoolean(false);
    private final AtomicBoolean autoRefreshLoopShutdown = new AtomicBoolean(false);

    private final ConfigUpdateStrategy configUpdateStrategy;

    protected WithConfigAutoRefreshMechanism(ConfigUpdateStrategy configUpdateStrategy) {
        this.configUpdateStrategy = configUpdateStrategy;

        if (!configUpdateStrategy.isAutoRefresh()) {
            // don't even give a chance to polling loop to start
            autoRefreshLoopShutdown.set(true);
        }
    }

    abstract void loadConfigs(boolean tolerateException);

    final void init() {
        if (!configUpdateStrategy.isAutoRefresh()) {
            return;
        }

        if (!autoRefreshLoopStarted.get()) {
            // kick off data client config polling thread
            configPollThread.submit(this::startConfigAutoRefreshLoop);
            autoRefreshLoopStarted.set(true);
        }
    }

    /**
     * Shut down and clean up
     */
    final void shutdown() {
        // stop data client config polling thread
        autoRefreshLoopShutdown.compareAndSet(false, true);
    }

    /**
     * Start the loop which periodically loads configs from provider
     */
    private void startConfigAutoRefreshLoop() {
        while (!configUpdateStrategy.couldAutoRefreshStart()) {
            try {
                LOGGER.info("Wait 2 seconds for open of auto refresh start gate");
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                LOGGER.warn("Wait for open of auto refresh start gate is interrupted", e); // not expected
                break;
            }
        }

        LOGGER.info("Start to automatically refresh config every {} seconds", configUpdateStrategy.getAutoRefreshInterval() / 1000);

        try {
            while (!autoRefreshLoopShutdown.get()) {
                try {
                    loadConfigs(TOLERATE_EXCEPTION);
                    Thread.sleep(configUpdateStrategy.getAutoRefreshInterval());
                } catch (InterruptedException e) {
                    LOGGER.warn("Config polling thread is interrupted", e); // not expected
                    break;
                }
            }
            LOGGER.info("Exit from client config polling");
        } catch (Exception e) {
            // Not recoverable
            LOGGER.error("Exception occurred while polling, reading and applying config from provider", e);
        }
    }

}
