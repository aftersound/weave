package io.aftersound.weave.client.cr;

import java.io.InputStream;
import java.net.URL;

public class ClassResource {

    private final Class<?> cls;
    private final String baseDirectory;

    public ClassResource(Class<?> cls, String baseDirectory) {
        this.cls = cls;
        this.baseDirectory = baseDirectory;
    }

    public InputStream getAsStream(String resourceName) {
        if (baseDirectory != null) {
            return cls.getResourceAsStream(baseDirectory + "/" + resourceName);
        } else {
            return cls.getResourceAsStream(resourceName);
        }
    }

    public URL getAsURL(String resourceName) {
        if (baseDirectory != null) {
            return cls.getResource(baseDirectory + "/" + resourceName);
        } else {
            return cls.getResource(resourceName);
        }
    }

}
