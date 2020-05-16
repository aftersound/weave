package io.aftersound.weave.filehandler;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.client.ClientRegistry;

import java.lang.reflect.Constructor;

public final class FileHandlerFactory {

    private final ClientRegistry clientRegistry;
    private final ActorBindings<FileHandlingControl, FileHandler<?, FileHandlingControl>, Object> fileHandlerBindings;
    private final ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory;

    public FileHandlerFactory(
            ClientRegistry clientRegistry,
            ActorBindings<FileHandlingControl, FileHandler<?, FileHandlingControl>, Object> fileHandlerBindings,
            ActorBindings<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterBindings) {
        this.clientRegistry = clientRegistry;
        this.fileHandlerBindings = fileHandlerBindings;
        this.fileFilterFactory = new ActorFactory<>(fileFilterBindings);
    }

    public <CLIENT, CONTROL extends FileHandlingControl> FileHandler<CLIENT, CONTROL> getFileHandler(
            CONTROL control) throws Exception {
        if (control == null || control.getType() == null) {
            throw new IllegalArgumentException("FileHandleControl cannot be null or missing type information");
        }

        Class<? extends FileHandler> fileHandlerType = fileHandlerBindings.getActorType(control.getType());
        if (fileHandlerType != null) {
            Constructor<? extends FileHandler> constructor = fileHandlerType.getDeclaredConstructor(
                    ClientRegistry.class,
                    ActorFactory.class,
                    control.getClass());
            constructor.setAccessible(true);
            return constructor.newInstance(clientRegistry, fileFilterFactory, control);
        } else {
            return null;
        }
    }

}
