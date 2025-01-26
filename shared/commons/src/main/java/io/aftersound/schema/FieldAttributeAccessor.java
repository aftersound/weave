package io.aftersound.schema;

import io.aftersound.dict.AttributeAccessor;

import java.util.Map;

public class FieldAttributeAccessor implements AttributeAccessor<Field> {

    public static final AttributeAccessor<Field> INSTANCE = new FieldAttributeAccessor();

    @SuppressWarnings("unchecked")
    @Override
    public <ATTR> ATTR get(Field field, String name) {
        switch (name) {
            case "description": {
                return (ATTR) field.getDescription();
            }
            case "friendlyName": {
                return (ATTR) field.getFriendlyName();
            }
            case "name": {
                return (ATTR) field.getName();
            }
            case "tags": {
                return (ATTR) field.getTags();
            }
            case "type": {
                return (ATTR) field.getType();
            }
            case "type.name":
            case "TYPE": {
                return (ATTR) (field.getType() != null ? field.getType().getName() : null);
            }
            case "type.options": {
                return (ATTR) (field.getType() != null ? field.getType().getOptions() : null);
            }
            default: {
                if (name.startsWith("tags.")) {
                    String key = name.substring("tags.".length());
                    Map<String, Object> tags = field.getTags();
                    return tags != null ? (ATTR) tags.get(key) : null;
                }

                if (name.startsWith("type.options.")) {
                    String key = name.substring("type.options.".length());
                    Map<String, Object> options = field.getType().getOptions();
                    return options != null ? (ATTR) options.get(key) : null;
                }

                return null;
            }
        }
    }

}
