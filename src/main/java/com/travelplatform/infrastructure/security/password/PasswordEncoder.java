package com.travelplatform.infrastructure.security.password;

import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Password Encoder using BCrypt hashing algorithm.
 * This class provides secure password hashing and verification.
 */
@ApplicationScoped
public class PasswordEncoder {

    private static final int BCRYPT_COST = 12;

    /**
     * Hash a plain text password using BCrypt.
     *
     * @param rawPassword Plain text password
     * @return Hashed password
     * @throws IllegalArgumentException if password is null or empty
     */
    public String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    /**
     * Verify a plain text password against a hashed password.
     *
     * @param rawPassword Plain text password to verify
     * @param encodedPassword Hashed password to compare against
     * @return true if passwords match, false otherwise
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Raw password cannot be null or empty");
        }
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            throw new IllegalArgumentException("Encoded password cannot be null or empty");
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    /**
     * Check if a password needs rehashing (e.g., when cost factor changes).
     *
     * @param encodedPassword Hashed password to check
     * @return true if password needs rehashing, false otherwise
     */
    public boolean needsRehash(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        // Extract the cost factor from the BCrypt hash
        // BCrypt hash format: $2a$cost$salt+hash
        try {
            String[] parts = encodedPassword.split("\\$");
            if (parts.length >= 3) {
                int cost = Integer.parseInt(parts[2]);
                return cost < BCRYPT_COST;
            }
        } catch (Exception e) {
            // If parsing fails, assume rehash is needed
            return true;
        }
        return false;
    }

    /**
     * Validate password strength.
     *
     * @param password Password to validate
     * @return true if password meets strength requirements, false otherwise
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        // Minimum 8 characters
        if (password.length() < 8) {
            return false;
        }

        // At least one uppercase letter
        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        // At least one lowercase letter
        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        // At least one digit
        if (!password.matches(".*\\d.*")) {
            return false;
        }

        // At least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return false;
        }

        return true;
    }

    /**
     * Get password strength description.
     *
     * @param password Password to evaluate
     * @return Password strength description
     */
    public String getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return "EMPTY";
        }

        int score = 0;

        // Length score
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;

        // Character variety score
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;

        // Return strength based on score
        if (score <= 2) return "WEAK";
        if (score <= 4) return "MODERATE";
        if (score <= 5) return "STRONG";
        return "VERY_STRONG";
    }

    /**
     * Generate a random password with specified length.
     *
     * @param length Password length
     * @return Random password
     */
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        String allChars = upper + lower + digits + special;
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upper.charAt((int) (Math.random() * upper.length())));
        password.append(lower.charAt((int) (Math.random() * lower.length())));
        password.append(digits.charAt((int) (Math.random() * digits.length())));
        password.append(special.charAt((int) (Math.random() * special.length())));

        // Fill the rest with random characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt((int) (Math.random() * allChars.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    /**
     * Shuffle a string randomly.
     *
     * @param input String to shuffle
     * @return Shuffled string
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = (int) (Math.random() * characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
