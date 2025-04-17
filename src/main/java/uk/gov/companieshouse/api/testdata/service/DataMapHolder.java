package uk.gov.companieshouse.api.testdata.service;

import java.util.Map;
import uk.gov.companieshouse.logging.util.DataMap.Builder;

public class DataMapHolder {
    private static final ThreadLocal<Builder> DATAMAP_BUILDER
            = ThreadLocal.withInitial(() -> new Builder().requestId("uninitialised"));

    public static void initialise(String requestId) {
        DATAMAP_BUILDER.get().requestId(requestId);
    }

    private DataMapHolder() {}

    public static void clear() {
        DATAMAP_BUILDER.remove();
    }

    public static Builder get() {
        return DATAMAP_BUILDER.get();
    }

    /**
     * Get the Log Map from the Builder.
     * @return Map
     */
    public static Map<String, Object> getLogMap() {
        return DATAMAP_BUILDER.get()
                .build()
                .getLogMap();
    }

    public static String getRequestId() {
        return (String) getLogMap().get("request_id");
    }
}
