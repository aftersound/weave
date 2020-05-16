package io.aftersound.weave.config;

import java.util.Map;

class FullNameParser extends ValueParser<FullName> {

    @Override
    public FullName parse(Map<String, String> rawValues) {
        String firstName = rawValues.get("first.name");
        String lastName = rawValues.get("last.name");
        if (firstName != null && lastName != null) {
            return FullName.from(firstName, lastName);
        } else {
            throw ConfigException.create("either first.name or last.name or both is not configured");
        }
    }

}
