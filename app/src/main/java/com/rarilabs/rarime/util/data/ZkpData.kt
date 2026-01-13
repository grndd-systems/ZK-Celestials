package com.rarilabs.rarime.util.data

import com.google.gson.Gson
import com.rarilabs.rarime.api.registration.models.LightRegistrationData
import org.web3j.utils.Numeric
import java.math.BigInteger


sealed class UniversalProof {
    abstract fun getPubSignals(): List<String>
    abstract fun getIdentityKey(): String
    abstract fun getPublicKey(): String
    abstract fun getPassportHash(): String
    abstract fun getProofJson(): String

    data class Groth internal constructor(val proof: GrothProof) : UniversalProof() {
        override fun getPubSignals() = proof.pub_signals
        override fun getIdentityKey() = proof.pub_signals[3]
        override fun getPublicKey() = proof.pub_signals[0]
        override fun getPassportHash() = proof.pub_signals[1]
        override fun getProofJson(): String {
            return Gson().toJson(proof)
        }
    }

    data class Light internal constructor(
        val proof: LightRegistrationData, val grothProof: GrothProof
    ) : UniversalProof() {
        override fun getPubSignals() = grothProof.pub_signals

        override fun getIdentityKey() = grothProof.pub_signals[2]
        override fun getPublicKey() =
            BigInteger(Numeric.hexStringToByteArray(proof.public_key)).toString()

        override fun getPassportHash() =
            BigInteger(Numeric.hexStringToByteArray(proof.passport_hash)).toString()

        override fun getProofJson(): String {
            return Gson().toJson(grothProof)
        }
    }

    data class Plonk internal constructor(val proof: PlonkProof) : UniversalProof() {
        override fun getPubSignals() = proof.pub_signals
        override fun getIdentityKey() = proof.pub_signals[3]

        // Returns decimal string (for compatibility with existing code)
        override fun getPublicKey() = proof.pub_signals[0]

        // Returns decimal string (for compatibility with existing code)
        override fun getPassportHash() = proof.pub_signals[1]

        override fun getProofJson(): String {
            return Gson().toJson(proof)
        }

        /**
         * Returns public key as hex string with 0x prefix
         */
        fun getPublicKeyHex(): String {
            val decimal = BigInteger(proof.pub_signals[0])
            return Numeric.toHexString(decimal.toByteArray())
        }

        /**
         * Returns passport hash as hex string with 0x prefix (32 bytes padded)
         */
        fun getPassportHashHex(): String {
            val decimal = BigInteger(proof.pub_signals[1])
            // Ensure 32 bytes (64 hex chars after 0x)
            val hexStr = Numeric.toHexStringNoPrefix(decimal.toByteArray())
            return "0x" + hexStr.padStart(64, '0')
        }
    }

    companion object {
        fun fromGroth(grothProof: GrothProof): Groth = Groth(grothProof)
        fun fromPlonk(plonkProof: PlonkProof): Plonk = Plonk(plonkProof)
        fun fromLight(light: LightRegistrationData, groth: GrothProof): Light = Light(light, groth)


    }
}


object UniversalProofFactory {
    private val gson = Gson()
    fun fromRaw(raw: String): UniversalProof? {
        return try {
            val groth = gson.fromJson(raw, GrothProof::class.java)
            UniversalProof.fromGroth(groth)
        } catch (_: Exception) {
            try {
                val plonk = gson.fromJson(raw, PlonkProof::class.java)
                UniversalProof.fromPlonk(plonk)
            } catch (_: Exception) {
                null
            }
        }
    }

    fun fromGroth(groth: GrothProof): UniversalProof.Groth = UniversalProof.fromGroth(groth)

    fun fromPlonkBytes(plonkRaw: ByteArray): UniversalProof.Plonk =
        UniversalProof.fromPlonk(PlonkProof.fromByteArray(plonkRaw))

    fun fromLight(light: LightRegistrationData, groth: GrothProof): UniversalProof.Light =
        UniversalProof.fromLight(light, groth)
}

// --- Proof data classes ---

data class GrothProof(
    val proof: GrothProofData, val pub_signals: List<String>
)

data class GrothProofData(
    val pi_a: List<String>,
    val pi_b: List<List<String>>,
    val pi_c: List<String>,
    val protocol: String,
) {
    companion object {
        fun fromJson(jsonString: String): GrothProofData =
            Gson().fromJson(jsonString, GrothProofData::class.java)
    }
}

data class PlonkProof(
    val rawProof: ByteArray,
    val proof: String,
    val pub_signals: List<String>,
) {
    companion object {
        /**
         * Parse PlonkProof from byte array with known structure (Registration circuit)
         * Registration circuit: 5 public signals + 2144 bytes proof data = 2304 bytes total
         */
        fun fromByteArray(data: ByteArray): PlonkProof {
            require(data.size == 2304) { "data.size != 2304, got ${data.size}" }

            val pubSignalLen = 5
            val pubSignalData = 32
            val pubSignalSize = pubSignalLen * pubSignalData

            // Extract public signals
            val pubSignalsRaw = data.copyOfRange(0, pubSignalSize)
            val pubSignalsList = pubSignalsRaw.toList().chunked(pubSignalData)
                .map { BigInteger(it.toByteArray()).toString() }

            // Extract proof bytes (after pub signals)
            val proofBytes = data.copyOfRange(pubSignalSize, data.size)
            val proofHex = Numeric.toHexString(proofBytes)

            return PlonkProof(
                rawProof = data, proof = proofHex, pub_signals = pubSignalsList
            )
        }

        /**
         * Parse PlonkProof from hex string with dynamic public signal detection
         *
         * This function automatically determines the number of public signals based on
         * the proof size. Plonk proofs have a fixed proof data size (2144 bytes),
         * with public signals prepended (32 bytes each).
         *
         * @param hexProof Hex string with 0x prefix (from Noir circuit.prove())
         * @param proofDataSize Size of the proof data in bytes (default: 2144 for Plonk)
         * @return PlonkProof with automatically detected number of public signals
         *
         * Example:
         * - Registration circuit: 5 pub signals * 32 + 2144 = 2304 bytes total
         * - Query circuit: N pub signals * 32 + 2144 = variable size
         */
        fun fromHexString(hexProof: String, proofDataSize: Int = 2144): PlonkProof {
            val proofBytes = Numeric.hexStringToByteArray(hexProof)

            // Public signals are at the beginning, each is 32 bytes
            val pubSignalSize = 32
            val numPubSignals = (proofBytes.size - proofDataSize) / pubSignalSize

            require(numPubSignals >= 0) {
                "Invalid proof structure: proofBytes.size=${proofBytes.size}, proofDataSize=$proofDataSize"
            }

            // Extract public signals
            val pubSignalsList = (0 until numPubSignals).map { i ->
                val start = i * pubSignalSize
                val end = start + pubSignalSize
                val bytes = proofBytes.copyOfRange(start, end)
                BigInteger(bytes).toString()
            }

            return PlonkProof(
                rawProof = proofBytes,
                proof = hexProof,
                pub_signals = pubSignalsList
            )
        }
    }
}


data class UniversalProofWrapper(
    val type: String,
    val groth: GrothProof? = null,
    val plonk: PlonkProof? = null,
    val light: LightProofDto? = null
)

data class LightProofDto(
    val proof: LightRegistrationData,
    val grothProof: GrothProof
)


fun UniversalProof.toWrapper(): UniversalProofWrapper = when (this) {
    is UniversalProof.Groth -> UniversalProofWrapper("Groth", groth = this.proof)
    is UniversalProof.Plonk -> UniversalProofWrapper("Plonk", plonk = this.proof)
    is UniversalProof.Light -> UniversalProofWrapper(
        "Light",
        light = LightProofDto(this.proof, this.grothProof)
    )
}

fun UniversalProofWrapper.toProof(): UniversalProof = when (type) {
    "Groth" -> UniversalProof.fromGroth(groth!!)
    "Plonk" -> UniversalProof.fromPlonk(plonk!!)
    "Light" -> UniversalProof.fromLight(light!!.proof, light.grothProof)
    else -> throw IllegalStateException("Unknown proof type: $type")
}
