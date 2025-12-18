package com.megabyte.payonapplication;

import java.security.SecureRandom;

public class AccountUtils {
    public static String generateAccountNumber() {
        SecureRandom rand = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);

        sb.append(rand.nextInt(9) + 1);
        for (int i = 1; i < 16; i++) {
            sb.append(rand.nextInt(10));
        }
        return sb.toString();
    }
}