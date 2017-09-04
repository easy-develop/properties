package com.easy.properties.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This utility class provides methods for regular-expressions related operations
 * @author himanshu_shekhar
 */
public class RegexUtil {

    private static final List<Character> REGEX_SPECIAL_CHARACTERS = new ArrayList<Character>(Arrays.asList(new Character[]{
        '[',
        '\\',
        '^',
        '$',
        '.',
        '|',
        '?',
        '*',
        '+',
        '(',
        ')'
    }));
    
    private RegexUtil(){}

    /**
     * 
     * @param text The text which is to be checked for any regex meta-character
     * @return If the meta-character is found
     */
    public static boolean containsSpecialCharacter(String text) {
        boolean hasSpecialChar = false;

        for (char specialCharacter : REGEX_SPECIAL_CHARACTERS) {
            if (text.contains(Character.toString(specialCharacter))) {
                hasSpecialChar = true;
                break;
            }
        }

        return hasSpecialChar;
    }

    /**
     * 
     * @param text The text which in which meta-characters are to be escaped
     * @return The text which has meta-characters escaped so  that it can be used with String.split() method
     */
    public static String getSpecialCharactersEscaped(String text) {
        StringBuilder escapedDelimiter = new StringBuilder();
        for (char currentChar : text.toCharArray()) {
            if (REGEX_SPECIAL_CHARACTERS.contains(currentChar)) {
                escapedDelimiter.append("\\");
            }
            escapedDelimiter.append(currentChar);
        }

        return escapedDelimiter.toString();
    }
}
