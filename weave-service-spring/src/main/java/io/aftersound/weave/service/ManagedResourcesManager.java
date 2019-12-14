package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.resource.ManagedResources;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.resource.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

public class ManagedResourcesManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedResourcesManager.class);

    private final ObjectMapper resourceConfigReader;
    private final Path metadataDirectory;
    private final ManagedResources managedResources;
    private final ActorRegistry<ResourceManager> resourceManagerRegistry;

    public ManagedResourcesManager(
            ObjectMapper resourceConfigReader,
            Path metadataDirectory,
            ManagedResources managedResources,
            ActorRegistry<ResourceManager> resourceManagerRegistry) {
        this.resourceConfigReader = resourceConfigReader;
        this.metadataDirectory = metadataDirectory;
        this.managedResources = managedResources;
        this.resourceManagerRegistry = resourceManagerRegistry;
    }

    public void init() {
        List<ResourceConfig> resourceConfigs = new ManagedResourceConfigLoader(
                resourceConfigReader,
                metadataDirectory
        ).load();

        for (ResourceConfig resourceConfig : resourceConfigs) {
            ResourceManager resourceManager = resourceManagerRegistry.get(resourceConfig.getType());
            try {
                resourceManager.initializeResources(managedResources, resourceConfig);
            } catch (Exception e) {
                LOGGER.error(
                        "Exception occurred on initialize resource ({}, {}, {})",
                        resourceConfig.getClass().getName(),
                        resourceConfig.getType(),
                        resourceConfig.getName(),
                        e);
            }
        }
    }

    public void shutdown() {
    }
}
