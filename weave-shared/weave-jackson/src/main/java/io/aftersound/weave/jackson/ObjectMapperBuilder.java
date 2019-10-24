package io.aftersound.weave.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
        return new ObjectMapperBuilder(new YAMLFactory());
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
