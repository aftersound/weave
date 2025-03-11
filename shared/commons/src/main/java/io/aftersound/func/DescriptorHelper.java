package io.aftersound.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DescriptorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DescriptorHelper.class);

    private static final Mapper MAPPER = new Mapper();

    public static List<Descriptor> getDescriptors(Class<? extends FuncFactory> funcFactoryClass) {
        if (funcFactoryClass == null) {
            return Collections.emptyList();
        }

        final String jsonResource = funcFactoryClass.getSimpleName() + ".json";
        try (InputStream is = funcFactoryClass.getResourceAsStream(jsonResource)) {
            Descriptor[] descriptors = MAPPER.readValue(is, Descriptor[].class);
            return Arrays.asList(descriptors);
        } catch (Throwable t) {
            LOGGER.error("Failed to read Descriptor(s) from resource at class path '{}' of {}", jsonResource, funcFactoryClass.getName(), t);
            return Collections.emptyList();
        }
    }

    private static class Mapper {

        private static Object delegate;

        <T> T readValue(InputStream src, Class<T> valueType) throws Exception{
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = (com.fasterxml.jackson.databind.ObjectMapper) getDelegate();
            return objectMapper.readValue(src, valueType);
        }

        private static Object getDelegate() {
            if (delegate == null) {
                delegate = new com.fasterxml.jackson.databind.ObjectMapper();
            }
            return delegate;
        }

    }


}
