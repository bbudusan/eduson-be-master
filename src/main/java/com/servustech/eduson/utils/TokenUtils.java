package com.servustech.eduson.utils;

import org.apache.commons.lang3.RandomStringUtils;

public class TokenUtils {

    private static final int TOKEN_LENGTH = 8;
    private static final int PASSWORD_LENGTH = 6;

    public static String generateConfirmationToken() {
        return RandomStringUtils.randomAlphanumeric(TOKEN_LENGTH);
    }

    public static String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(PASSWORD_LENGTH);
    }
}
