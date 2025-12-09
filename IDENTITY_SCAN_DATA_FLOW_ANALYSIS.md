# Complete Data Flow Analysis: Identity → Scan MRZ → Scan NFC

## Executive Summary

This document provides a comprehensive technical analysis of the data flow when a user performs the Identity → Scan MRZ → Scan NFC flow in the Rarime Android application. It covers all classes, data structures, storage locations, and proof generation mechanisms.

---

## Table of Contents

1. [Flow Overview](#flow-overview)
2. [Stage 1: Opening Identity Screen](#stage-1-opening-identity-screen)
3. [Stage 2: MRZ Scanning](#stage-2-mrz-scanning)
4. [Stage 3: NFC Scanning](#stage-3-nfc-scanning)
5. [Data Storage Locations](#data-storage-locations)
6. [Proof Generation Flow](#proof-generation-flow)
7. [Data Flow Diagram](#data-flow-diagram)
8. [Key Files and Functions](#key-files-and-functions)

---

## Flow Overview

```
User Action Flow:
1. User taps "Identity" tab → ZkIdentityScreen
2. User scans MRZ → MRZInfo extracted
3. User scans NFC → EDocument created
4. Proof generation → UniversalProof created
5. Registration → On-chain registration
```

---

## Stage 1: Opening Identity Screen

### Entry Point
- **File**: `app/src/main/java/com/rarilabs/rarime/modules/you/ZkIdentityScreen.kt`
- **Composable**: `ZkIdentityScreen()`

### Components Involved

1. **ZkIdentityScreenViewModel**
   - **File**: `app/src/main/java/com/rarilabs/rarime/modules/you/ZkIdentityScreenViewModel.kt`
   - **Purpose**: Manages identity screen state
   - **Key Properties**:
     ```kotlin
     var passport = passportManager.passport  // StateFlow<EDocument?>
     val passportStatus = passportManager.passportStatus
     val performRegistration = proofGenerationManager::performRegistration
     ```

2. **PassportManager**
   - **File**: `app/src/main/java/com/rarilabs/rarime/manager/PassportManager.kt`
   - **Purpose**: Manages passport data persistence
   - **Key State**:
     ```kotlin
     private var _passport = MutableStateFlow(dataStoreManager.readEDocument())
     val passport: StateFlow<EDocument?>
     ```

### Flow Logic

```kotlin
// ZkIdentityScreen.kt:28-35
val passport by zkIdentityScreenViewModel.passport.collectAsState()

if (passport != null) {
    ZkIdentityPassport(navigate = navigate)  // Show existing passport
} else {
    ScanPassportScreen(...)  // Show scanning UI
}
```

### Data State at This Stage
- **In Memory**: `PassportManager._passport` (StateFlow)
- **Persisted**: `SecureSharedPrefsManager.readEDocument()` (encrypted storage)
- **Data Type**: `EDocument?` (null if no passport scanned)

---

## Stage 2: MRZ Scanning

### Entry Point
- **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/ScanPassportScreen.kt`
- **State**: `ScanPassportState.SCAN_MRZ`

### Components Involved

1. **ScanMRZStep**
   - **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/camera/ScanMRZStep.kt`
   - **Composable**: `ScanMRZStep(onNext: (MRZInfo) -> Unit, onClose: () -> Unit)`

2. **CameraScanPassport**
   - **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/camera/CameraScanPassport.kt`
   - **Purpose**: Camera preview and image analysis setup

3. **TextRecognitionAnalyzer**
   - **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/camera/TextRecognitionAnalyzer.kt`
   - **Purpose**: ML Kit text recognition and MRZ parsing

### MRZ Scanning Process

#### Step 1: Camera Setup
```kotlin
// CameraScanPassport.kt:56-69
val imageAnalysisUseCase = ImageAnalysis.Builder()
    .setAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyzer(onDetectedTextUpdated = onMrzDetected)
    )
```

#### Step 2: Image Processing
```kotlin
// TextRecognitionAnalyzer.kt:191-219
override fun analyze(imageProxy: ImageProxy) {
    val mediaImage: Image = imageProxy.image
    val bitmap = mediaImage.toBitmap()
    val grayscaleBitmap = bitmap.toGrayscaleHighContrast()
    val inputImage = InputImage.fromBitmap(grayscaleBitmap, rotationDegrees)
    
    textRecognizer.process(inputImage).addOnSuccessListener { visionText ->
        onProcess(visionText)  // Extract MRZ text
    }
}
```

#### Step 3: MRZ Text Extraction
```kotlin
// TextRecognitionAnalyzer.kt:148-188
private fun filterScannedTextPassport(text: String) {
    val updatedText = text.replace("«", "<<").uppercase().replace("O", "0")
    val match = PASSPORT_TD_3_LINE_2_REGEX.find(updatedText)
    
    // Extract fields:
    val documentNumber = match.value.substring(0, 9)
    val dateOfBirth = match.value.substring(13, 19)
    val expiryDate = match.value.substring(21, 27)
    val nationality = it.value.substring(10, 13)
    
    // Verify checksums
    val check1 = verifyChecksum(dateOfBirthWithCheckSum)
    val check2 = verifyChecksum(documentNumberWithCheckSum)
    val check3 = verifyChecksum(expiryDateWithCheckSum)
    
    // Build MRZInfo
    val mrz = buildTempMrz(documentNumber, dateOfBirth, expiryDate, nationality)
    onDetectedTextUpdated(mrz)  // Callback with MRZInfo
}
```

#### Step 4: MRZInfo Creation
```kotlin
// TextRecognitionAnalyzer.kt:35-63
fun buildTempMrz(
    documentNumber: String, 
    dateOfBirth: String, 
    expiryDate: String, 
    nationality: String
): MRZInfo? {
    mrzInfo = MRZInfo(
        "P",                    // Document type (Passport)
        "NNN",                  // Issuing state
        "",                     // Primary identifier
        "",                     // Secondary identifier
        documentNumber,         // Document number
        nationality,            // Nationality
        dateOfBirth,            // Date of birth (YYMMDD)
        Gender.UNSPECIFIED,     // Gender
        expiryDate,             // Expiry date (YYMMDD)
        ""                      // Optional data
    )
    return if (isMrzValid(mrzInfo)) mrzInfo else null
}
```

### Data Storage at MRZ Stage

#### In-Memory Storage
```kotlin
// ScanPassportScreen.kt:52
var mrzData: MRZInfo? by remember { mutableStateOf(null) }
```

**Location**: `ScanPassportScreen` composable's local state
**Data Type**: `MRZInfo` (from `org.jmrtd.lds.icao.MRZInfo`)
**Fields Stored**:
- `documentNumber: String`
- `dateOfBirth: String` (YYMMDD format)
- `dateOfExpiry: String` (YYMMDD format)
- `nationality: String` (3-letter ISO code)
- `documentCode: String` ("P" for passport)

#### State Transition
```kotlin
// ScanPassportScreen.kt:98-106
ScanPassportState.SCAN_MRZ -> {
    ScanMRZStep(
        onNext = {
            mrzData = it  // Store MRZInfo in local state
            state = ScanPassportState.READ_NFC  // Transition to NFC
        }
    )
}
```

**Important**: MRZ data is **NOT persisted** at this stage. It's only stored in composable's `remember` state and passed to NFC scanning.

---

## Stage 3: NFC Scanning

### Entry Point
- **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/ScanPassportScreen.kt`
- **State**: `ScanPassportState.READ_NFC`

### Components Involved

1. **ReadEDocStep**
   - **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/nfc/ReadEDocStep.kt`
   - **Composable**: `ReadEDocStep(mrzInfo: MRZInfo, onNext: (EDocument) -> Unit)`

2. **ReadEDocStepViewModel**
   - **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/models/ReadEDocStepViewModel.kt`
   - **Purpose**: Manages NFC scanning state and EDocument creation

3. **NfcManager**
   - **File**: `app/src/main/java/com/rarilabs/rarime/manager/NfcManager.kt`
   - **Purpose**: Handles NFC tag detection

4. **NfcUseCase**
   - **File**: `app/src/main/java/com/rarilabs/rarime/modules/passportScan/nfc/NfcUseCase.kt`
   - **Purpose**: Extracts data from passport NFC chip

### NFC Scanning Process

#### Step 1: Initialize NFC Scanning
```kotlin
// ReadEDocStepViewModel.kt:82-85
fun startScanning(mrzInfo: MRZInfo) {
    this.mrzInfo = mrzInfo  // Store MRZ for BAC key generation
    nfcManager.startScanning(::handleScan, onError = { onError(it) })
}
```

#### Step 2: NFC Tag Detected
```kotlin
// ReadEDocStepViewModel.kt:47-75
private fun handleScan(tag: Tag) {
    val birthDate = mrzInfo.dateOfBirth
    val expirationDate = mrzInfo.dateOfExpiry
    val passportNumber = mrzInfo.documentNumber
    
    // Create BAC key from MRZ data
    bacKey = BACKey(passportNumber, birthDate, expirationDate)
    
    // Get private key for active authentication
    val privateKeyBytes = identityManager.privateKeyBytes
    
    // Initialize NFC use case
    val isoDep = IsoDep.get(tag)
    scanNfcUseCase = NfcUseCase(isoDep, bacKey, privateKeyBytes!!, _currentNfcScanStep)
    
    // Scan passport and create EDocument
    eDocument = scanNfcUseCase.scanPassport()
}
```

#### Step 3: Passport Data Extraction (NfcUseCase.scanPassport)

**3.1. PACE/BAC Authentication**
```kotlin
// NfcUseCase.kt:78-110
service.open()
var paceSucceeded = false

// Try PACE (if available)
val cardSecurityFile = CardSecurityFile(service.getInputStream(PassportService.EF_CARD_SECURITY))
for (securityInfo in securityInfoCollection) {
    if (securityInfo is PACEInfo) {
        service.doPACE(bacKey, paceInfo.objectIdentifier, ...)
        paceSucceeded = true
    }
}

// Fallback to BAC if PACE failed
if (!paceSucceeded) {
    service.doBAC(bacKey)
}
```

**3.2. Read SOD (Security Object Document)**
```kotlin
// NfcUseCase.kt:116-161
currentStateMutableFlow.value = NfcScanStep.SOD

val sodIn = service.getInputStream(PassportService.EF_SOD)
val sodFile = SODFileOwn(sodIn)

// Store SOD as hex string
val sod = cropByteArray(byteArray, byteLen).toHexString()
eDocument.sod = sod

// Extract certificate and signature info
val docSigningCert = sodFile.docSigningCertificate
val pemFile = SecurityUtil.convertToPEM(docSigningCert)
val digestAlgorithm = sodFile.digestAlgorithm
val digestEncryptionAlgorithm = sodFile.digestEncryptionAlgorithm
```

**3.3. Read DG1 (Data Group 1 - Personal Details)**
```kotlin
// NfcUseCase.kt:162-199
currentStateMutableFlow.value = NfcScanStep.DG1SCAN

val dg1In = service.getInputStream(PassportService.EF_DG1)
val dg1File = DG1File(dg1In)
var encodedDg1File = dg1File.encoded.toHexString()
val mrzInfo = dg1File.mrzInfo

// Extract personal details
personDetails.name = mrzInfo.secondaryIdentifier.replace("<", " ").trim()
personDetails.surname = mrzInfo.primaryIdentifier.replace("<", " ").trim()
personDetails.personalNumber = mrzInfo.personalNumber
personDetails.gender = mrzInfo.gender.toString()
personDetails.birthDate = DateUtil.convertFromMrzDate(mrzInfo.dateOfBirth)
personDetails.expiryDate = DateUtil.convertFromMrzDate(mrzInfo.dateOfExpiry)
personDetails.serialNumber = mrzInfo.documentNumber
personDetails.nationality = mrzInfo.nationality.replace("<", "")
personDetails.issuerAuthority = mrzInfo.issuingState.replace("<", "")

eDocument.dg1 = encodedDg1File

// Verify DG1 hash against SOD
val dg1StoredHash = sodFile.dataGroupHashes[1]
val dg1ComputedHash = digest.digest(encodedDg1File.toByteArray())
if (Arrays.equals(dg1StoredHash, dg1ComputedHash)) {
    hashesMatched = true
}
```

**3.4. Read DG2 (Data Group 2 - Face Image)**
```kotlin
// NfcUseCase.kt:201-228
currentStateMutableFlow.value = NfcScanStep.DG2SCAN

val dg2In = service.getInputStream(PassportService.EF_DG2)
val dg2File = DG2File(dg2In)

// Verify DG2 hash
val dg2StoredHash = sodFile.dataGroupHashes[2]
val dg2ComputedHash = digest.digest(dg2File.encoded)
if (Arrays.equals(dg2StoredHash, dg2ComputedHash)) {
    hashesMatched = true
}

// Extract face image
val faceInfos = dg2File.faceInfos
val allFaceImageInfos: MutableList<FaceImageInfo> = ArrayList()
for (faceInfo in faceInfos) {
    allFaceImageInfos.addAll(faceInfo.faceImageInfos)
}
if (allFaceImageInfos.isNotEmpty()) {
    personDetails.faceImageInfo = allFaceImageInfos.iterator().next()
}
```

**3.5. Read DG15 (Data Group 15 - Active Authentication Public Key)**
```kotlin
// NfcUseCase.kt:236-276
currentStateMutableFlow.value = NfcScanStep.DG15Scan

val dg15 = try {
    val dG15File = service.getInputStream(PassportService.EF_DG15, 256)
    Dg15FileOwn(dG15File)
} catch (e: Exception) {
    null  // DG15 is optional
}

eDocument.dg15 = dg15?.encoded?.toHexString()
eDocument.dg15Pem = dg15?.publicKey?.publicKeyToPem()

// Perform Active Authentication (if DG15 available)
if (dg15 != null) {
    val profiler = Profile().newProfile(privateKey).registrationChallenge
    try {
        val response = service.doAA(
            dg15.publicKey,
            sodFile.digestAlgorithm,
            sodFile.signerInfoDigestAlgorithm,
            profiler
        )
        eDocument.aaSignature = response.response
        eDocument.aaResponse = response.toString()
        eDocument.isActiveAuth = true
    } catch (e: Exception) {
        eDocument.isActiveAuth = false
    }
}
```

**3.6. Finalize EDocument**
```kotlin
// NfcUseCase.kt:230-233
eDocument.docType = docType  // PASSPORT or ID_CARD
eDocument.personDetails = personDetails
eDocument.additionalPersonDetails = additionalPersonDetails
eDocument.isPassiveAuth = hashesMatched  // SOD verification result

return eDocument
```

### EDocument Data Structure

```kotlin
// EDocument.kt:42-55
data class EDocument(
    var docType: DocType? = null,                    // PASSPORT, ID_CARD, OTHER
    var personDetails: PersonDetails? = null,         // Personal information
    var additionalPersonDetails: AdditionalPersonDetails? = null,
    var isPassiveAuth: Boolean = false,              // SOD hash verification
    var isActiveAuth: Boolean = false,               // Active Authentication result
    var isChipAuth: Boolean = false,
    var sod: String? = null,                         // SOD file (hex string)
    var dg1: String? = null,                         // DG1 file (hex string)
    var dg15: String? = null,                        // DG15 file (hex string)
    var dg15Pem: String? = null,                     // DG15 public key (PEM)
    var aaSignature: ByteArray? = null,             // Active Auth signature
    var aaResponse: String? = null                    // Active Auth response
)
```

**PersonDetails Structure**:
```kotlin
// PersonDetails.kt:7-22
data class PersonDetails(
    var name: String? = null,
    var surname: String? = null,
    var personalNumber: String? = null,
    var gender: String? = null,
    var birthDate: String? = null,
    var expiryDate: String? = null,
    var serialNumber: String? = null,
    var nationality: String? = null,
    var issuerAuthority: String? = null,
    var faceImageInfo: FaceImageInfo? = null,        // Face biometric data
    var portraitImageBase64: String? = null,
    var signature: Bitmap? = null,
    var signatureBase64: String? = null,
    var fingerprints: List<Bitmap>? = null
)
```

### Data Storage After NFC Scan

#### Step 1: Temporary Storage in ViewModel
```kotlin
// ReadEDocStepViewModel.kt:27-28
lateinit var eDocument: EDocument
    private set
```

#### Step 2: Pass to ScanPassportScreenViewModel
```kotlin
// ScanPassportScreen.kt:108-123
ScanPassportState.READ_NFC -> {
    ReadEDocStep(
        onNext = {
            scanPassportScreenViewModel.setPassportTEMP(it)  // Store temporarily
            scanPassportScreenViewModel.savePassport()      // Persist to storage
            setVisibilityOfBottomBar(true)
        },
        mrzInfo = mrzData!!
    )
}
```

#### Step 3: Store in RegistrationManager
```kotlin
// ScanPassportScreenViewModel.kt:47-49
fun setPassportTEMP(eDocument: EDocument?) {
    registrationManager.setEDocument(eDocument)
}
```

```kotlin
// RegistrationManager.kt:84-86
fun setEDocument(eDocument: EDocument?) {
    _eDocument.value = eDocument  // StateFlow<EDocument?>
}
```

#### Step 4: Persist to Secure Storage
```kotlin
// ScanPassportScreenViewModel.kt:51-56
fun savePassport() {
    registrationManager.eDocument.value?.let {
        passportManager.setPassport(eDocument.value)
        passportManager.updatePassportStatus(status = PassportStatus.UNREGISTERED)
    }
}
```

```kotlin
// PassportManager.kt:74-79
fun setPassport(passport: EDocument?) {
    if (passport != null) {
        dataStoreManager.saveEDocument(passport)  // Encrypted storage
    }
    _passport.value = passport  // Update StateFlow
}
```

---

## Data Storage Locations

### Complete Storage Map

#### 1. **In-Memory (Composable State)**
- **Location**: `ScanPassportScreen` composable
- **Data**: `mrzData: MRZInfo?`
- **Lifetime**: Until composable is disposed
- **File**: `ScanPassportScreen.kt:52`

#### 2. **ViewModel State (ReadEDocStepViewModel)**
- **Location**: `ReadEDocStepViewModel.eDocument`
- **Data**: `EDocument` (lateinit var)
- **Lifetime**: ViewModel lifecycle
- **File**: `ReadEDocStepViewModel.kt:27-28`

#### 3. **RegistrationManager State**
- **Location**: `RegistrationManager._eDocument`
- **Data**: `StateFlow<EDocument?>`
- **Lifetime**: Application lifecycle (Singleton)
- **File**: `RegistrationManager.kt:58-60`

#### 4. **PassportManager State**
- **Location**: `PassportManager._passport`
- **Data**: `StateFlow<EDocument?>`
- **Lifetime**: Application lifecycle (Singleton)
- **File**: `PassportManager.kt:24-27`

#### 5. **Encrypted Persistent Storage**
- **Location**: `SecureSharedPrefsManager`
- **Method**: `saveEDocument(EDocument)`
- **Storage Type**: Encrypted SharedPreferences/DataStore
- **File**: `SecureSharedPrefsManager.kt:56`
- **Retrieval**: `readEDocument(): EDocument?`

### Storage Flow Diagram

```
MRZ Scanning:
  Camera Frame → TextRecognitionAnalyzer → MRZInfo
  ↓
  ScanPassportScreen.mrzData (remember state)

NFC Scanning:
  NFC Tag → NfcUseCase.scanPassport() → EDocument
  ↓
  ReadEDocStepViewModel.eDocument (lateinit var)
  ↓
  RegistrationManager._eDocument (StateFlow)
  ↓
  PassportManager.setPassport() → SecureSharedPrefsManager.saveEDocument()
  ↓
  Encrypted Storage (persistent)
```

---

## Proof Generation Flow

### Entry Point
- **Trigger**: User initiates registration (e.g., from ZkIdentityScreen)
- **Method**: `ProofGenerationManager.performRegistration(EDocument)`

### Components Involved

1. **ProofGenerationManager**
   - **File**: `app/src/main/java/com/rarilabs/rarime/manager/ProofGenerationManager.kt`
   - **Purpose**: Orchestrates proof generation

2. **RegistrationManager**
   - **File**: `app/src/main/java/com/rarilabs/rarime/manager/RegistrationManager.kt`
   - **Purpose**: Manages registration state and contract calls

3. **IdentitySEManager** (via Identity library)
   - **Purpose**: Generates ZK proofs

### Proof Generation Process

#### Step 1: Determine Circuit Type
```kotlin
// ProofGenerationManager.kt:167
val circuitType = getCircuitType(eDocument)

// EDocument.kt:90-239
fun getRegisterIdentityCircuitType(): RegisterIdentityCircuitType {
    val dg1Group = getDg1File()
    val sodFile = getSodFile()
    
    // Extract signature algorithm from SOD
    val sodSignatureAlgorithm = SODAlgorithm.fromValue(sodFile.digestEncryptionAlgorithm)
    
    // Get public key size
    val sodPublicKey = sodFile.docSigningCertificate.publicKey
    val publicKeySize = getPublicKeySupportedSize(getPublicKeySize(sodPublicKey))
    
    // Create signature type
    val signatureType = CircuitSignatureType(
        algorithm = sodSignatureAlgorithm.getCircuitSignatureAlgorithm(),
        keySize = publicKeySize,
        exponent = getPublicKeyExponent(sodPublicKey),
        salt = getSaltSize(),
        curve = if (sodPublicKey is ECPublicKey) getPublicKeyCurve(sodPublicKey) else null,
        hashAlgorithm = sodSignatureAlgorithm.getCircuitSignatureHashAlgorithm()
    )
    
    // Get passport hash type
    val digestAlgorithm = sodFile.digestAlgorithm
    val passportHashType = CircuitPassportHashType.fromValue(digestAlgorithm)
    
    // Get document type
    val documentType = CircuitDocumentType.fromValue(getStandardizedDocumentType(dg1Group.mrzInfo.documentCode))
    
    // Calculate digest positions
    val encapsulatedContent = Numeric.hexStringToByteArray(sodFile.readASN1Data())
    val ecHash = MessageDigest.getInstance(digestEncryptionAlgorithm, "BC").digest(encapsulatedContent)
    val ecDigestPosition = signedAttributes.findSubarrayIndex(ecHash)
    
    val dg1Hash = MessageDigest.getInstance(passportHashType.value.uppercase(), "BC")
        .digest(dg1!!.decodeHexString())
    val dg1DigestPositionShift = encapsulatedContent.findSubarrayIndex(dg1Hash)
    
    // Build circuit type
    val circuitType = RegisterIdentityCircuitType(
        signatureType = signatureType,
        passportHashType = passportHashType,
        documentType = documentType,
        ecChunkNumber = getChunkNumber(encapsulatedContent, passportHashType.getChunkSize()),
        ecDigestPosition = ecDigestPosition * 8u,
        dg1DigestPositionShift = dg1DigestPositionShift * 8u,
        aaType = null
    )
    
    // Add AA type if DG15 available
    if (!dg15.isNullOrEmpty()) {
        // Extract DG15 data and add to circuitType
        circuitType.aaType = CircuitAAType(...)
    }
    
    return circuitType
}
```

#### Step 2: Register Master Certificate
```kotlin
// ProofGenerationManager.kt:400
registerCertificate(eDocument)

// ProofGenerationManager.kt:788-863
private suspend fun registerCertificate(eDocument: EDocument): Map<String, Any> {
    // Read ICAO master certificate
    val icaoMasterCert = readICAO(context)  // From assets/masters_asset.pem
    
    // Get SOD file
    val sodFile = eDocument.getSodFile()
    val docSigningCert = sodFile.docSigningCertificate
    
    // Verify certificate chain
    val certificateChain = X509Util.buildCertificateChain(docSigningCert, icaoMasterCert)
    
    // Get circuit type for certificate registration
    val circuitType = getCertificateCircuitType(sodFile)
    
    // Build proof inputs
    val inputs = buildCertificateProofInputs(
        certificateChain,
        sodFile,
        eDocument,
        privateKeyBytes
    )
    
    // Generate proof
    val proof = identitySEManager.generateProof(
        circuitType = circuitType,
        inputs = inputs
    )
    
    // Store master certificate proof
    registrationManager.setMasterCertProof(proof)
    
    return inputs
}
```

#### Step 3: Generate Registration Proof
```kotlin
// ProofGenerationManager.kt:164-235
private suspend fun registerByDocument(eDocument: EDocument): UniversalProof {
    _state.value = PassportProofState.READING_DATA
    val circuitType = getCircuitType(eDocument)
    registrationManager.setCircuitData(circuitType)
    val circuitName = getCircuitName(circuitType)
    
    _state.value = PassportProofState.APPLYING_ZERO_KNOWLEDGE
    
    // Generate proof based on circuit type
    val proof = if (RegisterNoirCircuitData.fromValue(circuitType.buildName()) != null) {
        generateRegisterIdentityProofPlonk(eDocument, circuitType)  // Plonk proof
    } else {
        generateRegisterIdentityProofGroth(eDocument, circuitType)  // Groth16 proof
    }
    
    // Store proof
    registrationManager.setRegistrationProof(proof)
    
    // Check if document already registered
    val isDocumentRegistered = isDocumentRegistered(eDocument, proof)
    
    if (!isDocumentRegistered) {
        // Register on-chain
        val masterCertProof = registrationManager.masterCertProof.value
        registrationManager.register(
            proof, 
            eDocument, 
            masterCertProof, 
            false, 
            circuitName
        )
    }
    
    return proof
}
```

#### Step 4: Build Proof Inputs
```kotlin
// ProofGenerationManager.kt:576-833
private suspend fun buildRegisterIdentityInputs(
    eDocument: EDocument,
    circuitType: RegisterIdentityCircuitType
): Map<String, Any> {
    val sodFile = eDocument.getSodFile()
    
    // Extract signature data
    val publicKey = sodFile.docSigningCertificate.publicKey
    val sigBytes = sodFile.encryptedDigest
    val pubKeyData = CryptoUtilsPassport.getDataFromPublicKey(publicKey)!!
    val (pk, reductionPk, sig) = processSignatureData(circuitType, pubKeyData, sigBytes)
    
    // Convert data groups to hex lists
    val dg1Deferred = toHexList(eDocument.dg1!!.decodeHexString())
    val dg15Deferred = eDocument.dg15?.decodeHexString()?.let(toHexList) ?: listOf()
    val ecDeferred = toHexList(Numeric.hexStringToByteArray(sodFile.readASN1Data()))
    val saDeferred = toHexList(sodFile.eContent)
    val skIdentityDeferred = Numeric.toHexString(privateKeyBytes)
    
    // Get master certificate proof
    val proof = proofDeferred.await()  // From registerCertificate()
    
    return mapOf(
        "dg15" to dg15Deferred,
        "sa" to saDeferred,
        "pk" to pk,
        "icao_root" to Numeric.toHexString(proof.root),
        "inclusion_branches" to proof.siblings.map { Numeric.toHexString(it) },
        "ec" to ecDeferred,
        "sk_identity" to skIdentityDeferred,
        "dg1" to dg1Deferred,
        "sig" to sig,
        "reduction_pk" to reductionPk
    )
}
```

#### Step 5: Generate ZK Proof
```kotlin
// ProofGenerationManager.kt:430-580
private suspend fun generateRegisterIdentityProofGroth(
    eDocument: EDocument,
    registerIdentityCircuitType: RegisterIdentityCircuitType
): UniversalProof.Groth {
    // Download circuit files (zkey, dat)
    val circuitFiles = circuitDownloader.downloadCircuitFiles(circuitName)
    
    // Build proof inputs
    val inputs = buildRegisterIdentityInputs(eDocument, registerIdentityCircuitType)
    
    // Generate witness
    val witness = witnessCalculator.calculateWitness(circuitFiles.witnessCalc, inputs)
    
    // Generate proof using rapidsnark
    val proof = rapidsnarkProver.prove(
        circuitFiles.zkey,
        witness
    )
    
    // Convert to GrothProof
    val grothProof = GrothProof(
        a = proof.a,
        b = proof.b,
        c = proof.c
    )
    
    // Create UniversalProof
    return UniversalProof.Groth(
        proof = grothProof,
        publicSignals = proof.publicSignals
    )
}
```

### Proof Data Structure

```kotlin
// ZkpData.kt
sealed class UniversalProof {
    data class Groth(
        val proof: GrothProof,
        val publicSignals: List<String>
    ) : UniversalProof() {
        fun getProofJson(): String { ... }
        fun getPassportHash(): String { ... }
        fun getPublicKey(): String { ... }
        fun getIdentityKey(): String { ... }
    }
    
    data class Light(
        val proof: GrothProof,
        val publicSignals: List<String>
    ) : UniversalProof() { ... }
    
    data class Plonk(
        val rawProof: ByteArray,
        val publicSignals: List<String>
    ) : UniversalProof() { ... }
}

data class GrothProof(
    val a: List<String>,  // G1 point
    val b: List<List<String>>,  // G2 point
    val c: List<String>  // G1 point
)
```

### Proof Storage

#### In-Memory
```kotlin
// RegistrationManager.kt:45-47
private var _registrationProof = MutableStateFlow<UniversalProof?>(null)
val registrationProof: StateFlow<UniversalProof?>
```

#### Persistent Storage
```kotlin
// IdentityManager.kt
fun setRegistrationProof(proof: UniversalProof) {
    dataStoreManager.saveUniversalProof(proof)
    _registrationProof.value = proof
}
```

---

## Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    USER INTERACTION FLOW                         │
└─────────────────────────────────────────────────────────────────┘

1. User opens Identity tab
   │
   ├─→ ZkIdentityScreen
   │   ├─→ ZkIdentityScreenViewModel
   │   │   └─→ PassportManager.passport (StateFlow<EDocument?>)
   │   │
   │   └─→ if (passport == null) → ScanPassportScreen
   │
   │
2. User scans MRZ
   │
   ├─→ ScanPassportScreen (state = SCAN_MRZ)
   │   ├─→ ScanMRZStep
   │   │   ├─→ CameraScanPassport
   │   │   │   └─→ TextRecognitionAnalyzer
   │   │   │       ├─→ ML Kit Text Recognition
   │   │   │       ├─→ MRZ regex matching
   │   │   │       ├─→ Checksum verification
   │   │   │       └─→ buildTempMrz() → MRZInfo
   │   │   │
   │   │   └─→ Manual input (optional)
   │   │
   │   └─→ mrzData: MRZInfo? (remember state)
   │       │
   │       └─→ state = READ_NFC
   │
   │
3. User scans NFC
   │
   ├─→ ScanPassportScreen (state = READ_NFC)
   │   ├─→ ReadEDocStep
   │   │   ├─→ ReadEDocStepViewModel
   │   │   │   ├─→ startScanning(mrzInfo)
   │   │   │   │   └─→ NfcManager.startScanning()
   │   │   │   │
   │   │   │   └─→ handleScan(tag: Tag)
   │   │   │       ├─→ Create BACKey from MRZ
   │   │   │       ├─→ NfcUseCase(isoDep, bacKey, privateKey)
   │   │   │       └─→ scanNfcUseCase.scanPassport()
   │   │   │
   │   │   └─→ NfcUseCase.scanPassport()
   │   │       ├─→ PACE/BAC authentication
   │   │       ├─→ Read SOD (Security Object Document)
   │   │       ├─→ Read DG1 (Personal Details)
   │   │       ├─→ Read DG2 (Face Image)
   │   │       ├─→ Read DG15 (Public Key, optional)
   │   │       ├─→ Active Authentication (if DG15 available)
   │   │       └─→ Return EDocument
   │   │
   │   └─→ onNext(eDocument)
   │       ├─→ ScanPassportScreenViewModel.setPassportTEMP(eDocument)
   │       │   └─→ RegistrationManager.setEDocument(eDocument)
   │       │
   │       └─→ ScanPassportScreenViewModel.savePassport()
   │           └─→ PassportManager.setPassport(eDocument)
   │               └─→ SecureSharedPrefsManager.saveEDocument(eDocument)
   │
   │
4. Proof Generation (triggered separately)
   │
   ├─→ ProofGenerationManager.performRegistration(eDocument)
   │   ├─→ registerCertificate(eDocument)
   │   │   ├─→ Read ICAO master certificate
   │   │   ├─→ Build certificate chain
   │   │   ├─→ Generate certificate proof
   │   │   └─→ Store masterCertProof
   │   │
   │   ├─→ registerByDocument(eDocument)
   │   │   ├─→ getCircuitType(eDocument)
   │   │   │   └─→ EDocument.getRegisterIdentityCircuitType()
   │   │   │
   │   │   ├─→ buildRegisterIdentityInputs(eDocument, circuitType)
   │   │   │   ├─→ Extract SOD signature data
   │   │   │   ├─→ Convert DG1, DG15, EC, SA to hex lists
   │   │   │   └─→ Include master certificate proof
   │   │   │
   │   │   ├─→ generateRegisterIdentityProofGroth/Plonk()
   │   │   │   ├─→ Download circuit files
   │   │   │   ├─→ Calculate witness
   │   │   │   ├─→ Generate ZK proof
   │   │   │   └─→ Return UniversalProof
   │   │   │
   │   │   ├─→ isDocumentRegistered(eDocument, proof)
   │   │   │   └─→ Check on-chain registration
   │   │   │
   │   │   └─→ registrationManager.register(proof, eDocument, ...)
   │   │       ├─→ Build contract call data
   │   │       ├─→ Submit to relayer
   │   │       └─→ Register on-chain
   │   │
   │   └─→ Store proof
   │       ├─→ RegistrationManager.setRegistrationProof(proof)
   │       └─→ IdentityManager.setRegistrationProof(proof)
   │           └─→ SecureSharedPrefsManager.saveUniversalProof(proof)
```

---

## Key Files and Functions

### Navigation & UI
- `ZkIdentityScreen.kt` - Identity screen entry point
- `ScanPassportScreen.kt` - Main scanning screen with state machine
- `ScanMRZStep.kt` - MRZ scanning UI
- `ReadEDocStep.kt` - NFC scanning UI

### MRZ Processing
- `TextRecognitionAnalyzer.kt` - ML Kit text recognition and MRZ parsing
  - `analyze(ImageProxy)` - Processes camera frames
  - `filterScannedTextPassport(String)` - Extracts MRZ from text
  - `buildTempMrz(...)` - Creates MRZInfo object
  - `verifyChecksum(String)` - Validates MRZ checksums

### NFC Processing
- `ReadEDocStepViewModel.kt` - NFC scanning ViewModel
  - `startScanning(MRZInfo)` - Initiates NFC scan
  - `handleScan(Tag)` - Processes NFC tag
- `NfcUseCase.kt` - NFC data extraction
  - `scanPassport(): EDocument` - Main scanning function
  - Reads SOD, DG1, DG2, DG15
  - Performs Active Authentication

### Data Models
- `EDocument.kt` - Complete passport data structure
  - `getRegisterIdentityCircuitType()` - Determines circuit type
  - `getSodFile()` - Parses SOD file
  - `getDg1File()` - Parses DG1 file
  - `getDg15File()` - Parses DG15 file
- `PersonDetails.kt` - Personal information structure

### State Management
- `PassportManager.kt` - Passport data management
  - `setPassport(EDocument?)` - Stores passport
  - `passport: StateFlow<EDocument?>` - Observable state
- `RegistrationManager.kt` - Registration state
  - `setEDocument(EDocument?)` - Stores EDocument
  - `setRegistrationProof(UniversalProof)` - Stores proof
  - `register(...)` - On-chain registration
- `ScanPassportScreenViewModel.kt` - Scanning screen state
  - `setPassportTEMP(EDocument?)` - Temporary storage
  - `savePassport()` - Persists passport

### Proof Generation
- `ProofGenerationManager.kt` - Proof generation orchestration
  - `performRegistration(EDocument)` - Main entry point
  - `registerCertificate(EDocument)` - Certificate registration
  - `registerByDocument(EDocument)` - Document registration
  - `buildRegisterIdentityInputs(...)` - Builds proof inputs
  - `generateRegisterIdentityProofGroth(...)` - Groth16 proof
  - `generateRegisterIdentityProofPlonk(...)` - Plonk proof

### Storage
- `SecureSharedPrefsManager.kt` - Encrypted storage
  - `saveEDocument(EDocument)` - Persists EDocument
  - `readEDocument(): EDocument?` - Retrieves EDocument
  - `saveUniversalProof(UniversalProof)` - Persists proof
  - `readUniversalProof(): UniversalProof?` - Retrieves proof

---

## Summary

### Data Flow Summary

1. **MRZ Data**:
   - **Source**: Camera frame → ML Kit → Regex parsing
   - **Storage**: `ScanPassportScreen.mrzData` (temporary, in-memory)
   - **Type**: `MRZInfo` (org.jmrtd.lds.icao.MRZInfo)
   - **Fields**: documentNumber, dateOfBirth, dateOfExpiry, nationality

2. **NFC Data**:
   - **Source**: NFC chip → NfcUseCase.scanPassport()
   - **Storage**: 
     - `ReadEDocStepViewModel.eDocument` (temporary)
     - `RegistrationManager._eDocument` (StateFlow)
     - `PassportManager._passport` (StateFlow)
     - `SecureSharedPrefsManager` (encrypted, persistent)
   - **Type**: `EDocument`
   - **Fields**: sod, dg1, dg2, dg15, personDetails, aaSignature, etc.

3. **Proof Data**:
   - **Source**: `ProofGenerationManager.performRegistration()`
   - **Storage**:
     - `RegistrationManager._registrationProof` (StateFlow)
     - `IdentityManager._registrationProof` (StateFlow)
     - `SecureSharedPrefsManager` (encrypted, persistent)
   - **Type**: `UniversalProof` (Groth, Light, or Plonk)
   - **Contains**: ZK proof, public signals, identity key

### Key Takeaways

1. **MRZ data is NOT persisted** - It's only used to create BAC key for NFC authentication
2. **EDocument is the primary data structure** - Contains all passport information
3. **Proof generation is separate** - Triggered after scanning, not automatically
4. **All sensitive data is encrypted** - Stored via SecureSharedPrefsManager
5. **StateFlow is used extensively** - For reactive state management across the app

---

## End of Analysis

This document provides a complete technical breakdown of the Identity → Scan MRZ → Scan NFC data flow. All file paths, functions, and data structures are based on the actual codebase analysis.



