package com.easy.properties.enums;

public enum MyPropsWithKeyNameAndIncorrectGetter {
    HOME("my.home");
    
    private final String keyName;
    
    private MyPropsWithKeyNameAndIncorrectGetter(String keyName){
        this.keyName = keyName;
    }

    // Name of this getter method is made intentionally incorrect. Otherwise, it should have been "getKeyName"
    public String getKeyname() {
        return keyName;
    }
}
