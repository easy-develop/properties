package com.easy.properties.data;

public class Var {

    public static final String RESOURCE_DIR = "src/test/resources/";
    public static final String SIMPLE_PROPS = RESOURCE_DIR + "propsSimple.properties";
    public static final String NON_EXISTENT_PROPS = RESOURCE_DIR + "nonExistentFile.properties";
    public static final String INVALID_PROPS = RESOURCE_DIR + "propsInvalid.properties";
    public static final String COMMENTED_PROPS = RESOURCE_DIR + "propsWithComments.properties";
    public static final String SUBSTITUTED_VAR_PROPS = RESOURCE_DIR + "propsWithVariableSubstituition.properties";
    public static final String MULTI_SUBSTITUTED_VAR_PROPS = RESOURCE_DIR + "propsWithMultipleVariableSubstituition.properties";
    public static final String MULTILINED_VAR_PROPS = RESOURCE_DIR + "propsWithMultiline.properties";
    public static final String UNKNOWN_KEY_VAR_PROPS = RESOURCE_DIR + "propsWithUnknownKey.properties";
    public static final String WITHOUT_CURLY_BRACE_VAR_PROPS = RESOURCE_DIR + "propsWithVariableWithoutCurlyBrace.properties";
    public static final String WITH_DIFFERENT_KEYNAME_VAR_PROPS = RESOURCE_DIR + "propsWithDiffKeyName.properties";
    public static final String WITH_NON_STRING_VALS_PROPS = RESOURCE_DIR + "propsWithNonStringValues.properties";
    public static final String WITH_SINGLE_LETTER_KEYS_PROPS = RESOURCE_DIR + "propsWithSingleLetterKey.properties";
    public static final String WITH_CYCLIC_DEPENDENCY_PROPS = RESOURCE_DIR + "propsWithCyclicDependency.properties";
    public static final String WITH_SYSTEM_PROPERTY_AS_VARIABLE_PROPS = RESOURCE_DIR + "propsWithSysPropsAsVariable.properties";
    public static final String WITH_ENV_AS_VARIABLE_PROPS = RESOURCE_DIR + "propsWithEnvAsVariable.properties";

    private Var() {
    }
}
