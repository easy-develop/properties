## Table of Contents
- [Overview](#overview)
- [Feature Highlights](#feature_highlights)
- [Short Description](#short_description)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting_started)
- [Support](#support)
- [License](#license)

<a name="overview"></a>
## Overview
An easy to use and lightweight Java library for retrieving configuration properties backed by an `enum`. Supports multi-lined value and variable substitution (e.g. `$PATH` or `${PATH}` as in bash syntax) in the values. Use of enum for defining the property keys will make referencing source **less error prone** and **easy to refactor**, as far as this library is concerned

<a name="feature_highlights"></a>
## Feature Highlights
- All property keys are defined in a single place, i.e. a user defined `enum`
- Property values can contain variables (starting with `$` sign, e.g. `$PATH`) where `PATH` is a property key in same file
- Property value can be obtained as specified data type, so no need to convert string value to required data type
- Property value can be obtained as list of specified data types
- Property value can be a multi-line text
- Can make property keys mandatory or optional
- Can specify default value for the property key if value is not available
- Is thread safe

<a name="short_description"></a>
## Short Description
While using property files are often preferred for defining application configurations due to their simplicity and readability, they also have certain disadvantages. One of the major problem in using property files is that property keys are in the form of `String`, so it is difficult to manage. If we were to change some key, we need to manually search its occurrences and update all of them. This makes our code fragile and cumbersome. So, this library is designed to provide `enum` backed configuration property. Due to use of `enum`, all the property keys are defined in a single place, and since enum constants are referred to instead of plain `String`s, refactoring the code becomes easy and less error prone. The APIs also provide methods for retrieval of the values in form of data type that we want

<a name="prerequisites"></a>
## Prerequisites
- JRE 1.6 or above

<a name="getting_started"></a>
## Getting Started
- First of all, include jar for this library in your project. For example, if you are using maven, add below given dependency to your pom.xml file.
```xml
<dependency>
    <groupId>com.github.easy-develop</groupId>
    <artifactId>properties</artifactId>
    <version>1.0</version>
</dependency>
```
If you are using any other tool for build, have a look at [Maven Repository](https://mvnrepository.com/artifact/com.github.easy-develop/properties/1.0) page for corresponding dependency declaration

- Create an `enum` class which will define the property keys, like below example:
```java
public enum MyPropertyKey {
    HOME_DIR,
    CONF_DIR,
    LOG_DIR,
    CURRENT_USER_IDS
}
```
**Note**: *The `enum` can be changed to indicate things like whether property key is mandatory or optional, or default value if value is not available. But for our example, we are considering simplest of the cases*

- Invoke the APIs to obtain a Properties object which will provide available property values in required format, like below example:
```java
import com.easy.properties.Properties;
import com.easy.properties.PropertiesLoader;
import java.util.List;

public class ReadPropsDemo {
    public static void main(String[] args) {
        PropertiesLoader propsLoader = new PropertiesLoader("_path_to_property_file_", MyPropertyKey.class);
        Properties props = propsLoader.load();
        
        String homeDir = props.get(MyPropertyKey.HOME_DIR);
        List<Integer> currentUserIds = props.getList(MyPropertyKey.CURRENT_USER_IDS, int.class);
        
        System.out.println("Home directory is: " + homeDir);
        System.out.println("Current users: " + currentUserIds);
    }
}
```
And that's it. Obtained instance of `com.easy.properties.Properties` can be used to retrieve value corresponding to certain enum constant in required format (data type) as shown above

<a name="support"></a>
## Support
Please [open an issue](https://github.com/easy-develop/properties/issues) if you need any assistance or have any suggestion regarding this library

<a name="license"></a>
## License
This project is licensed under [MIT License](https://github.com/easy-develop/properties/blob/master/LICENSE)
