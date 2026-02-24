package com.grnddsystems.celestials.modules.webrtc.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Messages exchanged through WebRTC Data Channel between Mobile and Desktop
 */

// ===== Mobile to Desktop =====

/**
 * Initial message from Mobile with passport and identity keys
 */
@JsonClass(generateAdapter = true)
data class PassportKeysMessage(
    val type: String = "passport_keys",
    val data: PassportKeysData
)

@JsonClass(generateAdapter = true)
data class PassportKeysData(
    val passportKey: String,  // From getPublicKey()
    val identityKey: String   // From getIdentityKey()
)

/**
 * Final message from Mobile with query proof
 */
@JsonClass(generateAdapter = true)
data class QueryProofMessage(
    val type: String = "query_proof",
    val data: QueryProofResponseData
)

@JsonClass(generateAdapter = true)
data class QueryProofResponseData(
    val zkPoints: String,
    val registration: RegistrationData? = null  // Only included if needsRegistration = true
)

@JsonClass(generateAdapter = true)
data class ProofData(
    val piA: List<String>,
    val piB: List<List<String>>,
    val piC: List<String>
)

@JsonClass(generateAdapter = true)
data class RegistrationData(
    val certificatesRoot: String,
    val dgCommit: String,
    val passportKey: String,
    val identityKey: String,
    val passport: PassportData,
    val zkPoints: String
)

@JsonClass(generateAdapter = true)
data class PassportData(
    val dataType: String,
    val zkType: String,
    val signature: String,
    val publicKey: String,
    val passportHash: String
)

// ===== Desktop to Mobile =====

/**
 * Response from Desktop with query proof parameters
 */
@JsonClass(generateAdapter = true)
data class QueryProofParamsMessage(
    val type: String = "query_proof_params",
    val data: QueryProofParamsData
)

@JsonClass(generateAdapter = true)
data class QueryProofParamsData(
    val id: String,
    @Json(name = "needs_registration")
    val needsRegistration: Boolean,  // Flag if mobile needs to include registration data
    @Json(name = "user_address")
    val userAddress: String,  // Desktop wallet address for userPayload
    val attributes: QueryProofAttributes
)

@JsonClass(generateAdapter = true)
data class QueryProofAttributes(
    @Json(name = "birth_date_lower_bound")
    val birthDateLowerBound: String,

    @Json(name = "birth_date_upper_bound")
    val birthDateUpperBound: String,

    @Json(name = "citizenship_mask")
    val citizenshipMask: String,

    @Json(name = "current_date")
    val currentDate: String,

    @Json(name = "event_data")
    val eventData: String,

    @Json(name = "event_id")
    val eventId: String,

    @Json(name = "expiration_date_lower_bound")
    val expirationDateLowerBound: String,

    @Json(name = "expiration_date_upper_bound")
    val expirationDateUpperBound: String,

    @Json(name = "identity_counter")
    val identityCounter: String,

    @Json(name = "identity_counter_lower_bound")
    val identityCounterLowerBound: String,

    @Json(name = "identity_counter_upper_bound")
    val identityCounterUpperBound: String,

    @Json(name = "selector")
    val selector: String,

    @Json(name = "timestamp_lower_bound")
    val timestampLowerBound: String,

    @Json(name = "timestamp_upper_bound")
    val timestampUpperBound: String
)

// ===== Generic Message Wrapper =====

/**
 * Generic message wrapper for parsing incoming messages
 */
@JsonClass(generateAdapter = true)
data class WebRTCMessage(
    val type: String,
    val data: Map<String, Any>? = null
)
