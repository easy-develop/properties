package com.easy.properties;

import com.easy.properties.data.Var;
import com.easy.properties.enums.MyPropsSimple;
import com.easy.properties.enums.MyPropsWithAllOptionalFieldsPresent;
import com.easy.properties.enums.MyPropsWithDefaultValue;
import com.easy.properties.enums.MyPropsWithKeyName;
import com.easy.properties.enums.MyPropsWithMandatory;
import com.easy.properties.enums.MyPropsWithNonStringVals;
import com.easy.properties.exception.InvalidConfigException;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class PropertiesTest {
    private static final String TEST_HOME_VAL = "/home/test";
    @Test
    public void readsConfigValueSimple(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.SIMPLE_PROPS, MyPropsSimple.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot read value from simple config", TEST_HOME_VAL, props.get(MyPropsSimple.HOME));
    }
    
    @Test
    public void readsConfigValueSubstituted(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.SUBSTITUTED_VAR_PROPS, MyPropsSimple.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get variable substituted value from configuration", TEST_HOME_VAL + "/bin", props.get(MyPropsSimple.BIN_DIR));
    }
    
    @Test
    public void readsConfigValueMultiSubstituted(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.MULTI_SUBSTITUTED_VAR_PROPS, MyPropsSimple.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get multi-substituted value from configuration", TEST_HOME_VAL + "/bin/dump.log", props.get(MyPropsSimple.DUMP_FILE));
    }
    
    @Test
    public void readsMultilinedConfigValue(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.MULTILINED_VAR_PROPS, MyPropsSimple.class);
        Properties props = propsLoader.load();
        StringBuilder expected = new StringBuilder();
        expected.append("This is a test").append("\n").
                append("properties file, and has multiple").append("\n").
                append("lines in it");
        assertEquals("Cannot get multilined value from configuration", expected.toString(), props.get(MyPropsSimple.DESCRIPTION));
    }
    
    @Test
    public void readsConfigValueWithoutCurlyBrace(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITHOUT_CURLY_BRACE_VAR_PROPS, MyPropsSimple.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get without-curly-brace variable value from configguration", TEST_HOME_VAL + "/bin", props.get(MyPropsSimple.BIN_DIR));
    }
    
    @Test(expected = InvalidConfigException.class)
    public void invalidConfigIsThrownIfUnknownKeyInProps(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.UNKNOWN_KEY_VAR_PROPS, MyPropsSimple.class);
        propsLoader.load();
    }
    
    @Test
    public void readsConfigValueWithKeyNameInEnum(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_DIFFERENT_KEYNAME_VAR_PROPS, MyPropsWithKeyName.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get config value for enum with key-name", TEST_HOME_VAL + "/conf", props.get(MyPropsWithKeyName.CONF));
    }
    
    @Test
    public void readsConfigValueWithDefaultValueInEnum(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.SIMPLE_PROPS, MyPropsWithDefaultValue.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get config with default value", TEST_HOME_VAL, props.get(MyPropsWithDefaultValue.HOME));
        assertEquals("Cannot get config with default value", "/tmp", props.get(MyPropsWithDefaultValue.LOGS));
    }
    
    @Test
    public void readsConfigIfOptionalIsMissing(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.SIMPLE_PROPS, MyPropsWithMandatory.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get config with optional key missing", TEST_HOME_VAL, props.get(MyPropsWithMandatory.HOME));
    }
    
    @Test
    public void readsDefaultValueIfOptionalIsMissing(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_DIFFERENT_KEYNAME_VAR_PROPS, MyPropsWithAllOptionalFieldsPresent.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get config if optional key is missing", "/tmp", props.get(MyPropsWithAllOptionalFieldsPresent.LOGS));
    }
    
    @Test
    public void readsConfigIfVariableInDefaultValue(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_DIFFERENT_KEYNAME_VAR_PROPS, MyPropsWithAllOptionalFieldsPresent.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot do variable substituition in default value", TEST_HOME_VAL + "/utils", 
                props.get(MyPropsWithAllOptionalFieldsPresent.UTIL_HOME));
    }
    
    @Test
    public void readsConfigWithVariableIndependentOfOrder(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_DIFFERENT_KEYNAME_VAR_PROPS, MyPropsWithAllOptionalFieldsPresent.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get config with out-of-order variable declaration", "SomethingInMy:/home/test", 
                props.get(MyPropsWithAllOptionalFieldsPresent.NO_VAL_YET));
    }
    
    @Test
    public void readsListOfIntegersInConfig(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        List<Integer> expected = Arrays.asList(new Integer[]{10, 43, 89, 7});
        assertEquals("Cannot get list of integers from config", expected, props.getList(MyPropsWithNonStringVals.INTS_VALID, int.class));
    }
    
    @Test
    public void readsListOfFloatsInConfig(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        List<Float> expected = Arrays.asList(new Float[]{3.89f, 1.45f, 9.0f, 10f});
        assertEquals("Cannot get list of floats from config", expected, props.getList(MyPropsWithNonStringVals.FLOATS_VALID, float.class));
    }
    
    @Test
    public void readsListOfStrings(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        List<String> expected = Arrays.asList(new String[]{"apple", "ball", "cat"});
        assertEquals("Cannot get list of strings from config", expected, props.getList(MyPropsWithNonStringVals.STRINGS, String.class));
    }
    
    @Test
    public void readsListOfValuesWithDifferentDelimiterInConfig(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        List<Integer> expected = Arrays.asList(new Integer[]{10, 43, 89, 7});
        assertEquals("Cannot get list of integers with different delimiter from config", expected, 
                props.getList(MyPropsWithNonStringVals.INTS_VALID_WITH_DIFFERENT_DELIMITER, Integer.class, ":"));
    }
    
    @Test
    public void readsListOfValuesWithSpecialCharacterAsDelimiterInConfig(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        List<Integer> expected = Arrays.asList(new Integer[]{10, 43, 89, 7});
        assertEquals("Cannot get list of integers with different delimiter from config", expected, 
                props.getList(MyPropsWithNonStringVals.INTS_VALID_WITH_SPECIAL_CHARACTER_DELIMITER, Integer.class, "|"));
    }
    
    @Test(expected = InvalidConfigException.class)
    public void invalidConfigIsThrownIfConfigListHasIllegalValue(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        props.getList(MyPropsWithNonStringVals.INTS_INVALID, int.class);
    }
    
    @Test(expected = InvalidConfigException.class)
    public void invalidConfigIsThrownIfIncorrectDataTypeSpecified(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        props.getList(MyPropsWithNonStringVals.FLOATS_VALID, int.class);
    }
    
    @Test
    public void readsOtherDataTypesInConfig(){
        PropertiesLoader propsLoader = new PropertiesLoader(Var.WITH_NON_STRING_VALS_PROPS, MyPropsWithNonStringVals.class);
        Properties props = propsLoader.load();
        assertEquals("Cannot get char value from config", 'X', props.getChar(MyPropsWithNonStringVals.CHAR_VAL));
        assertEquals("Cannot get byte value from config", (byte)45, props.getByte(MyPropsWithNonStringVals.BYTE_VAL));
        assertEquals("Cannot get boolean value from config", true, props.getBoolean(MyPropsWithNonStringVals.BOOLEAN_VAL));
        assertEquals("Cannot get double value from config", 8182.99123, props.getDouble(MyPropsWithNonStringVals.DOUBLE_VAL), 0.001);
        assertEquals("Cannot get long value from config", 991923919918L, props.getLong(MyPropsWithNonStringVals.LONG_VAL));
    }
}