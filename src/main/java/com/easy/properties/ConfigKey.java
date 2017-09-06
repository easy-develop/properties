package com.easy.properties;

import com.easy.properties.exception.EnumMissingOptionalFieldException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This thread-safe class acts as a wrapper to the Enum having keys for configuration properties, to achieve customized behavior of Enum <br>
 * Corresponding Enum can have below optional fields for this purpose (of behavior customization):
 * <ul>
 *    <li>{@code String keyName}</li>
 *    <li>{@code boolean mandatory}</li>
 *    <li>{@code String defaultValue}</li>
 * </ul>
 * <i><b>Note: </b>A getter method following Java naming conventions should also be available for the customization to work</i>
 * <br>
 * This means that following methods should also be defined and accessible
 * <ul>
 *    <li>{@code String getKeyName()}</li>
 *    <li>{@code boolean isMandatory()}</li>
 *    <li>{@code String getDefaultValue()}</li>
 * </ul>
 * If {@code keyName} field is not defined, name of the Enum constant is considered as the key name <br>
 * Two instances with same key-name are considered to be equal <br>
 * 
 * @author himanshu_shekhar
 */
public class ConfigKey {
    private static final Logger logger = LoggerFactory.getLogger(ConfigKey.class);
    
    private static final String FIELD_KEYNAME = "keyName";
    private static final String FIELD_MANDATORY = "mandatory";
    private static final String FIELD_DEFAULT_VALUE = "defaultValue";
    
    private final String keyName;
    private final boolean mandatory;
    private final String defaultValue;
    
    /**
     * Depending upon the fields defined in specified Enum, {@code keyName, mandatory and defaultValue} will be updated
     * @param enumConstant The enum constant for which configuration property is to be obtained
     */
    public ConfigKey(Object enumConstant){
        logger.trace("Constructing instance with {}", enumConstant.getClass().getName());
        this.keyName = getKeyName(enumConstant);
        this.mandatory = isMandatory(enumConstant);
        this.defaultValue = getDefaultValue(enumConstant);
    }
    
    /**
     * Values other than {@code keyName} ({@code mandatory and defaultValue}) will be set to their default values
     * @param keyName The key name corresponding to enum constant
     */
    public ConfigKey(String keyName){
        logger.trace("Constructing instance with {}", keyName);
        this.keyName = keyName;
        this.mandatory = false;
        this.defaultValue = "";
    }
    
    /*
    If keyName is defined in Enum then use its value, else use the name() on Enum
    */
    private static String getKeyName(Object enumConstant){
        String keyName;
        try{
            keyName = (String) getFieldValue(enumConstant, FIELD_KEYNAME);
        }catch(EnumMissingOptionalFieldException e){
            logger.debug("Cannot find keyName field for " + enumConstant.getClass().getName(), e);
            keyName = ((Enum) enumConstant).name();
        }
        
        logger.trace("Determined keyName for {} to be {}", enumConstant, keyName);
        
        return keyName;
    }
    
    /*
    If mandatory is defined in Enum then use it, else use false
    */
    private static boolean isMandatory(Object enumConstant){
        boolean mandatory = false;
        try{
            mandatory = Boolean.parseBoolean(getFieldValue(enumConstant, FIELD_MANDATORY).toString());
        }catch(EnumMissingOptionalFieldException e){
            logger.debug("Cannot find mandatory field for " + enumConstant.getClass().getName(), e);
        }
        
        logger.trace("Determined mandatory for {} to be {}", enumConstant, mandatory);
        
        return mandatory;
    }
    
    /*
    If defaultValue is defined in Enum, else use empty string
    */
    private static String getDefaultValue(Object enumConstant){
        String defaultValue = "";
        try{
            defaultValue = (String) getFieldValue(enumConstant, FIELD_DEFAULT_VALUE);
        }catch(EnumMissingOptionalFieldException e){
            logger.debug("Cannot find default value field for " + enumConstant.getClass().getName(), e);
        }
        
        logger.trace("Determined default value for {} to be {}", enumConstant, defaultValue);
        
        return defaultValue;
    }
    
    /*
    The fieldName must be either of keyName, mandatory or defaultValue (as only these fields are supported as of now), otherwise
    EnumMissingOptionalFieldException will be thrown
    */
    private static Object getFieldValue(Object enumConstant, String fieldName){
        return new EnumFieldInfo(enumConstant, fieldName).getFieldValue();
    }

    /**
     * @return Key name to be used in the configuration property file
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * @return If corresponding key is mandatory and must be present in the configuration property file
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * @return Default value to be used if configuration property file does not contain value for corresponding key
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.keyName != null ? this.keyName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConfigKey other = (ConfigKey) obj;
        if ((this.keyName == null) ? (other.keyName != null) : !this.keyName.equals(other.keyName)) {
            return false;
        }
        return true;
    }
}