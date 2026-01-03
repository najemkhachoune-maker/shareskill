package com.skillverse.authservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorUtilTest {

    @Test
    void isValidEmail_ShouldReturnTrue_ForValidEmail() {
        assertTrue(ValidatorUtil.isValidEmail("test@example.com"));
        assertTrue(ValidatorUtil.isValidEmail("user.name@domain.co.uk"));
    }

    @Test
    void isValidEmail_ShouldReturnFalse_ForInvalidEmail() {
        assertFalse(ValidatorUtil.isValidEmail("invalid-email"));
        assertFalse(ValidatorUtil.isValidEmail("@example.com"));
        assertFalse(ValidatorUtil.isValidEmail("test@"));
        assertFalse(ValidatorUtil.isValidEmail(null));
    }

    @Test
    void isValidPhone_ShouldReturnTrue_ForValidPhone() {
        assertTrue(ValidatorUtil.isValidPhone("+1234567890"));
        assertTrue(ValidatorUtil.isValidPhone("1234567890"));
    }

    @Test
    void isValidPhone_ShouldReturnFalse_ForInvalidPhone() {
        assertFalse(ValidatorUtil.isValidPhone("123"));
        assertFalse(ValidatorUtil.isValidPhone("invalid"));
        assertFalse(ValidatorUtil.isValidPhone(null));
    }

    @Test
    void isStrongPassword_ShouldReturnTrue_ForStrongPassword() {
        assertTrue(ValidatorUtil.isStrongPassword("Password123"));
        assertTrue(ValidatorUtil.isStrongPassword("MyP@ssw0rd"));
    }

    @Test
    void isStrongPassword_ShouldReturnFalse_ForWeakPassword() {
        assertFalse(ValidatorUtil.isStrongPassword("password")); // No uppercase or digit
        assertFalse(ValidatorUtil.isStrongPassword("PASSWORD123")); // No lowercase
        assertFalse(ValidatorUtil.isStrongPassword("Password")); // No digit
        assertFalse(ValidatorUtil.isStrongPassword("Pass1")); // Too short
        assertFalse(ValidatorUtil.isStrongPassword(null));
    }
}
