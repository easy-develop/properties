package com.easy.properties.enums;

public enum MyPropsWithAllOptionalFieldsPresent {
    NO_VAL_YET("my.not.yet", false, "SomethingInMy:${my.home}"),
    HOME("my.home", true, "/home"),
    CONF("my.conf", true, "/home/conf"),
    LOGS("my.logs", false, "/tmp"),
    UTIL_HOME("my.util", false, "${my.home}/utils"),
    PRIVILEGE_LEVE("my.privilege", false, "10");
    
    private final String keyName;
    private final boolean mandatory;
    private final String defaultValue;
    
    private MyPropsWithAllOptionalFieldsPresent(String keyName, boolean mandatory, String defaultValue){
        this.keyName = keyName;
        this.mandatory = mandatory;
        this.defaultValue = defaultValue;
    }

    public String getKeyName() {
        return keyName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public String getDefaultValue() {
        return defaultValue;
    }    
}