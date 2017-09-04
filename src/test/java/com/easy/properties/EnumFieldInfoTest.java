package com.easy.properties;

import com.easy.properties.enums.MyPropsWithAllOptionalFieldsPresent;
import com.easy.properties.enums.MyPropsWithKeyNameAndIncorrectGetter;
import com.easy.properties.exception.EnumMissingOptionalFieldException;
import org.junit.Test;
import static org.junit.Assert.*;

public class EnumFieldInfoTest {
    
    @Test
    public void providesFieldValueFromEnum(){
        EnumFieldInfo enumFieldInfo = new EnumFieldInfo(MyPropsWithAllOptionalFieldsPresent.HOME, "keyName");
        assertEquals("Cannot get field value from enum", "my.home", (String)enumFieldInfo.getFieldValue());
    }
    
    @Test(expected = EnumMissingOptionalFieldException.class)
    public void enumMissingFieldExceptionIsThrownIfFieldNotPresent(){
        EnumFieldInfo enumFieldInfo = new EnumFieldInfo(MyPropsWithAllOptionalFieldsPresent.HOME, "nonExistent");
        enumFieldInfo.getFieldValue();
    }
    
    @Test(expected = EnumMissingOptionalFieldException.class)
    public void enumMissingFieldExceptionIsThrownIfIncorrectGetter(){
        EnumFieldInfo enumFieldInfo = new EnumFieldInfo(MyPropsWithKeyNameAndIncorrectGetter.HOME, "keyName");
        enumFieldInfo.getFieldValue();
    }
}
