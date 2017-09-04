package com.easy.properties;

import com.easy.properties.enums.MyPropsSimple;
import com.easy.properties.enums.MyPropsWithDefaultValue;
import com.easy.properties.enums.MyPropsWithKeyName;
import com.easy.properties.enums.MyPropsWithMandatory;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigKeyTest {

    @Test
    public void setsKeyNameCorrectly() {
        ConfigKey configKey = new ConfigKey(MyPropsWithKeyName.HOME);
        assertEquals("Key name is not set correctly", "my.home", configKey.getKeyName());
    }

    @Test
    public void usesConstantNameIfUserDefinedKeyNameNotSpecified() {
        ConfigKey configKey = new ConfigKey(MyPropsSimple.HOME);
        assertEquals("Key name is not set if user defined key is not specified", "HOME", configKey.getKeyName());
    }

    @Test
    public void setsIsMandatoryCorrectly() {
        ConfigKey configKey = new ConfigKey(MyPropsWithMandatory.HOME);
        assertEquals("Mandatory flag is not set if user defined flag is not specified", true, configKey.isMandatory());
    }

    @Test
    public void setsDefaultValueCorrectly() {
        ConfigKey configKey = new ConfigKey(MyPropsWithDefaultValue.HOME);
        assertEquals("Default value is not set if user defined value is not specified", "/home", configKey.getDefaultValue());
    }
    
    @Test
    public void instanceWithSameKeyNameAreEqual(){
        ConfigKey configKeyFirst = new ConfigKey(MyPropsSimple.HOME);
        ConfigKey configKeySecond = new ConfigKey("HOME");
        assertEquals("Config keys with same key name are not equal", configKeyFirst, configKeySecond);
    }
}
