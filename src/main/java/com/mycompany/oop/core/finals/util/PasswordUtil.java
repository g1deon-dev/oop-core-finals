package com.mycompany.oop.core.finals.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 310000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);

        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt)
            throws GeneralSecurityException {

        PBEKeySpec spec = new PBEKeySpec(
            password.toCharArray(),
            Base64.getDecoder().decode(salt),
            ITERATIONS,
            KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                SecretKeyFactory.getInstance(ALGORITHM);

            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);

        } finally {
            spec.clearPassword();
        }
    }

    public static boolean verifyPassword(
            String password, String salt, String storedHash)
            throws GeneralSecurityException {

        String calculatedHash = hashPassword(password, salt);

        byte[] calculatedBytes =
            Base64.getDecoder().decode(calculatedHash);

        byte[] storedBytes =
            Base64.getDecoder().decode(storedHash);

        return MessageDigest.isEqual(calculatedBytes, storedBytes);
    }
}