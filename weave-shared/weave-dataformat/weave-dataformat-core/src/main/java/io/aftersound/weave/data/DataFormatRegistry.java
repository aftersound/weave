package io.aftersound.weave.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataFormatRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFormatRegistry.class);

    private final Map<String, DataFormat> dataFormatByType = new HashMap<>();

    public void register(DataFormat dataFormat) {
        dataFormatByType.put(dataFormat.getType(), dataFormat);
    }

    public DataFormat getDataFormat(String type) {
        return dataFormatByType.get(type);
    }

    public DataFormatRegistry initialize(Collection<Class<? extends DataFormat>> dataFormatTypes) {
        for (Class<? extends DataFormat> type : dataFormatTypes) {
            try {
                DataFormat dataFormat = type.getDeclaredConstructor().newInstance();
                this.register(dataFormat);
            } catch (Exception e) {
                LOGGER.error("{} exception occured when trying to create instance of {}", e, type.getName());
            }
        }
        return this;
    }
}
