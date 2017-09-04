package com.easy.properties.enums;

public enum MyPropsWithKeyName {
    HOME("my.home"),
    CONF("my.conf");
    
    private final String keyName;
    
    private MyPropsWithKeyName(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
