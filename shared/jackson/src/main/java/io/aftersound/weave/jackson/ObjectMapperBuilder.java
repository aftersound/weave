package io.aftersound.weave.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class ObjectMapperBuilder {

    private final JsonFactory codecFactory;
    private final SimpleModule simpleModule;

    private ObjectMapperBuilder(JsonFactory codecFactory) {
        this.codecFactory = codecFactory;
        this.simpleModule = new SimpleModule();
    }

    public static ObjectMapperBuilder forJson() {
        return new ObjectMapperBuilder(new JsonFactory());
    }

    public static ObjectMapperBuilder forYAML() {
        /**
         * Intentionally use reflection to create YAML based ObjectMapper
         * to avoid the dependency on library 'jackson-dataformat-yaml'.
         * Enclosing application has the right to choose whether to support
         * YAML and decide to include the dependency accordingly.
         */
        final JsonFactory yamlFactory;
        try {
            Class<? extends JsonFactory> jsonFactoryClass =
                    (Class<? extends JsonFactory>) ObjectMapperBuilder.class.getClassLoader().loadClass(
                            "com.fasterxml.jackson.dataformat.yaml.YAMLFactory"
                    );
            yamlFactory= jsonFactoryClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create an instance of YAMLFactory", e);
        }
        return new ObjectMapperBuilder(yamlFactory);
    }

    public <BT> ObjectMapperBuilder with(BaseTypeDeserializer<BT> baseTypeDeserializer) {
        simpleModule.addDeserializer(baseTypeDeserializer.baseType(), baseTypeDeserializer);
        return this;
    }

    public ObjectMapper build() {
        return new ObjectMapper(codecFactory)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(simpleModule);
    }

}
