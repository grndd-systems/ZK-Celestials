package com.grnddsystems.celestials.api.ext_integrator

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.grnddsystems.celestials.BaseConfig
import com.grnddsystems.celestials.R
import com.grnddsystems.celestials.api.ext_integrator.models.LightSignatureCallbackRequest
import com.grnddsystems.celestials.api.ext_integrator.models.LightSignatureCallbackRequestAttributes
import com.grnddsystems.celestials.api.ext_integrator.models.LightSignatureCallbackRequestData
import com.grnddsystems.celestials.api.ext_integrator.models.QueryProofGenCallbackRequest
import com.grnddsystems.celestials.api.ext_integrator.models.QueryProofGenCallbackRequestAttributes
import com.grnddsystems.celestials.api.ext_integrator.models.QueryProofGenCallbackRequestData
import com.grnddsystems.celestials.api.ext_integrator.models.QueryProofGenResponse
import com.grnddsystems.celestials.contracts.rarimo.StateKeeper
import com.grnddsystems.celestials.data.ProofTxFull
import com.grnddsystems.celestials.manager.IdentityManager
import com.grnddsystems.celestials.manager.PassportManager
import com.grnddsystems.celestials.manager.RarimoContractManager
import com.grnddsystems.celestials.store.SecureSharedPrefsManager
import com.grnddsystems.celestials.util.ZKPUseCase
import com.grnddsystems.celestials.util.ZkpUtil
import com.grnddsystems.celestials.util.data.GrothProof
import com.grnddsystems.celestials.util.decodeHexString
import com.grnddsystems.celestials.util.data.UniversalProof
import com.grnddsystems.celestials.util.data.UniversalProofFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.web3j.utils.Numeric
import javax.inject.Inject

class ExtIntegratorApiManager @Inject constructor(
    private val extIntegratorAPI: ExtIntegratorAPI,
    private val contractManager: RarimoContractManager,
    private val sharedPreferences: SecureSharedPrefsManager,
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager,
) {
    suspend fun queryProofCallback(url: String, proof: GrothProof, userIdHash: String) {
        return withContext(Dispatchers.IO) {
            val payload = QueryProofGenCallbackRequest(
                data = QueryProofGenCallbackRequestData(
                    id = userIdHash,
                    attributes = QueryProofGenCallbackRequestAttributes(
                        proof = proof
                    )
                )
            )
            val str = Gson().toJson(payload)
            Log.i("payload", str)
            try {
                extIntegratorAPI.queryProofCallback(
                    url,
                    payload
                )
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun lightSignatureCallback(
        url: String,
        pubSignals: List<String>,
        signature: String,
        userIdHash: String
    ) {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.lightSignatureCallback(
                    url,
                    LightSignatureCallbackRequest(
                        data = LightSignatureCallbackRequestData(
                            id = userIdHash,
                            attributes = LightSignatureCallbackRequestAttributes(
                                pub_signals = pubSignals,
                                signature = signature
                            )
                        )
                    )
                )
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun queryProofData(url: String): QueryProofGenResponse {
        return withContext(Dispatchers.IO) {
            try {
                extIntegratorAPI.queryProofData(url)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun queryIpfsData(url: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = extIntegratorAPI.queryIpfsData(url)

                Gson().toJson(response)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private var _passportInfo = MutableStateFlow<StateKeeper.PassportInfo?>(null)
    val passportInfo: StateFlow<StateKeeper.PassportInfo?>
        get() = _passportInfo.asStateFlow()

    // In new architecture, SessionInfo replaces IdentityInfo
    private var _identityInfo = MutableStateFlow<StateKeeper.SessionInfo?>(null)
    val identityInfo: StateFlow<StateKeeper.SessionInfo?>
        get() = _identityInfo.asStateFlow()

    suspend fun loadPassportInfo() {
        try {
            Log.d("ExtIntegrator", "→ Loading passport info...")
            val eDocument = passportManager.passport.value

            if (eDocument == null) {
                Log.e("ExtIntegrator", "✗ eDocument is null")
                return
            }

            val passportInfoKey = passportManager.getPassportInfoKeyBytes(
                eDocument,
                identityManager.registrationProof.value!!
            )

            val stateKeeperContract = contractManager.getStateKeeper()

            val passportInfo = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportInfo(passportInfoKey).send()
            }

            _passportInfo.value = passportInfo
            Log.d("ExtIntegrator", "✓ Passport info loaded: activeSessionCount=${passportInfo.activeSessionCount}")

            // Get session info to populate identityInfo
            // In new architecture, identity info comes from sessions
            val sessionsInfo = withContext(Dispatchers.IO) {
                stateKeeperContract.getPassportSessionsInfo(passportInfoKey).send()
            }

            Log.d("ExtIntegrator", "Sessions found: ${sessionsInfo.value2.size}")

            // Use the first active session if available
            if (!sessionsInfo.value2.isEmpty()) {
                val firstSession = sessionsInfo.value2[0]
                // Create a mock IdentityInfo from SessionInfo for backwards compatibility
                _identityInfo.value = StateKeeper.SessionInfo(
                    firstSession.activePassport,
                    firstSession.issueTimestamp
                )
                Log.d("ExtIntegrator", "✓ Identity info populated from session: issueTimestamp=${firstSession.issueTimestamp}")
            } else {
                Log.w("ExtIntegrator", "No sessions found in contract - using mock session data")
                // For WebRTC: if no sessions in contract, create mock with zero values
                // Timestamp 0 means timestamp verification will be skipped in query proof
                _identityInfo.value = StateKeeper.SessionInfo(
                    ByteArray(32), // Empty 32-byte array for activePassport
                    java.math.BigInteger.ZERO // timestamp = 0
                )
                Log.d("ExtIntegrator", "✓ Mock identity info created with zero timestamp")
            }
        } catch (e: Exception) {
            Log.e("ExtIntegrator", "✗ Failed to load passport info", e)
            throw e
        }
    }

    private suspend fun getQueryParams(): Pair<ByteArray, String> {

        val registrationSmtContract = contractManager.getPoseidonSMT(
            BaseConfig.REGISTRATION_SMT_CONTRACT_ADDRESS
        )

        val passportInfoKey = passportManager.getPassportInfoKey(
            passportManager.passport.value!!,
            identityManager.registrationProof.value!!,
        )

        val proofIndex = passportManager.getProofIndex(
            passportInfoKey
        )

        val smtProofRaw = withContext(Dispatchers.IO) {
            registrationSmtContract.getProof(proofIndex).send()
        }
        val smtProof = ProofTxFull.fromContractProof(smtProofRaw)
        val smtProofJson = Gson().toJson(smtProof)

        return Pair(smtProofJson.toByteArray(), passportInfoKey)

    }

    suspend fun generateQueryProofPlonk(
        context: Context,
        queryProofParametersRequest: QueryProofGenResponse
    ): UniversalProof? {
        try {
            Log.d("PlonkQuery", "=== Starting generateQueryProofPlonk ===")

            if (passportInfo.value == null) {
                Log.e("PlonkQuery", "✗ passportInfo is null")
                return null
            }
            if (identityInfo.value == null) {
                Log.e("PlonkQuery", "✗ identityInfo is null")
                return null
            }

            Log.d("PlonkQuery", "✓ passportInfo and identityInfo are available")

            // Build inputs for Plonk query proof
            Log.d("PlonkQuery", "→ Building query inputs...")
            val inputs = buildPlonkQueryInputs(queryProofParametersRequest)
            Log.d("PlonkQuery", "✓ Query inputs built: ${inputs.keys}")

            // Read bytecode from assets
            Log.d("PlonkQuery", "→ Reading circuit bytecode from assets...")
            val assetContext: Context = context.createPackageContext("com.grnddsystems.celestials", 0)
            val assetManager = assetContext.assets
            val circuitByteCode = assetManager.open("query.json").bufferedReader().use { it.readText() }
            Log.d("PlonkQuery", "✓ Circuit bytecode loaded: ${circuitByteCode.length} bytes")

            // Download trusted setup (same as for registration)
            Log.d("PlonkQuery", "→ Downloading trusted setup...")
            val circuitDownloader = com.grnddsystems.celestials.modules.passportScan.CircuitNoirDownloader(context)
            val trustedSetupPath = circuitDownloader.downloadTrustedSetup { progress, isEnded ->
                Log.d("PlonkQuery", "Trusted setup download: $progress% ${if (isEnded) "(done)" else ""}")
            }
            Log.d("PlonkQuery", "✓ Trusted setup ready at: $trustedSetupPath")

            val customDispatcher = java.util.concurrent.Executors.newFixedThreadPool(1) { runnable ->
                Thread(null, runnable, "LargeStackThread", 100 * 1024 * 1024) // 100 MB stack size
            }.asCoroutineDispatcher()

            return withContext(customDispatcher) {
                try {
                    Log.d("PlonkQuery", "→ Creating circuit from JSON manifest...")
                    val circuit = com.noirandroid.lib.Circuit.fromJsonManifest(circuitByteCode)
                    Log.d("PlonkQuery", "✓ Circuit created")

                    Log.d("PlonkQuery", "→ Setting up SRS...")
                    circuit.setupSrs(trustedSetupPath, false)
                    Log.d("PlonkQuery", "✓ SRS setup complete")

                    Log.d("PlonkQuery", "→ Starting Plonk query proof generation...")
                    val noirProof = circuit.prove(inputs, proofType = "plonk", recursive = false)
                    Log.d("PlonkQuery", "✓ Proof generated: ${noirProof.proof.take(50)}...")

                    Log.d("PlonkQuery", "→ Parsing PlonkProof for query circuit...")
                    // Use PlonkProof.fromHexString() for automatic public signal detection
                    //val plonkProof = com.grnddsystems.celestials.util.data.PlonkProof.fromHexString(noirProof.proof)
                    val plonkProof = UniversalProofFactory.fromPlonkBytes(Numeric.hexStringToByteArray(noirProof.proof))
                    plonkProof
                } catch (e: Exception) {
                    Log.e("PlonkQuery", "✗ Error in proof generation", e)
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e("PlonkQuery", "✗ generateQueryProofPlonk failed", e)
            return null
        }
    }

    private fun buildPlonkQueryInputs(
        queryProofParametersRequest: QueryProofGenResponse
    ): Map<String, Any> {
        try {
            Log.d("PlonkQuery", "  → Extracting DG1...")
            val dg1 = passportManager.passport.value!!.dg1!!.decodeHexString()
            Log.d("PlonkQuery", "  ✓ DG1 extracted: ${dg1.size} bytes")

            // Get identity key from registration proof (pub_signals[3])
            // This is a large number (field element), keep as BigInteger
            val pkIdentity = identityManager.registrationProof.value!!.getIdentityKey().toBigInteger()
            val selector = queryProofParametersRequest.data.attributes.selector
            val issueTimestamp = identityInfo.value?.issueTimestamp?.toLong() ?: 0L
            val identityCounter = passportInfo.value?.activeSessionCount?.toLong() ?: 0L
            val eventID = queryProofParametersRequest.data.attributes.event_id
            val eventData = queryProofParametersRequest.data.attributes.event_data

            // Use current_date from contract (not generated by mobile)
            val currentDateEncoded = queryProofParametersRequest.data.attributes.current_date

            val timestampUpperbound = queryProofParametersRequest.data.attributes.timestamp_upper_bound

            val identityCountUpperbound = 0


            // Convert to hex list for Plonk (u8 array)
            val toHexList: (ByteArray) -> List<String> = { bytes ->
                bytes.map { byte -> Numeric.toHexString(byteArrayOf(byte)) }
            }

            Log.d("PlonkQuery", "  → Getting private key...")
            val privateKey = identityManager.privateKeyBytes
                ?: throw IllegalStateException("Private key not found")
            Log.d("PlonkQuery", "  ✓ Private key obtained")

            Log.d("PlonkQuery", "  Input values:")
            Log.d("PlonkQuery", "    eventID: $eventID")
            Log.d("PlonkQuery", "    eventData: $eventData")
            Log.d("PlonkQuery", "    selector: $selector")
            Log.d("PlonkQuery", "    currentDate: $currentDateEncoded")
            Log.d("PlonkQuery", "    pkIdentity: $pkIdentity")
            Log.d("PlonkQuery", "    issueTimestamp: $issueTimestamp")
            Log.d("PlonkQuery", "    identityCounter: $identityCounter")

            // Helper to convert Long to hex string
            val toHexString: (Long) -> String = { value ->
                "0x${value.toString(16).padStart(1, '0')}"
            }

            // Helper to convert BigInteger to hex string
            val bigIntToHexString: (java.math.BigInteger) -> String = { value ->
                "0x${value.toString(16)}"
            }

            // Based on ABI: event_id, event_data, selector, current_date, timestamp_lowerbound, timestamp_upperbound,
            // identity_count_lowerbound, identity_count_upperbound, birth_date_lowerbound, birth_date_upperbound,
            // expiration_date_lowerbound, expiration_date_upperbound, citizenship_mask, pk_identity, sk_identity,
            // dg1 (u8 array length 93), timestamp, identity_counter
            // Circuit expects ALL parameters as hex strings with 0x prefix
            return mapOf(
                "event_id" to eventID,
                "event_data" to eventData,
                "selector" to selector,
                "current_date" to currentDateEncoded,
                "timestamp_lowerbound" to queryProofParametersRequest.data.attributes.timestamp_lower_bound,
                "timestamp_upperbound" to queryProofParametersRequest.data.attributes.timestamp_upper_bound,
                "identity_count_lowerbound" to toHexString(queryProofParametersRequest.data.attributes.identity_counter_lower_bound),
                "identity_count_upperbound" to toHexString(queryProofParametersRequest.data.attributes.identity_counter_upper_bound),
                "birth_date_lowerbound" to queryProofParametersRequest.data.attributes.birth_date_lower_bound,
                "birth_date_upperbound" to queryProofParametersRequest.data.attributes.birth_date_upper_bound,
                "expiration_date_lowerbound" to queryProofParametersRequest.data.attributes.expiration_date_lower_bound,
                "expiration_date_upperbound" to queryProofParametersRequest.data.attributes.expiration_date_upper_bound,
                "citizenship_mask" to queryProofParametersRequest.data.attributes.citizenship_mask,
                "pk_identity" to bigIntToHexString(pkIdentity),
                "sk_identity" to Numeric.toHexString(privateKey),
                "dg1" to toHexList(dg1),
                "timestamp" to toHexString(issueTimestamp.toLong()),
                "identity_counter" to toHexString(identityCounter.toLong())
            )
        } catch (e: Exception) {
            Log.e("PlonkQuery", "  ✗ Error building inputs", e)
            throw e
        }
    }

    suspend fun generateQueryProof(
        context: Context,
        queryProofParametersRequest: QueryProofGenResponse
    ): GrothProof? {
        if (passportInfo.value == null || identityInfo.value == null) return null

        val assetContext: Context = context.createPackageContext("com.grnddsystems.celestials", 0)
        val assetManager = assetContext.assets

        val zkp = ZKPUseCase(context, assetManager)

        val queryParams = getQueryParams()

        val profiler = identityManager.getProfiler()

        // In new architecture, use activeSessionCount instead of identityReissueCounter
        val targets_identity_counter_upper_bound =
            if (passportInfo.value!!.activeSessionCount.toLong() > queryProofParametersRequest.data.attributes.identity_counter_upper_bound) passportInfo.value!!.activeSessionCount.toString()
            else queryProofParametersRequest.data.attributes.identity_counter_upper_bound.toString()

        val dg1 = passportManager.passport.value!!.dg1!!.decodeHexString()
        val smtProofJSON = queryParams.first
        val selector = queryProofParametersRequest.data.attributes.selector
        val pkPassportHash = queryParams.second
        val issueTimestamp = identityInfo.value!!.issueTimestamp.toString()
        // In new architecture, use activeSessionCount instead of identityReissueCounter
        val identityCounter = passportInfo.value!!.activeSessionCount.toString()
        val eventID = queryProofParametersRequest.data.attributes.event_id
        val eventData = queryProofParametersRequest.data.attributes.event_data
        val TimestampLowerbound = queryProofParametersRequest.data.attributes.timestamp_lower_bound

        val TimestampUpperbound =
            if (identityInfo.value!!.issueTimestamp.toString()
                    .toULong() >= queryProofParametersRequest.data.attributes.timestamp_upper_bound.toULong()
            )
                (identityInfo.value!!.issueTimestamp.toString().toULong() + 1u).toString()
            else
                queryProofParametersRequest.data.attributes.timestamp_upper_bound

        val IdentityCounterLowerbound =
            queryProofParametersRequest.data.attributes.identity_counter_lower_bound.toString()
        // In new architecture, use activeSessionCount instead of identityReissueCounter
        val IdentityCounterUpperbound =
            (passportInfo.value!!.activeSessionCount.toLong() + 1).toString()
        val ExpirationDateLowerbound =
            queryProofParametersRequest.data.attributes.expiration_date_lower_bound
        val ExpirationDateUpperbound =
            queryProofParametersRequest.data.attributes.expiration_date_upper_bound // largets_identity_counter_upper_bound
        val BirthDateLowerbound = queryProofParametersRequest.data.attributes.birth_date_lower_bound
        val BirthDateUpperbound = queryProofParametersRequest.data.attributes.birth_date_upper_bound
        val CitizenshipMask = queryProofParametersRequest.data.attributes.citizenship_mask

        Log.i(
            "generateQueryProof", """
            dg1: ${Numeric.toHexString(dg1)}
            smtProofJSON: ${smtProofJSON.decodeToString()}
            selector: $selector
            pkPassportHash: $pkPassportHash
            issueTimestamp: $issueTimestamp
            identityCounter: $identityCounter
            eventID: $eventID
            eventData: $eventData
            TimestampLowerbound: $TimestampLowerbound
            TimestampUpperbound: $TimestampUpperbound
            IdentityCounterLowerbound: $IdentityCounterLowerbound
            IdentityCounterUpperbound: $IdentityCounterUpperbound
            ExpirationDateLowerbound: $ExpirationDateLowerbound
            ExpirationDateUpperbound: $ExpirationDateUpperbound
            BirthDateLowerbound: $BirthDateLowerbound
            BirthDateUpperbound: $BirthDateUpperbound
            CitizenshipMask: $CitizenshipMask
        """.trimIndent()
        )

        val queryProofInputs = profiler.buildQueryIdentityInputs(
            dg1,
            smtProofJSON,
            selector,

            pkPassportHash,

            issueTimestamp,
            identityCounter,

            eventID,
            eventData,
            TimestampLowerbound,
            TimestampUpperbound,

            IdentityCounterLowerbound,
            IdentityCounterUpperbound,
            ExpirationDateLowerbound,
            ExpirationDateUpperbound,
            BirthDateLowerbound,
            BirthDateUpperbound,
            CitizenshipMask,
        )

        val queryProof = withContext(Dispatchers.Default) {
            zkp.generateZKP(
                "circuit_query_zkey.zkey",
                R.raw.query_identity_dat,
                queryProofInputs,
                ZkpUtil::queryIdentity
            )
        }

        Log.i("queryProof", Gson().toJson(queryProof))

        return queryProof
    }
}