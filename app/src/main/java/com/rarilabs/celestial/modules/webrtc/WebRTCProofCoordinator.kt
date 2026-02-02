package com.rarilabs.celestial.modules.webrtc

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.grndd.celestials.webrtc.core.WebRTCManager
import com.grndd.celestials.webrtc.models.ConnectionState
import com.rarilabs.celestial.api.ext_integrator.ExtIntegratorApiManager
import com.rarilabs.celestial.api.ext_integrator.models.QueryProofGenResponse
import com.rarilabs.celestial.api.ext_integrator.models.QueryProofGenResponseAttributes
import com.rarilabs.celestial.api.ext_integrator.models.QueryProofGenResponseData
import com.rarilabs.celestial.manager.IdentityManager
import com.rarilabs.celestial.manager.PassportManager
import com.rarilabs.celestial.manager.ProofGenerationManager
import com.rarilabs.celestial.modules.webrtc.models.*
import com.rarilabs.celestial.util.data.UniversalProof
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Coordinates the WebRTC proof exchange flow
 *
 * Flow:
 * 1. User scans QR code with peerId
 * 2. WebRTC connection established
 * 3. Send passport + identity keys to Desktop
 * 4. Desktop sends query proof parameters
 * 5. Mobile generates query proof locally (passport data stays on device!)
 * 6. Send both registration and query proofs to Desktop
 * 7. Done!
 */
@Singleton
class WebRTCProofCoordinator @Inject constructor(
    @ApplicationContext private val context: Context,
    private val webRTCManager: WebRTCManager,
    private val identityManager: IdentityManager,
    private val passportManager: PassportManager,
    private val proofGenerationManager: ProofGenerationManager,
    private val extIntegratorApiManager: ExtIntegratorApiManager
) {
    companion object {
        private const val TAG = "WebRTCProofCoord"
    }

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val _flowState = MutableStateFlow<ProofFlowState>(ProofFlowState.Idle)
    val flowState: StateFlow<ProofFlowState> = _flowState.asStateFlow()

    sealed class ProofFlowState {
        data object Idle : ProofFlowState()
        data object Connecting : ProofFlowState()
        data object SendingKeys : ProofFlowState()
        data object WaitingForParams : ProofFlowState()
        data class GeneratingProof(val progress: String) : ProofFlowState()
        data object SendingProofs : ProofFlowState()
        data object Completed : ProofFlowState()
        data class Error(val message: String) : ProofFlowState()
    }

    /**
     * Starts the full proof exchange flow
     * @param peerId Peer ID from scanned QR code
     */
    suspend fun startProofFlow(peerId: String) {
        try {
            Log.d(TAG, "=== Starting proof flow with peer: $peerId ===")
            Log.d(TAG, "WebRTCManager: $webRTCManager")
            Log.d(TAG, "Current flow state: ${_flowState.value}")

            _flowState.value = ProofFlowState.Connecting
            Log.d(TAG, "Updated flow state to: Connecting")

            // Step 1: Connect to Desktop
            Log.d(TAG, "→ Calling webRTCManager.connectToPeer($peerId)...")
            webRTCManager.connectToPeer(peerId)
            Log.d(TAG, "✓ connectToPeer called, waiting for connection...")

            // Step 2: Wait for connection and listen for messages
            Log.d(TAG, "→ Starting connection state listener...")
            coroutineScope.launch {
                webRTCManager.connectionState.collect { state ->
                    Log.d(TAG, "Connection state changed: $state")
                    when (state) {
                        is ConnectionState.Connected -> {
                            Log.d(TAG, "✓ Connected! Sending passport keys...")
                            sendPassportKeys()
                        }
                        is ConnectionState.Connecting -> {
                            Log.d(TAG, "→ Still connecting...")
                        }
                        is ConnectionState.Disconnected -> {
                            Log.d(TAG, "✗ Disconnected")
                        }
                        is ConnectionState.Error -> {
                            Log.e(TAG, "✗ Connection error: ${state.message}")
                            _flowState.value = ProofFlowState.Error(state.message)
                        }
                        else -> {
                            Log.d(TAG, "Other connection state: $state")
                        }
                    }
                }
            }

            // Step 3: Listen for incoming messages
            Log.d(TAG, "→ Starting message listener...")
            coroutineScope.launch {
                webRTCManager.receivedMessages.collect { messages ->
                    Log.d(TAG, "Received messages update: ${messages.size} messages total")
                    messages.lastOrNull()?.let { message ->
                        Log.d(TAG, "→ Processing last message: ${message.take(100)}...")
                        handleIncomingMessage(message)
                    }
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "✗ Proof flow failed", e)
            _flowState.value = ProofFlowState.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Step 1: Send passport and identity keys to Desktop
     */
    private fun sendPassportKeys() {
        coroutineScope.launch {
            try {
                _flowState.value = ProofFlowState.SendingKeys

                // Get keys from registration proof
                val registrationProof = identityManager.registrationProof.value
                if (registrationProof == null) {
                    _flowState.value = ProofFlowState.Error("Registration proof not found")
                    return@launch
                }

                val passportKey = registrationProof.getPublicKey()
                val identityKey = registrationProof.getIdentityKey()

                Log.d(TAG, "Passport key: ${passportKey.take(20)}...")
                Log.d(TAG, "Identity key: ${identityKey.take(20)}...")

                val message = PassportKeysMessage(
                    data = PassportKeysData(
                        passportKey = passportKey,
                        identityKey = identityKey
                    )
                )

                val json = moshi.adapter(PassportKeysMessage::class.java).toJson(message)
                val success = webRTCManager.sendMessage(json)

                if (success) {
                    Log.d(TAG, "✓ Passport keys sent successfully")
                    _flowState.value = ProofFlowState.WaitingForParams
                } else {
                    _flowState.value = ProofFlowState.Error("Failed to send passport keys")
                }

            } catch (e: Exception) {
                Log.e(TAG, "✗ Failed to send passport keys", e)
                _flowState.value = ProofFlowState.Error("Failed to send keys: ${e.message}")
            }
        }
    }

    /**
     * Handles incoming messages from Desktop
     */
    private fun handleIncomingMessage(messageJson: String) {
        try {
            Log.d(TAG, "← Received message: ${messageJson.take(100)}...")

            // Parse message type
            val genericMessage = moshi.adapter(WebRTCMessage::class.java).fromJson(messageJson)

            when (genericMessage?.type) {
                "query_proof_params" -> {
                    Log.d(TAG, "✓ Received query proof params from Desktop")
                    handleQueryProofParams(messageJson)
                }
                else -> {
                    Log.w(TAG, "Unknown message type: ${genericMessage?.type}")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to handle incoming message", e)
        }
    }

    /**
     * Step 2: Handle query proof params and generate proofs
     */
    private fun handleQueryProofParams(messageJson: String) {
        coroutineScope.launch {
            try {
                // Parse params from WebRTC message
                val paramsMessage = moshi.adapter(QueryProofParamsMessage::class.java)
                    .fromJson(messageJson)

                if (paramsMessage == null) {
                    _flowState.value = ProofFlowState.Error("Failed to parse query proof params")
                    return@launch
                }

                val params = paramsMessage.data.attributes
                Log.d(TAG, "Query proof params: eventId=${params.eventId}")

                // Load passport info first
                _flowState.value = ProofFlowState.GeneratingProof("Loading passport info...")
                extIntegratorApiManager.loadPassportInfo()

                // Convert WebRTC params to QueryProofGenResponse format
                // Desktop sends hex strings, need to convert to Long
                val identityCounter = params.identityCounter.toLongOrNull()
                    ?: params.identityCounter.removePrefix("0x").toLongOrNull(16) ?: 0L
                val identityCounterLowerBound = params.identityCounterLowerBound.toLongOrNull()
                    ?: params.identityCounterLowerBound.removePrefix("0x").toLongOrNull(16) ?: 0L
                val identityCounterUpperBound = params.identityCounterUpperBound.toLongOrNull()
                    ?: params.identityCounterUpperBound.removePrefix("0x").toLongOrNull(16) ?: 0L

                val queryProofRequest = QueryProofGenResponse(
                    data = QueryProofGenResponseData(
                        id = params.eventId,
                        type = "get_proof_params",
                        attributes = QueryProofGenResponseAttributes(
                            birth_date_lower_bound = params.birthDateLowerBound,
                            birth_date_upper_bound = params.birthDateUpperBound,
                            citizenship_mask = params.citizenshipMask,
                            current_date = params.currentDate,
                            event_data = params.eventData,
                            event_id = params.eventId,
                            expiration_date_lower_bound = params.expirationDateLowerBound,
                            expiration_date_upper_bound = params.expirationDateUpperBound,
                            identity_counter = identityCounter,
                            identity_counter_lower_bound = identityCounterLowerBound,
                            identity_counter_upper_bound = identityCounterUpperBound,
                            selector = params.selector,
                            timestamp_lower_bound = params.timestampLowerBound,
                            timestamp_upper_bound = params.timestampUpperBound,
                            callback_url = "" // Not used in WebRTC - direct connection
                        )
                    )
                )

                // Generate query proof using ExtIntegratorApiManager (Plonk)
                _flowState.value = ProofFlowState.GeneratingProof("Generating query proof...")
                val queryProof = extIntegratorApiManager.generateQueryProofPlonk(context, queryProofRequest)

                if (queryProof == null) {
                    _flowState.value = ProofFlowState.Error("Failed to generate query proof")
                    return@launch
                }

                Log.d(TAG, "✓ Query proof generated (Plonk)")

                // Get registration proof
                _flowState.value = ProofFlowState.GeneratingProof("Getting registration proof...")
                val registrationProof = identityManager.registrationProof.value
                if (registrationProof == null) {
                    _flowState.value = ProofFlowState.Error("Registration proof not found")
                    return@launch
                }

                // Send query proof to Desktop (with registration data if needed)
                sendQueryProof(queryProof, registrationProof, paramsMessage.data.needsRegistration, paramsMessage.data.userAddress)

            } catch (e: Exception) {
                Log.e(TAG, "✗ Failed to generate query proof", e)
                _flowState.value = ProofFlowState.Error("Proof generation failed: ${e.message}")
            }
        }
    }

    /**
     * Step 3: Send query proof to Desktop (with optional registration data)
     */
    private fun sendQueryProof(
        queryProof: UniversalProof,
        registrationProof: UniversalProof,
        needsRegistration: Boolean,
        userAddress: String
    ) {
        coroutineScope.launch {
            try {
                _flowState.value = ProofFlowState.SendingProofs

                // Extract zkPoints (proof bytes only, without public signals)
                val zkPoints = when (queryProof) {
                    is UniversalProof.Plonk -> {
                        // queryProof.proof.proof contains 0x prefix + proof bytes only (no public signals)
                        val proofHex = queryProof.proof.proof
                        Log.d(TAG, "=== Query Proof Public Signals ===")
                        Log.d(TAG, "Public signals count: ${queryProof.proof.pub_signals.size}")
                        queryProof.proof.pub_signals.forEachIndexed { index, signal ->
                            Log.d(TAG, "  [$index] = $signal")
                        }
                        Log.d(TAG, "Proof hex starts: ${proofHex.take(100)}")
                        Log.d(TAG, "Proof hex length: ${proofHex.length} chars")
                        Log.d(TAG, "=================================")
                        proofHex
                    }
                    is UniversalProof.Groth -> {
                        Gson().toJson(queryProof.proof.proof)
                    }
                    is UniversalProof.Light -> {
                        throw Exception("Light registration not supported for WebRTC")
                    }
                }

                // Build registration data if needed
                val registrationData = if (needsRegistration) {
                    buildRegistrationData(registrationProof)
                } else {
                    null
                }

                val responseData = QueryProofResponseData(
                    zkPoints = zkPoints, // Already has 0x prefix from PlonkProof.proof
                    registration = registrationData
                )

                val message = QueryProofMessage(data = responseData)
                val json = moshi.adapter(QueryProofMessage::class.java).toJson(message)

                Log.d(TAG, "Sending query proof:")
                Log.d(TAG, "  zkPoints: ${zkPoints.take(50)}...")
                Log.d(TAG, "  needsRegistration: $needsRegistration")

                val success = webRTCManager.sendMessage(json)

                if (success) {
                    Log.d(TAG, "✓ Query proof sent successfully!")
                    _flowState.value = ProofFlowState.Completed
                } else {
                    _flowState.value = ProofFlowState.Error("Failed to send query proof")
                }

            } catch (e: Exception) {
                Log.e(TAG, "✗ Failed to send query proof", e)
                _flowState.value = ProofFlowState.Error("Failed to send proof: ${e.message}")
            }
        }
    }

    /**
     * Build registration data from UniversalProof (Plonk)
     */
    private fun buildRegistrationData(registrationProof: UniversalProof): RegistrationData {
        val eDocument = passportManager.passport.value
            ?: throw Exception("Passport not found")

        // Get registration proof data
        val (registrationZkPoints, regPubSignals) = when (registrationProof) {
            is UniversalProof.Plonk -> {
                // For Plonk: proof hex is the zkPoints
                registrationProof.proof.proof to registrationProof.proof.pub_signals
            }
            is UniversalProof.Groth -> {
                // For Groth16: serialize proof as zkPoints
                Gson().toJson(registrationProof.proof.proof) to registrationProof.proof.pub_signals
            }
            is UniversalProof.Light -> {
                throw Exception("Light registration not supported for WebRTC")
            }
        }

        // Extract passport data from eDocument
        val circuitType = eDocument.getRegisterIdentityCircuitType()
        val circuitName = circuitType.buildName()

        val dataType = PassportTypeUtils.getPassportDataType(circuitType)
        val zkType = PassportTypeUtils.getVerifierType(circuitName)

        Log.d(TAG, "=== Passport Type Computation ===")
        Log.d(TAG, "Circuit name: $circuitName")
        Log.d(TAG, "Verifier name: Z_NOIR_PASSPORT_${circuitName.removePrefix("registerIdentity_")}")
        Log.d(TAG, "Passport dataType: $dataType")
        Log.d(TAG, "Verifier zkType: $zkType")
        Log.d(TAG, "===================================")

        // Get passport hash from proof in hex format (with 0x prefix and 32-byte formatted)
        val passportHashHex = when (registrationProof) {
            is UniversalProof.Plonk -> registrationProof.getPassportHashHex()
            else -> {
                // For Groth/Light - pub_signals are already strings, convert to hex
                val decimal = java.math.BigInteger(registrationProof.getPassportHash())
                val hexStr = org.web3j.utils.Numeric.toHexStringNoPrefix(decimal.toByteArray())
                "0x" + hexStr.padStart(64, '0')
            }
        }

        // Get AA public key from DG15 (modulus for RSA or X,Y coordinates for ECDSA)
        val aaPublicKeyHex = eDocument.getAAPublicKeyHex() ?: "0x"

        // Get AA signature (Active Authentication signature)
        val aaSignatureHex = eDocument.aaSignature?.let {
            val hex = org.web3j.utils.Numeric.toHexString(it)
            if (hex.startsWith("0x")) hex else "0x$hex"
        } ?: "0x"

        val passportData = PassportData(
            dataType = dataType,  // Computed from passport AA algorithm
            zkType = zkType,      // Computed from circuit name
            signature =  aaSignatureHex,
            publicKey = aaPublicKeyHex,
            passportHash = passportHashHex
        )

        // Convert public signals from decimal to hex
        val passportKeyDecimal = regPubSignals.getOrNull(0) ?: "0"
        val dgCommitDecimal = regPubSignals.getOrNull(2) ?: "0"
        val identityKeyDecimal = regPubSignals.getOrNull(3) ?: "0"
        val certificatesRootDecimal = regPubSignals.getOrNull(4) ?: "0"

        val passportKeyHex = if (passportKeyDecimal == "0") {
            "0x0"
        } else {
            val decimal = java.math.BigInteger(passportKeyDecimal)
            org.web3j.utils.Numeric.toHexString(decimal.toByteArray())
        }

        val dgCommitHex = if (dgCommitDecimal == "0") {
            "0x0"
        } else {
            val decimal = java.math.BigInteger(dgCommitDecimal)
            org.web3j.utils.Numeric.toHexString(decimal.toByteArray())
        }

        val identityKeyHex = if (identityKeyDecimal == "0") {
            "0x0"
        } else {
            val decimal = java.math.BigInteger(identityKeyDecimal)
            org.web3j.utils.Numeric.toHexString(decimal.toByteArray())
        }

        val certificatesRootHex = if (certificatesRootDecimal == "0") {
            "0x0"
        } else {
            val decimal = java.math.BigInteger(certificatesRootDecimal)
            org.web3j.utils.Numeric.toHexString(decimal.toByteArray())
        }

        Log.d(TAG, "Building registration data:")
        Log.d(TAG, "  publicKey: ${passportData.publicKey.take(20)}...")
        Log.d(TAG, "  passportHash: ${passportData.passportHash.take(20)}...")
        Log.d(TAG, "  passportKey: $passportKeyHex")
        Log.d(TAG, "  identityKey: $identityKeyHex")
        Log.d(TAG, "  dgCommit: $dgCommitHex")
        Log.d(TAG, "  certificatesRoot: $certificatesRootHex")
        Log.d(TAG, "  registrationZkPoints: ${registrationZkPoints.take(50)}...")

        return RegistrationData(
            certificatesRoot = certificatesRootHex,
            dgCommit = dgCommitHex,
            passportKey = passportKeyHex,
            identityKey = identityKeyHex,
            passport = passportData,
            zkPoints = registrationZkPoints
        )
    }

    /**
     * Resets the flow state
     */
    suspend fun reset() {
        _flowState.value = ProofFlowState.Idle
        webRTCManager.disconnect()
    }
}
