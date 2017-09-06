package com.easy.properties;

import com.easy.properties.exception.InvalidConfigException;
import com.easy.properties.util.RegexUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This thread-safe class represents the configuration property and provides several methods for obtaining values defined in property file in various formats <br>
 * Some of the key points to note for this class are: <br>
 * <b>1) Supports below data types and methods to retrieve configuration value as particular data type</b>
 * <hr>
 * <ol>
 *    <li>{@code String}</li>
 *    <li>{@code short}</li>
 *    <li>{@code int}</li>
 *    <li>{@code long}</li>
 *    <li>{@code char}</li>
 *    <li>{@code byte}</li>
 *    <li>{@code boolean}</li>
 *    <li>{@code float}</li>
 *    <li>{@code double}</li>
 *    <li>List of any of the above types</li>
 * </ol>
 * <hr>
 * <br>
 * <b>2) The value can be multi-lined, for example below content in a property file is perfectly fine</b> <br>
 * <hr>
 * {@code HOME = /home/util_test} <br>
 * {@code DESCRIPTION = This utility is for providing fast and easy} <br>
 * {@code APIs to the end user for various purpose, and if you have more queries,} <br>
 * {@code please send a mail to: developers@test.com} <br>
 * {@code BIN = ${HOME}/bin} <br>
 * <hr>
 * 
 * <br>
 * 
 * <b>
 * 3) Value can contain variables defined through dollar sign, for example it is perfectly fine to have something like this in the property file <br>
 * </b>
 * <hr>
 * {@code HOME = /home/util_test} <br>
 * {@code BIN = $HOME/bin} <br>
 * {@code CONSOLE_FILE = ${BIN}/console.out} <br>
 * <hr>
 * The value of {@code CONSOLE_FILE} will resolve to {@code /home/util_test/bin/console.out} <br>
 * Note that enclosing the variable in curly braces is optional <br>
 * 
 * <br>
 * 
 * <b>4) If any problem like those mentioned below is encountered then an unchecked exception will be thrown</b>
 * <ol>
 *    <li>Mandatory key is missing from property file</li>
 *    <li>Format of value in configuration file is incorrect</li>
 * </ol>
 * 
 * @author himanshu_shekhar
 */
public class Properties{
    private static final Logger logger = LoggerFactory.getLogger(Properties.class);
    
    private final Map<ConfigKey, String> dataMap;

    Properties() {
        logger.trace("Constructing the instance");
        this.dataMap = new ConcurrentHashMap<ConfigKey, String>();
    }
    
    /*
    Just update the internal data structure for specified key with given value
    */
    void update(ConfigKey configKey, String value){
        logger.debug("Updating config key {} with value {}", configKey.getKeyName(), value);
        dataMap.put(configKey, value);
    }
    
    /*
    Validate that none of the mandatory keys is missing from the read loaded configuration
    */
    synchronized void validate(Collection<ConfigKey> configKeys){
        logger.debug("Validating the configuration keys");
        for(ConfigKey configKey : configKeys){
            if(configKey.isMandatory() && !dataMap.containsKey(configKey)){
                throw new InvalidConfigException("Missing mandatory configuration key (" + configKey.getKeyName() + ")");
            }
        }
    }
    
    /*
    Make the variable substitution on values defined in configuration property file. A variable can be defined using dollar($) sign. For example, 
    ${HOME} or $HOME. Note, that curly braces are optional, but often they make the configuration text more readable by enhancing the clarity.
    The substitution is performed recursively so that value is resolved correctly if value for a key itself contains another key
    */
    synchronized void makeSubstitutions(){
        logger.debug("Performing variable-substitutions");
        for(ConfigKey configKey : dataMap.keySet()){
            String substitutedValue = getSubstitutedValue(dataMap.get(configKey));
            logger.trace("Value after substitutions: {}", substitutedValue);
            dataMap.put(configKey, substitutedValue);
        }
    }
    
    /*
    Obtain the value with variable substituted as per key-values available in configuration
    Logic summary:
    1. Find patterns like ${VAR} or $VAR in the given text value
    2. For each of found occurrences, replace "${VAR}" or "$VAR" with value obtained from data-map using key VAR (or empty if no value present)
    */
    private String getSubstitutedValue(String value){
        logger.trace("Making variable substitution on ({})", value);
        String result = value;
        String regex = "(\\$\\{\\s*([a-zA-Z_.][a-zA-Z_.0-9]+)\\s*\\})|(\\$([a-zA-Z_.][a-zA-Z_.0-9]+))";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        while(matcher.find()){
            String varNameWithCurlyBrace = matcher.group(2);
            String varNameWithoutCurlyBrace = matcher.group(4);
            String varName = varNameWithCurlyBrace == null ? varNameWithoutCurlyBrace : varNameWithCurlyBrace;
            ConfigKey varNameKey = new ConfigKey(varName);
            // Here, we are making a recursive call. This is done in order to handle cases where the value also
            // has some key in it. For example, "home = /tmp", path="${home}/bin" and "conf = ${path}/../conf", then
            // we would need to have this recursive call in order to resolve ${conf} correctly
            String valueToReplace = dataMap.containsKey(varNameKey) ? getSubstitutedValue(dataMap.get(varNameKey)) : "";
            String varNameRegex = "\\$\\{\\s*" + varName + "\\s*\\}|\\$" + varName;
            logger.trace("Making substitution on ({}): replace ({}) by ({})", result, varNameRegex, valueToReplace);
            result = result.replaceAll(varNameRegex, valueToReplace);
        }
        
        return result;
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Value corresponding to specified configuration key
     */
    public String get(Enum key){
        logger.trace("Obtaining string value for: {}", key.name());
        ConfigKey configKey = new ConfigKey(key);
        String val = dataMap.get(configKey);
        if(val == null || val.isEmpty()){
            val = getSubstitutedValue(configKey.getDefaultValue());
        }
        return val;
    }
    
    /**
     * @param <T> The class representing type of elements of the list
     * @param key Enum key for which configuration property is needed
     * @param type Data type of elements of the list
     * @return List of elements separated by comma in the configuration property file
     */
    public <T> List<T> getList(Enum key, Class<T> type){
        return getList(key, type, ",");
    }
    
    /**
     * @param <T> The class representing type of elements of the list
     * @param key Enum key for which configuration property is needed
     * @param type Data type of elements of the list
     * @param delimiter Delimiter which separates the list elements
     * @return List of elements separated by specified delimiter in the configuration property file
     */
    public <T> List<T> getList(Enum key, Class<T> type, String delimiter){
        logger.trace("Obtaining list of type ({}) for ({})", type.getName(), key.name());
        String delimiterEscaped = RegexUtil.getSpecialCharactersEscaped(delimiter);
        String value = get(key);
        String[] vals = value.split(delimiterEscaped);
        
        List<T> list = new ArrayList<T>();
        for(String val : vals){
            val = val.trim();
            if(val.isEmpty()){
                continue;
            }
            
            try{
                list.add(getNonArrayValue(val.trim(), type));
            }catch(NumberFormatException e){
                throw new InvalidConfigException("Values for " + key + " cannot be converted to array of " + type.getName(), e);
            }catch(IllegalArgumentException e){
                throw new InvalidConfigException("Values for " + key + " cannot be converted to array of " + type.getName(), e);
            }
        }
        
        return list;
    }
    
    /*
    It just converts the string value to object of appropriate type. And, I know it is bit complex 
    (with higher cyclomatic complexity, if saying technically) and show issue with most of the code quality checking tools
    but I cannot think of any other way to reduce the complexity
    (I can think of using reflection to invoke the "parse" methods but that would be more complex than the if else blocks
    in my opinion)
    */
    private <T> T getNonArrayValue(String valueString, Class<T> type){
        logger.trace("Converting ({}) to type: {}", valueString, type.getName());
        Object value;
        
        if (type == String.class) {
            value = valueString;
        } else if (type == Byte.class || type == byte.class) {
            value = Byte.parseByte(valueString);
        } else if(type == Boolean.class || type == boolean.class){
            value = Boolean.parseBoolean(valueString);
        } else if (type == Character.class || type == char.class) {
            value = valueString.charAt(0);
        } else if (type == Short.class || type == short.class) {
            value = Short.parseShort(valueString);
        } else if (type == Integer.class || type == int.class) {
            value = Integer.parseInt(valueString);
        } else if (type == Long.class || type == long.class) {
            value = Long.parseLong(valueString);
        } else if (type == Float.class || type == float.class) {
            value = Float.parseFloat(valueString);
        } else if (type == Double.class || type == double.class) {
            value = Double.parseDouble(valueString);
        } else {
            throw new IllegalArgumentException(valueString + " cannot be converted to " + type.getName());
        }
        
        return (T)value;
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as integer
     * @throws InvalidConfigException If value specified in configuration file is not an integer
     */
    public int getInt(Enum key){
        logger.trace("Obtaining integer value for: {}", key.name());
        String val = get(key);
        try{
            return Integer.parseInt(val);
        }catch(NumberFormatException e){
            throw new InvalidConfigException("Is not integer, check configuration (" + val + ")", e);
        }
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as long
     * @throws InvalidConfigException If value specified in configuration file is not a long
     */
    public long getLong(Enum key){
        logger.trace("Obtaining long value for: {}", key.name());
        String val = get(key);
        try{
            return Long.parseLong(val);
        }catch(NumberFormatException e){
            throw new InvalidConfigException("Is not long, check configuration (" + val + ")", e);
        }
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as float
     * @throws InvalidConfigException If value specified in configuration file is not a float
     */
    public float getFloat(Enum key){
        logger.trace("Obtaining float value for: {}", key.name());
        String val = get(key);
        try{
            return Float.parseFloat(val);
        }catch(NumberFormatException e){
            throw new InvalidConfigException("Is not float, check configuration (" + val + ")", e);
        }
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as double
     * @throws InvalidConfigException If value specified in configuration file is not a double
     */
    public double getDouble(Enum key){
        logger.trace("Obtaining double value for: {}", key.name());
        String val = get(key);
        try{
            return Double.parseDouble(val);
        }catch(NumberFormatException e){
            throw new InvalidConfigException("Is not double, check configuration (" + val + ")", e);
        }
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as short
     * @throws InvalidConfigException If value specified in configuration file is not a short
     */
    public short getShort(Enum key){
        logger.trace("Obtaining short value for: {}", key.name());
        String val = get(key);
        try{
            return Short.parseShort(val);
        }catch(NumberFormatException e){
            throw new InvalidConfigException("Is not double, check configuration (" + val + ")", e);
        }
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as character
     * @throws InvalidConfigException If value specified in configuration file is not a character
     */
    public char getChar(Enum key){
        logger.trace("Obtaining char value for: {}", key.name());
        String val = get(key);
        if(val.length() != 1){
            throw new InvalidConfigException("Is not character, check configuration (" + val + ")");
        }
        return val.charAt(0);
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as byte
     * @throws InvalidConfigException If value specified in configuration file is not a byte
     */
    public byte getByte(Enum key){
        logger.trace("Obtaining byte value for: {}", key.name());
        String val = get(key);
        try{
            return Byte.parseByte(val);
        }catch(NumberFormatException e){
            throw new InvalidConfigException("Is not byte, check configuration (" + val + ")", e);
        }
    }
    
    /**
     * @param key Enum key for which configuration property is needed
     * @return Configuration property value as boolean
     * @throws InvalidConfigException If value specified in configuration file is not a boolean
     */
    public boolean getBoolean(Enum key){
        logger.trace("Obtaining boolean value for: {}", key.name());
        String val = get(key);
        if(val.isEmpty()){
            throw new InvalidConfigException("Is not boolean, check configuration (" + val + ")");
        }
        return Boolean.parseBoolean(val);
    }
}