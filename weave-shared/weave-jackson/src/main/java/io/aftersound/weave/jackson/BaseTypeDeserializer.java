package io.aftersound.weave.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import io.aftersound.weave.common.NamedType;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * BaseTypeDeserializer de-serializes JSON into object of expected sub type
 *
 * @param <BT>
 *          - base type
 */
public final class BaseTypeDeserializer<BT> extends JsonDeserializer<BT> {

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final Class<BT> baseType;
    private final String typeVariableName;
    private final Map<String, Class<? extends BT>> subTypeByName;

    public BaseTypeDeserializer(Class<BT> baseType, String typeNameVariable, Collection<NamedType<BT>> namedSubTypes) {
        this.baseType = baseType;
        this.typeVariableName = typeNameVariable;
        this.subTypeByName = createSubTypeByNameMap(namedSubTypes);
    }

    @SuppressWarnings("unchecked")
    private static <BT> Map<String,Class<? extends BT>> createSubTypeByNameMap(Collection<NamedType<BT>> namedSubTypes) {
        Map<String, Class<? extends BT>> typeByName = new HashMap<>();
        for (NamedType namedType : namedSubTypes) {
            typeByName.put(namedType.name(), namedType.type());
        }
        return Collections.unmodifiableMap(typeByName);
    }

    Class<BT> baseType() {
        return baseType;
    }

    @Override
    public BT deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);

        String typeName = node.get(typeVariableName).asText();
        Class<? extends BT> subType = subTypeByName.get(typeName);

        return ((ObjectMapper)oc).readValue(MAPPER.writeValueAsString(node), subType);
    }

}
