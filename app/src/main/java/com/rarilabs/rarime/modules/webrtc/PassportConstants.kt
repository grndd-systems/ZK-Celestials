package com.rarilabs.rarime.modules.webrtc

import CircuitAlgorithmType
import CircuitHashAlgorithmType
import CircuitKeySizeType
import RegisterIdentityCircuitType
import org.web3j.crypto.Hash
import org.web3j.utils.Numeric

/**
 * Utilities for computing passport and verifier type identifiers
 * using keccak256 hashes, matching the approach in the smart contracts
 */
object PassportTypeUtils {
    /**
     * Determines the passport data type constant name based on circuit configuration
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
        val keySize = aaAlgorithm.keySize

        return when {
            // RSA with SHA1
            algorithm == CircuitAlgorithmType.RSA &&
            hashAlgo == CircuitHashAlgorithmType.HA160 &&
            keySize == CircuitKeySizeType.B2048 &&
            aaAlgorithm.exponent?.name != "E3" -> "P_RSA_SHA1_2688"

            // RSA with SHA1 and exponent 3
            algorithm == CircuitAlgorithmType.RSA &&
            hashAlgo == CircuitHashAlgorithmType.HA160 &&
            keySize == CircuitKeySizeType.B2048 &&
            aaAlgorithm.exponent?.name == "E3" -> "P_RSA_SHA1_2688_3"

            // ECDSA with SHA1
            algorithm == CircuitAlgorithmType.ECDSA &&
            hashAlgo == CircuitHashAlgorithmType.HA160 -> "P_ECDSA_SHA1_2704"

            // RSA with SHA256
            algorithm == CircuitAlgorithmType.RSA &&
            hashAlgo == CircuitHashAlgorithmType.HA256 &&
            keySize == CircuitKeySizeType.B2048 &&
            aaAlgorithm.exponent?.name != "E3" -> "P_RSA_SHA256_2688"

            // RSA with SHA256 and exponent 3
            algorithm == CircuitAlgorithmType.RSA &&
            hashAlgo == CircuitHashAlgorithmType.HA256 &&
            keySize == CircuitKeySizeType.B2048 &&
            aaAlgorithm.exponent?.name == "E3" -> "P_RSA_SHA256_2688_3"

            else -> "P_NO_AA" // Default fallback
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
 * Computes keccak256 hash of a string (matching ethers.js solidityPackedKeccak256)
 */
fun keccak256(input: String): String {
    // Use UTF-8 encoding like ethers.js does with toUtf8Bytes()
    val hash = Hash.sha3(input.toByteArray(Charsets.UTF_8))
    return Numeric.toHexString(hash)
}
