package com.easy.properties;

import com.easy.properties.exception.EnumMissingOptionalFieldException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a thread-safe class used to retrieve the value of a specified field in the Enum <br>
 * It obtains the value of field using getter method having name according to java-naming conventions <br>
 * 
 * @author himanshu_shekhar
 */
public class EnumFieldInfo {

    private static final Logger logger = LoggerFactory.getLogger(EnumFieldInfo.class);

    private final Object enumConstant;
    private final Field field;
    private final Method getterMethod;

    public EnumFieldInfo(Object enumConstant, String fieldName) {
        logger.trace("Constructing instance for {}", fieldName);
        this.enumConstant = enumConstant;

        Field myField = null;
        Method myMethod = null;

        try {
            myField = getField(enumConstant.getClass(), fieldName);
            logger.trace("Found field: {}", myField.getName());
            myMethod = getGetterMethod(enumConstant.getClass(), myField);
            logger.trace("Found getter method: {}", myMethod.getName());
        } catch (NoSuchFieldException e) {
            logger.debug("Cannot find field " + fieldName + " in " + enumConstant.getClass().getCanonicalName(), e);
        } catch (NoSuchMethodException e) {
            logger.debug("Cannot find getter for " + fieldName + " in " + enumConstant.getClass().getCanonicalName(), e);
        }

        field = myField;
        getterMethod = myMethod;
    }

    private static Field getField(Class<?> enumClass, String fieldName) throws NoSuchFieldException {
        return enumClass.getDeclaredField(fieldName);
    }

    private static Method getGetterMethod(Class<?> enumClass, Field field) throws NoSuchMethodException {
        return enumClass.getDeclaredMethod(getGetterMethodName(field));
    }

    /*
    Getter methods for boolean fields starts with "is", whereas that for other type of fields starts with "get" with first letter of field name
    capitalized
    */
    private static String getGetterMethodName(Field field) {
        String fieldName = field.getName();
        StringBuilder sb = new StringBuilder();
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            sb.append("is");
        } else {
            sb.append("get");
        }
        sb.append(Character.toUpperCase(fieldName.charAt(0)));
        sb.append(fieldName.substring(1, fieldName.length()));

        return sb.toString();
    }

    /**
     * @return The value obtained through getter method defined in the Enum
     */
    public Object getFieldValue() {
        validate();
        try {
            return getterMethod.invoke(enumConstant);
        } catch (InvocationTargetException e) {
            throw new EnumMissingOptionalFieldException("Cannot invoke getter method on " + enumConstant.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new EnumMissingOptionalFieldException("Cannot access getter method on " + enumConstant.getClass().getName(), e);
        }
    }

    /*
    Corresponding field and getter method must be defined with getter method having return type same as data type of the field
    */
    private void validate() {
        if (field == null || getterMethod == null || getterMethod.getReturnType() != field.getType()) {
            throw new EnumMissingOptionalFieldException("Enum " + enumConstant.getClass().getName() + "missing at least one of the optional fields");
        }
    }
}
