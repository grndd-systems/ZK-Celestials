package com.grnddsystems.celestials.util

/**
 * Utility object for formatting addresses from private keys
 */
object AddressFormatter {

    /**
     * Formats a private key into a shortened address format
     * Takes FIRST 3 characters + "..." + LAST 8 characters
     *
     * Example:
     * Input: "1234567891011121314151617181920212223242"
     * Output: "123...23242"
     *
     * @param privateKey The full private key (should be 40 characters)
     * @return Formatted address string, or "Not set" if key is empty/null
     */
    fun formatAddress(privateKey: String?): String {
        // Return "Not set" if key is null or empty
        if (privateKey.isNullOrEmpty()) {
            return "Not set"
        }

        // Return "Not set" if key is too short to format properly
        if (privateKey.length < 11) {
            return "Not set"
        }

        // Extract first 3 characters
        val firstThree = privateKey.take(3)

        // Extract last 8 characters
        val lastEight = privateKey.takeLast(8)

        // Combine: FIRST 3 + "..." + LAST 8
        return "$firstThree...$lastEight"
    }

    /**
     * Validates if a private key has the correct length
     * Must be exactly 40 characters
     *
     * @param privateKey The private key to validate
     * @return true if key is exactly 40 characters, false otherwise
     */
    fun isValidKeyLength(privateKey: String?): Boolean {
        // Key must be exactly 40 characters
        return privateKey?.length == 40
    }
}