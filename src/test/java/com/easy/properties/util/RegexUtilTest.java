package com.easy.properties.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class RegexUtilTest {
    
    @Test
    public void tellsIfSpecialCharacterPresent(){
        assertEquals("Cannot find if special character is present", true, RegexUtil.containsSpecialCharacter("|"));
        assertEquals("Cannot find if special character is present", false, RegexUtil.containsSpecialCharacter(":"));
    }
    
    @Test
    public void providesSpecialCharactersEscaped(){
        assertEquals("Cannot escape special characters", "delimiter is: \\|", RegexUtil.getSpecialCharactersEscaped("delimiter is: |"));
    }
}
