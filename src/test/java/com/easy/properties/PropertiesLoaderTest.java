package com.easy.properties;

import com.easy.properties.data.Var;
import com.easy.properties.enums.MyPropsEmpty;
import com.easy.properties.enums.MyPropsSimple;
import com.easy.properties.exception.InvalidConfigException;
import com.easy.properties.exception.InvalidEnumException;
import static org.junit.Assert.*;
import org.junit.Test;

public class PropertiesLoaderTest {
    @Test
    public void loadsSimpleProperties(){
        PropertiesLoader loader = new PropertiesLoader(Var.SIMPLE_PROPS, MyPropsSimple.class);
        assertNotNull("Cannot load simple configuration", loader.load());
    }
    
    @Test(expected = InvalidEnumException.class)
    public void invalidEnumIsThrownIfNoConstantDefined(){
        PropertiesLoader loader = new PropertiesLoader(Var.SIMPLE_PROPS, MyPropsEmpty.class);
        loader.load();
    }
    
    @Test(expected = InvalidConfigException.class)
    public void invalidConfigIsThrownIfConfigFileNotExists(){
        PropertiesLoader loader = new PropertiesLoader(Var.NON_EXISTENT_PROPS, MyPropsSimple.class);
        loader.load();
    }
    
    @Test(expected = InvalidConfigException.class)
    public void invalidConfigIsThrownIfConfigFileHasInvalidEntry(){
        PropertiesLoader loader = new PropertiesLoader(Var.INVALID_PROPS, MyPropsSimple.class);
        loader.load();
    }
    
    @Test
    public void loadsPropertiesWithComments(){
        PropertiesLoader loader = new PropertiesLoader(Var.COMMENTED_PROPS, MyPropsSimple.class);
        assertNotNull("Cannot load config file with comments", loader.load());
    }
}
