# Examples
Below given examples are for demonstrating how the [properties](https://github.com/easy-develop/properties) library can be used in various scenarios. Each of the examples will have these:

 1. `enum` class
 2. Configuration property file
 3. Demo driver program
 4. Console output
<hr>

## List of examples:

 - [A Simple Case](#simple_case)
 - [Specifying mandatory property keys](#specify_mandatory_keys)
 - [Using key name different from enum constant name](#different_key)
 - [Specifying default value for a key](#default_value)
 - [Obtaining values as non-String types](#typed_value)
 - [Obtaining list of values](#list_of_values)
 - [Multi-lined value](#multi_lined_value)
 - [Variable substitution in the values](#var_substitution)
 - [Using environment in variable](#env_in_variable)
 - [Properties with cyclic dependency](#with_cyclic_dependency)

<a name="simple_case"></a>
## A Simple Case
Name of constants in the user defined `enum` determines the property keys present in the configuration file. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR,
    CONF_DIR
}
```

The property file containing the configuration in the form of `key = value`:  
```properties
HOME_DIR = /home/demo
CONF_DIR = ${HOME_DIR}/conf
```

The demo driver program:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = propsLoader.load();
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        String confDir = props.get(MyPropertyKey.CONF_DIR);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Configuration directory is: " + confDir);
    }
}
```

And, the console output:
```txt
Home directory is: /home/demo
Configuration directory is: /home/demo/conf
```
<hr>

<a name="specify_mandatory_keys"></a>
## Specifying mandatory property keys
If we want to specify certain keys to be mandatory, then the `enum` must contain a `boolean` field named `mandatory` and corresponding getter method (i.e. `boolean isMandatory()`), indicating whether an enum constant represents a mandatory field or not. By default, all fields are considered to be optional. Here is the example enum:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR(true),
    CONF_DIR(true);

    private final boolean mandatory;

    private MyPropertyKey(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isMandatory() {
        return mandatory;
    }
}
```

Now, suppose the configuration file does not contain one of the mandatory keys. Then, the demo driver program will fail with error. Here is the example properties file:
```properties
# This configuration property file is for test purpose
HOME_DIR = /home/demo
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        String confDir = props.get(MyPropertyKey.CONF_DIR);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Configuration directory is: " + confDir);
    }
}
```

And, the output is:
```txt
Cannot load configuration due to: Missing mandatory configuration key (CONF_DIR)
```
<hr>

<a name="different_key"></a>
## Using key name different from enum constant name
If we want to have the property key name something other than variable name, we can do so by defining `keyName` field. The `enum` must contain a `String` field named `keyName` and corresponding getter method (i.e. `String getKeyName()` returning key name to be used in the property file. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR("demo.home.dir"),
    CONF_DIR("demo.conf.dir");

    private final String keyName;

    private MyPropertyKey(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}
```

And configuration property file is:
```properties
demo.home.dir = /home/demo
demo.conf.dir = ${demo.home.dir}/conf
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        String confDir = props.get(MyPropertyKey.CONF_DIR);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Configuration directory is: " + confDir);
    }
}
```

The output is:
```txt
Home directory is: /home/demo
Configuration directory is: /home/demo/conf
```
<hr>

<a name="default_value"></a>
## Specifying default value for a key
If we want to have a default value in case value is not available in the configuration file, we can do so by defining a field named `defaultValue` of type `String` in the `enum`. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR("/tmp"),
    CONF_DIR("/var/");

    private final String defaultValue;

    private MyPropertyKey(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
```

The configuration property file is:
```properties
HOME_DIR = /home/demo
# The CONF_DIR is missing intentionally, should take default value
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        String confDir = props.get(MyPropertyKey.CONF_DIR);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Configuration directory is: " + confDir);
    }
}
```

And the output is:
```txt
Home directory is: /home/demo
Configuration directory is: /var/tmp
```

NOTE: The `enum` can contain all or any of the fields defined above (i.e. `mandatory`, `keyName` and `defaultValue`) to customize the behavior of configuration properties
<hr>


<a name="typed_value"></a>
## Obtaining values as non-String types
Instead of getting the configuration values as `String` and parsing  them to required data type, API provides methods for obtaining values as supported data types. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR,
    MIN_ALLOWED_PORT,
    MAX_ALLOWED_LOAD_RATIO
}
```

The configuration property file is:
```properties
HOME_DIR = /home/demo
MIN_ALLOWED_PORT = 1024
MAX_ALLOWED_LOAD_RATIO = .85
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        int minAllowedPort = props.getInt(MyPropertyKey.MIN_ALLOWED_PORT);
        float maxAllowedLoadRation = props.getFloat(MyPropertyKey.MAX_ALLOWED_LOAD_RATIO);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Minimum allowed starting port: " + minAllowedPort);
        System.out.println("Max allowed load ratio: " + maxAllowedLoadRation);
    }
}
```

And the output is:
```txt
Home directory is: /home/demo
Minimum allowed starting port: 1024
Max allowed load ratio: 0.85
```
<hr>


<a name="list_of_values"></a>
## Obtaining list of values
The API provides method for obtaining configuration value as a list of elements of specified type. If delimiter separating the elements is not specified,  comma (`,`) is used by default.  Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR,
    DIRECTORIES_TO_SEARCH
}
```

The configuration property file is:
```properties
HOME_DIR = /home/demo
# Make a note that delimiter here is "|". So, specify this delimiter while
# obtaining the value as list
DIRECTORIES_TO_SEARCH = /tmp | /var/tmp | /var/www
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;
import java.util.List;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        List<String> directoriesToSearch = props.getList(MyPropertyKey.DIRECTORIES_TO_SEARCH, String.class, "|");
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Directories to search are: " + directoriesToSearch);
    }
}
```

And the output is:
```txt
Home directory is: /home/demo
Directories to search are: [/tmp, /var/tmp, /var/www]
```
<hr>


<a name="multi_lined_value"></a>
## Multi-lined value
The configuration property file can contain values spanned over multiple lines. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR,
    DESCRIPTION
}
```

The configuration property file is:
```properties
HOME_DIR = /home/demo
DESCRIPTION = This is a demo utility,
just for the test purpose. And, it is not
supposed to do any actual work as you might already have guessed!!
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;
import java.util.List;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        String description = props.get(MyPropertyKey.DESCRIPTION);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Description is: " + description);
    }
}
```

And the output is:
```txt
Home directory is: /home/demo
Description is: This is a demo utility,
just for the test purpose. And, it is not
supposed to do any actual work as you might already have guessed!!
```
<hr>

<a name="var_substitution"></a>
## Variable substitution in the values
The configuration value can have variables (starting with a `$` character you might have seen in `bash` syntax), where the name of variable is some key defined in the property file, irrespective of the order. The value of variable will be resolved recursively, which means that value of a variable can contain yet another variable. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR,
    BIN_DIR,
    DUMP_FILE_PATH
}
```

The configuration property file is:
```properties
BIN_DIR = ${HOME_DIR}/bin
DUMP_FILE_PATH = ${BIN_DIR}/dump_logs.trc
# Here, note that HOME_DIR is referened before it is defined
# This should not cause any problem and variable value should be resolved correctly
HOME_DIR = /home/demo
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        String binDir = props.get(MyPropertyKey.BIN_DIR);
        String dumpFilePath = props.get(MyPropertyKey.DUMP_FILE_PATH);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Bin directory is: " + binDir);
        System.out.println("Dump file path is: " + dumpFilePath);
    }
}
```

And the output is:
```txt
Home directory is: /home/demo
Bin directory is: /home/demo/bin
Dump file path is: /home/demo/bin/dump_logs.trc
```
<hr>

<a name="env_in_variable"></a>
## Using environment in variable
The property configuration file can contain Java system properties (e.g. those specified through `-D` option, like in `java -Dhome.dir=/tmp`) and environment variables. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    CURRENT_USER,
    USER_NAME
}
```

The configuration property file is:
```properties
CURRENT_USER = ${user.name}
USER_NAME = ${USERNAME}
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadPropsDemo {
    private static final Logger logger = LoggerFactory.getLogger(ReadPropsDemo.class);
    
    public static void main(String[] args) {
        logger.trace("Starting ...");
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        System.out.println("Current user: " + props.get(MyPropertyKey.CURRENT_USER));
        System.out.println("User name: " + props.get(MyPropertyKey.USER_NAME));
    }
}
```

And the output is:
```txt
Current user: himanshu_shekhar
User name: himanshu_shekhar
```
<hr>

<a name="with_cyclic_dependency"></a>
## Properties with cyclic dependency
The cyclic dependencies, if any, in the configuration property file will be detected and appropriate error message will be shown. Here is the example `enum`:
```java
package com.demo.properties;

public enum MyPropertyKey {
    HEAD,
    TAIL
}
```

The configuration property file is:
```properties
# The variables are specified intentionally in order to created cyclic dependency
HEAD = ${TAIL}
TAIL = ${HEAD}
```

The demo driver program is:
```java
package com.demo.properties;

import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import com.easy.properties.exception.InvalidConfigException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadPropsDemo {
    private static final Logger logger = LoggerFactory.getLogger(ReadPropsDemo.class);
    
    public static void main(String[] args) {
        logger.trace("Starting ...");
        PropertiesLoader propsLoader = new PropertiesLoader("src/main/resources/config.properties", MyPropertyKey.class);
        Properties props = null;
        try{
            props = propsLoader.load();
        }catch(InvalidConfigException e){
            System.err.println("Cannot load configuration due to: " + e.getMessage());
            System.exit(0);
        }
        
        System.out.println("Head: " + props.get(MyPropertyKey.HEAD));
        System.out.println("Tail: " + props.get(MyPropertyKey.TAIL));
    }
}
```

And the output is:
```txt
Cannot load configuration due to: Detected cyclic dependency in: ${HEAD}, for: HEAD
```
<hr>