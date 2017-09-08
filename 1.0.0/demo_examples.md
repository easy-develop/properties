# Demo Examples
Below given examples are for demonstrating how the [properties](https://github.com/easy-develop/properties) library can be used in various scenarios. Each of the examples will show these:

 1. `enum` class
 2. Configuration property file
 3. Demo driver program
 4. Console output

<hr>

 - **A simple case**
 

    MyPropertyKey.java

 
 ```java
package com.demo.properties;

public enum MyPropertyKey {
    HOME_DIR,
    CONF_DIR
}
```

```properties
HOME_DIR = /home/demo
CONF_DIR = ${HOME_DIR}/conf
```
`ReadPropsDemo.java`
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
        System.out.println("Current users: " + confDir);
    }
}
```
`Output`

    HOME_DIR = /home/demo
    CONF_DIR = ${HOME_DIR}/conf