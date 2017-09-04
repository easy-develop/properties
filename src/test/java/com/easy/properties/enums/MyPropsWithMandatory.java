package com.easy.properties.enums;

public enum MyPropsWithMandatory {
    HOME(true),
    LOGS(false);
    
    private final boolean mandatory;

    private MyPropsWithMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}