package com.easy.properties.enums;

public enum MyPropsWithDefaultValue {
    HOME("/home"),
    LOGS("/tmp"),
    PRIIVILEGE_LEVEL("10");
    
    private final String defaultValue;

    private MyPropsWithDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}