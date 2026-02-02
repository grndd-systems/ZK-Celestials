package com.rarilabs.celestial.modules.webrtc

import CircuitAlgorithmType
import CircuitHashAlgorithmType
import RegisterIdentityCircuitType
import org.web3j.crypto.Hash

/**
 * Utilities for computing passport and verifier type identifiers
 * using keccak256 hashes, matching the approach in the smart contracts
 */
object PassportTypeUtils {
    /**
     * Determines the passport data type constant name based on circuit configuration
     *
     * The number in the constant name represents:
     * dg15DigestPositionShift + aaKeyPositionShift
     *
     * For example: P_RSA_SHA1_2688 where 2688 = 2432 + 256
     */
    private fun getPassportDataTypeName(circuitType: RegisterIdentityCircuitType): String {
        val aaType = circuitType.aaType

        // If no AA (Active Authentication), return P_NO_AA
        if (aaType == null) {
            return "P_NO_AA"
        }

        val aaAlgorithm = aaType.aaAlgorithm
        val algorithm = aaAlgorithm.algorithm
        val hashAlgo = aaAlgorithm.hashAlgorithm
        val exponent = aaAlgorithm.exponent

        // Calculate the circuit size number
        // This represents: dg15DigestPositionShift + aaKeyPositionShift
        val circuitSize = aaType.dg15DigestPositionShift + aaType.aaKeyPositionShift

        // Determine algorithm prefix
        val algorithmPrefix = when (algorithm) {
            CircuitAlgorithmType.RSA -> "RSA"
            CircuitAlgorithmType.ECDSA -> "ECDSA"
            else -> return "P_NO_AA"
        }

        // Determine hash algorithm suffix
        val hashSuffix = when (hashAlgo) {
            CircuitHashAlgorithmType.HA160 -> "SHA1"
            CircuitHashAlgorithmType.HA256 -> "SHA256"
            CircuitHashAlgorithmType.HA384 -> "SHA384"
            CircuitHashAlgorithmType.HA512 -> "SHA512"
            else -> return "P_NO_AA"
        }

        // Build the constant name: P_{ALGORITHM}_{HASH}_{SIZE}[_3]
        val baseName = "P_${algorithmPrefix}_${hashSuffix}_${circuitSize}"

        // Add exponent suffix if it's E3
        return if (exponent?.name == "E3") {
            "${baseName}_3"
        } else {
            baseName
        }
    }

    /**
     * Computes passport data type hash from circuit configuration
     */
    fun getPassportDataType(circuitType: RegisterIdentityCircuitType): String {
        val typeName = getPassportDataTypeName(circuitType)
        return keccak256(typeName)
    }

    /**
     * Computes verifier type hash from circuit name
     * Converts from "registerIdentity_X_Y_Z..." to "Z_NOIR_PASSPORT_X_Y_Z..."
     */
    fun getVerifierType(circuitName: String): String {
        // Convert registerIdentity_11_256_3_5_576_248_1_1808_5_296
        // to Z_NOIR_PASSPORT_11_256_3_5_576_248_1_1808_5_296
        val verifierName = if (circuitName.startsWith("registerIdentity_")) {
            "Z_NOIR_PASSPORT_" + circuitName.removePrefix("registerIdentity_")
        } else {
            circuitName
        }
        return keccak256(verifierName)
    }
}

/**
 * Computes keccak256 hash of a string (matching ethers.js keccak256(["string"], [value]))
 * This matches the TypeScript implementation: keccak256(["string"], ["Z_NOIR_PASSPORT_..."])
 */
fun keccak256(input: String): String {
    // web3j's Hash.sha3String() encodes the string using ABI encoding then hashes it
    // This matches ethers.js keccak256(["string"], [value])
    val hash = Hash.sha3String(input)
    return hash
}
