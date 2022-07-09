package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ValueFuncDescriptorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValueFuncDescriptorHelper.class);

    private static Object mapper;

    public static List<Descriptor> getDescriptors(Class<? extends ValueFuncFactory> valueFuncFactoryClass) {
        if (valueFuncFactoryClass == null) {
            return Collections.emptyList();
        }

        final String jsonResource = "/META-INF/weave/" + valueFuncFactoryClass.getSimpleName() + ".json";

        try (InputStream is = valueFuncFactoryClass.getResourceAsStream(jsonResource)) {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = (com.fasterxml.jackson.databind.ObjectMapper) getMapper();
            Descriptor[] descriptors = objectMapper.readValue(is, Descriptor[].class);
            return Arrays.asList(descriptors);
        } catch (IOException e) {
            LOGGER.error("Failed to get resource at class path '{}'", jsonResource, e);
            return Collections.emptyList();
        } catch (Throwable t) {
            LOGGER.error("Failed to read Descriptor(s) from resource at class path '{}'", jsonResource, t);
            return Collections.emptyList();
        }
    }

    private static Object getMapper() {
        if (mapper == null) {
            mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        }
        return mapper;
    }
}
