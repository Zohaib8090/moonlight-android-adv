package com.limelight.binding.input.customcontrols.utils;

import java.util.Map;

public class JSONUtils {
    public static String insertSingleJSONValue(String value, Map<String, String> keyValueMap) {
        if (value == null) {
            return "0";
        }
        String valueInserted = value;
        for (Map.Entry<String, String> keyValue : keyValueMap.entrySet()) {
            valueInserted = valueInserted.replace("${" + keyValue.getKey() + "}", keyValue.getValue() == null ? "" : keyValue.getValue());
        }
        return valueInserted;
    }
}
