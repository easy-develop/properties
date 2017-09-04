package com.easy.properties;

import com.easy.properties.exception.InvalidConfigException;
import com.easy.properties.exception.InvalidEnumException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a thread-safe class used for reading given configuration property file and load the properties in memory based on specified enumeration<br>
 * The specified property file must have key and value pairs, separated by "=" <br>
 * <b>Note: The whitespace surrounding the delimiter is optional and is ignored while determining the values, for example {@code "Role=Developer"} and {@code "Role = Developer"} are equivalent
 * </b>
 * 
 * <br>
 * The lines in property file starting with {@code #} will be treated as comments and will be ignored
 * 
 * <br>
 * This class provides the object which can be used to obtain the values present in property file for specified key in enum
 * 
 * @author himanshu_shekhar
 */
public class PropertiesLoader {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final File configFile;
    private final Class<?> keyEnumClass;
    private final Map<String, ConfigKey> configKeyMap;
    private final Properties properties;

    private String previousKey;
    private StringBuilder previousValue = new StringBuilder();

    /**
     * 
     * @param configFilePath The configuration property file
     * @param keyEnumClass The enum class defining the keys to look for in configuration property file
     */
    public PropertiesLoader(String configFilePath, Class<? extends Enum> keyEnumClass) {
        logger.trace("Constructing instance for config file: {}, enum: {}", configFilePath, keyEnumClass.getName());
        
        this.configFile = new File(configFilePath);
        this.keyEnumClass = keyEnumClass;
        this.configKeyMap = new ConcurrentHashMap<String, ConfigKey>();
        this.properties = new Properties();
    }

    /**
     * Reads the given configuration property file and initializes Properties object containing values based on specified enum
     * 
     * @return Properties instance which can be used to get the values present in configuration property file
     * @throws InvalidEnumException If specified enum has no value defined in it
     * @throws InvalidConfigException If specified configuration file cannot be accessed or has some key which is not defined in given enum
     */
    public Properties load() {
        initializeConfigKeys();
        readConfigFile();
        
        properties.validate(configKeyMap.values());
        properties.makeSubstitutions();
        
        return properties;
    }

    /*
    Update the map for keeping track of config keys present in the specified enum class
    */
    private void initializeConfigKeys() {
        configKeyMap.clear();
        for (Object enumConstant : keyEnumClass.getEnumConstants()) {
            ConfigKey configKey = new ConfigKey(enumConstant);
            configKeyMap.put(configKey.getKeyName(), configKey);
        }
        if (configKeyMap.isEmpty()) {
            throw new InvalidEnumException("Enum " + keyEnumClass.getName() + " does not have any constant defined");
        }
    }

    /*
    Read the configuration property line-by-line and update the Properties instance with read values
    */
    private void readConfigFile() {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(configFile);
            BufferedReader br = new BufferedReader(fileReader);
            String line;
            while ((line = br.readLine()) != null) {
                parseLine(line.trim());
            }
            // Since multi-lined values are supported, values are first accumulated till next "KEY = VALUE" is found
            // So, the last "KEY = VALUE" is not processed through line-by-line parsing. That's why an extra call to
            // config-updating method is made so that last KEY and VALUE also gets processed and configuration is updated
            updateConfigSoFar();
        } catch (FileNotFoundException e) {
            throw new InvalidConfigException("Config file not found (" + configFile.getAbsolutePath() + ")", e);
        } catch (IOException e) {
            throw new InvalidConfigException("Cannot read from config file " + configFile.getAbsolutePath(), e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    logger.warn("Cannot close the reader for configuration file " + configFile.getAbsolutePath(), e);
                }
            }
        }
    }

    /*
    Logic summary:
    1. Ignore if line is empty or is a comment line (i.e. starts with "#")
    2. If line does not contain "=", it must be part of multi-lined VALUE for previous KEY, so record this line into VALUE gathered for previous KEY
    3. If "=" is present, invoke method for extracting key-value pair and updating value gathered for last configuration
    */
    private void parseLine(String line) {
        if (isCommentOrBlank(line)) {
            logger.trace("Ignoring line: {}", line);
        } else if (!line.contains(KEY_VALUE_SEPARATOR)) {
            // This must be part of the multi-line value
            logger.trace("Appending ({}) to multilined value", line);
            previousValue.append("\n").append(line);
        } else {
            logger.trace("Extracting key-value pair from {}", line);
            extractKeyValueAndUpdateConfigSoFar(line);
        }
    }

    /*
    An empty line or a line starting with "#" character is treated as a commented line
    */
    private boolean isCommentOrBlank(String line) {
        return line.isEmpty() || line.startsWith("#");
    }

    /*
    Extract KEY and VALUE from line in the format of "KEY = VALUE". Then update value gathered for last (i.e. previous) key and mark current key
    and value as previous key and value (so that it is updated when next "KEY = VALUE" is found)
    */
    private void extractKeyValueAndUpdateConfigSoFar(String line) {
        String[] fields = line.split(KEY_VALUE_SEPARATOR);
        if (!isFieldsValid(fields)) {
            throw new InvalidConfigException("Contains invalid line (" + line + ")");
        }
        // It is important here to trim, otherwise the key would not be found in the configKeys map due to any whitespace present
        String key = fields[0].trim();
        String val = fields[1].trim();
        updateConfigSoFar();
        synchronized(this){
            previousKey = key;
            previousValue = new StringBuilder().append(val);
        }
    }

    private boolean isFieldsValid(String[] fields) {
        return fields.length == 2 && !fields[0].isEmpty();
    }

    private synchronized void updateConfigSoFar() {
        if (previousKey != null && !previousKey.isEmpty()) {
            updateRepository(previousKey, previousValue.toString());
        }
    }

    private void updateRepository(String keyName, String value) {
        if (!configKeyMap.containsKey(keyName)) {
            throw new InvalidConfigException("Unrecongnized configuration key (" + keyName + ")");
        }
        properties.update(configKeyMap.get(keyName), value);
    }
}