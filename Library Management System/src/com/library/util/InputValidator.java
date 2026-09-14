package com.library.util;

import java.util.Scanner;

/**
 * InputValidator Utility
 * Demonstrates:
 * - Robust console input parsing (Unit 1)
 * - Exception handling during input reading (Unit 3)
 * - String validation and trimming (Unit 2 & 4)
 */
public class InputValidator {

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("(!) Invalid number format. Please enter a valid integer.");
            }
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("(!) Value must be greater than 0. Please try again.");
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("(!) Input cannot be empty. Please enter a valid value.");
        }
    }

    public static String readEmail(Scanner scanner, String prompt) {
        while (true) {
            String email = readNonEmptyString(scanner, prompt).trim();
            if (email.toLowerCase().endsWith("@vitbhopal.ac.in") && email.indexOf('@') > 0) {
                return email;
            }
            System.out.println("(!) Invalid email format. Email ID must be in @vitbhopal.ac.in format. Try again.");
        }
    }

    public static String readPhone(Scanner scanner, String prompt) {
        while (true) {
            String phone = readNonEmptyString(scanner, prompt);
            // Must contain at least 7 digits
            if (phone.replaceAll("[^0-9]", "").length() >= 7) {
                return phone;
            }
            System.out.println("(!) Invalid phone number (must contain at least 7 digits). Try again.");
        }
    }
}
