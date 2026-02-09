package com.grnddsystems.celestials

import com.grnddsystems.celestials.config.Keys
import com.grnddsystems.celestials.data.RarimoChains

val BaseConfig: IConfig = if (BuildConfig.isTestnet) TestNetConfig else MainnetConfig

interface IConfig {
    val APPSFLYER_DEV_KEY: String
    val RELAYER_URL: String
    val EVM_RPC_URL: String
    val COSMOS_RPC_URL: String
    val EVM_SERVICE_URL: String
    val DISCORD_URL: String
    val TWITTER_URL: String
    val INVITATION_BASE_URL: String
    val POINTS_SVC_ID: String
    val AIRDROP_SVC_ID: String
    val ICAO_COSMOS_RPC: String
    val MASTER_CERTIFICATES_FILENAME: String
    val MASTER_CERTIFICATES_BUCKETNAME: String
    val EVM_STABLE_COIN_RPC: String
    val STABLE_COIN_ADDRESS: String
    val REGISTER_CONTRACT_ADDRESS: String
    val CERTIFICATES_SMT_CONTRACT_ADDRESS: String
    val REGISTRATION_SMT_CONTRACT_ADDRESS: String
    val STATE_KEEPER_CONTRACT_ADDRESS: String
    val REGISTRATION_SIMPLE_CONTRACT_ADRRESS: String
    val POINTS_SVC_SELECTOR: String
    val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP: Long
    val FEEDBACK_EMAIL: String
    val CHAIN: RarimoChains
    val lightVerificationSKHex: String
    val GLOBAL_NOTIFICATION_TOPIC: String
    val REWARD_NOTIFICATION_TOPIC: String
    val GOOGLE_WEB_KEY: String
    val APP_ID_FIREBASE: String
    val EXPLORER_API_URL: String
    val VOTING_RELAYER_URL: String
    val RARIMO_EXPLORER: String
    val VOTING_REGISTRATION_SMT_CONTRACT_ADDRESS: String

    val VOTING_RPC_URL: String

    val PROPOSAL_CONTRACT_ADDRESS: String

    val MULTICALL_CONTRACT_ADDRRESS: String

    val VOTING_WEBSITE_URL: String

    val FACE_REGISTRY_ADDRESS: String

    val FACE_REGISTRY_ZKEY_URL: String


    val FACE_RECOGNITION_MODEL_URL: String

    val GUESS_CELEBRITY_CONTRACT_ADDRESS: String

    val NOIR_TRUSTED_SETUP_URL: String


    val registerIdentity_1_256_3_5_576_248_NA: String
    val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String


    val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String
    val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String
    val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String

    val registerIdentity_1_256_3_4_600_248_1_1496_3_256: String

    val registerIdentity_1_160_3_4_576_200_NA: String


    val registerIdentity_20_256_3_3_336_224_NA: String


    val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String
    val registerIdentity_21_256_3_3_576_232_NA: String


    val registerIdentity_21_256_3_4_576_232_NA: String


    val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String

    val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String
    val registerIdentity_20_256_3_5_336_72_NA: String

    val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String
    val registerIdentity_20_160_3_3_736_200_NA: String

    val registerIdentityLight160: String
    val registerIdentityLight224: String
    val registerIdentityLight256: String
    val registerIdentityLight384: String
    val registerIdentityLight512: String

    val registerIdentity_11_256_3_4_336_232_1_1480_4_256: String
    val registerIdentity_11_256_3_3_576_248_NA: String
    val registerIdentity_11_256_3_5_576_248_NA: String
    val registerIdentity_14_256_3_3_576_240_NA: String
    val registerIdentity_14_256_3_4_336_232_1_1480_5_296: String
    val registerIdentity_14_256_3_4_576_248_1_1496_3_256: String
    val registerIdentity_1_256_3_4_576_232_1_1480_3_256: String
    val registerIdentity_1_256_3_5_336_248_1_2120_4_256: String
    val registerIdentity_2_256_3_4_336_232_1_1480_4_256: String
    val registerIdentity_2_256_3_4_336_248_NA: String
    val registerIdentity_20_160_3_2_576_184_NA: String
    val registerIdentity_20_160_3_3_576_200_NA: String
    val registerIdentity_20_256_3_5_336_248_NA: String
    val registerIdentity_23_160_3_3_576_200_NA: String
    val registerIdentity_24_256_3_4_336_248_NA: String
    val registerIdentity_3_256_3_4_600_248_1_1496_3_256: String
    val registerIdentity_6_160_3_3_336_216_1_1080_3_256: String
    val registerIdentity_3_512_3_3_336_264_NA: String


    val registerIdentity_11_256_3_5_584_264_1_2136_4_256: String
    val registerIdentity_11_256_3_5_576_264_NA: String
    val registerIdentity_2_256_3_4_336_248_22_1496_7_2408: String
    val registerIdentity_1_256_3_4_336_232_NA: String

    val registerIdentity_25_384_3_3_336_232_NA: String
    val registerIdentity_25_384_3_4_336_264_1_2904_2_256: String
    val registerIdentity_26_512_3_3_336_248_NA: String

    val registerIdentity_26_512_3_3_336_264_1_1968_2_256: String
    val registerIdentity_27_512_3_4_336_248_NA: String

    val registerIdentity_1_256_3_5_336_248_1_2120_3_256: String
    val registerIdentity_7_160_3_3_336_216_1_1080_3_256: String

    val registerIdentity_8_160_3_3_336_216_1_1080_3_256: String

    val registerIdentity_3_256_3_3_576_248_NA: String

    val registerIdentity_25_384_3_3_336_264_1_2024_3_296: String

    val registerIdentity_28_384_3_3_576_264_24_2024_4_2792: String
    val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String
    val registerIdentity_25_384_3_3_336_248_NA: String

    val registerIdentity_1_160_3_3_576_200_NA: String
    val registerIdentity_1_256_3_3_576_248_NA: String
    val registerIdentity_1_256_3_4_336_232_1_1480_5_296: String
    val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String
    val registerIdentity_2_256_3_6_336_264_1_2448_3_256: String
    val registerIdentity_3_160_3_3_336_200_NA: String

    val registerIdentity_3_160_3_4_576_216_1_1512_3_256: String
    val registerIdentity_11_256_3_2_336_216_NA: String
    val registerIdentity_11_256_3_3_336_248_NA: String

    val registerIdentity_11_256_3_3_576_240_1_864_5_264: String
    val registerIdentity_11_256_3_3_576_248_1_1184_5_264: String
    val registerIdentity_11_256_3_4_584_248_1_1496_4_256: String

    val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String
    val registerIdentity_12_256_3_3_336_232_NA: String
    val registerIdentity_15_512_3_3_336_248_NA: String

    val registerIdentity_21_256_3_3_336_232_NA: String
    val registerIdentity_21_256_3_5_576_232_NA: String
    val registerIdentity_24_256_3_4_336_232_NA: String

    val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String

    val registerIdentity_25_384_3_5_576_248_20_3768_3_2008: String
    val registerIdentity_1_256_3_6_336_248_1_2432_3_256: String
    val registerIdentity_2_256_3_5_336_248_22_1808_7_2408: String

    val registerIdentity_1_256_3_4_336_248_1_1496_4_256: String
    val registerIdentity_11_256_3_4_576_248_1_1496_5_296: String

    val registerIdentity_1_256_3_5_344_232_NA: String
    val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String

    val registerIdentity_1_256_3_5_336_232_NA: String

    val registerIdentity_1_256_3_7_336_264_20_2760_6_2008: String

    val registerIdentity_1_256_3_4_336_232_1_1480_4_256: String

    val registerIdentity_1_256_3_4_336_248_1_560_4_256: String

    val registerIdentity_26_512_3_2_336_248_1_1384_2_256: String

    val queryIdentity: String

}

/* TESTNET */
object TestNetConfig : IConfig {
    override val RELAYER_URL = "https://registration-relayer-1062715874572.europe-west1.run.app"
    override val COSMOS_RPC_URL = "https://rpc-api.node1.mainnet-beta.rarimo.com"
    override val EVM_SERVICE_URL =
        "https://api.orgs.app.stage.rarime.com/integrations/evm-airdrop-svc-glo/"
    override val DISCORD_URL = "https://discord.gg/Bzjm5MDXrU"
    override val TWITTER_URL = "https://x.com/Rarimo_protocol"

    override val INVITATION_BASE_URL = "https://app.stage.rarime.com"

    override val POINTS_SVC_ID = "0x77fabbc6cb41a11d4fb6918696b3550d5d602f252436dd587f9065b7c4e62b"
    override val AIRDROP_SVC_ID = "0xac42d1a986804618c7a793fbe814d9b31e47be51e082806363dca6958f3062"

    override val ICAO_COSMOS_RPC = "core-api.node1.mainnet-beta.rarimo.com:443"
    override val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
    override val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"
    override val EVM_STABLE_COIN_RPC = "https://ethereum-sepolia-rpc.publicnode.com"
    override val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"
    override val EXPLORER_API_URL = "https://explorer-eden-testnet.binarybuilders.services/api/"
    override val RARIMO_EXPLORER = "https://explorer-eden-testnet.binarybuilders.services"
    override val EVM_RPC_URL = "https://ev-reth-eden-testnet.binarybuilders.services:8545"
    override val REGISTER_CONTRACT_ADDRESS = "0x6BF01a93ED6134681BDDd63933867F982895ca8c"
    override val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0xfe23b53440A3cF87F463C446EfAd128294085b93"
    override val REGISTRATION_SMT_CONTRACT_ADDRESS = "0x479F84502Db545FA8d2275372E0582425204A879"
    override val STATE_KEEPER_CONTRACT_ADDRESS = "0x91784Fd7FCC9D557e24e180016F1377120661475"
    override val REGISTRATION_SIMPLE_CONTRACT_ADRRESS = "0x497D6957729d3a39D43843BD27E6cbD12310F273"
    override val POINTS_SVC_SELECTOR = "23073"
    override val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP = 1715688000L

    override val FEEDBACK_EMAIL = "apereliez1@gmail.com"
    override val CHAIN = RarimoChains.MainnetBeta
    override val lightVerificationSKHex = Keys.lightVerificationSKHex
    override val GOOGLE_WEB_KEY = Keys.GOOGLE_WEB_KEY
    override val APP_ID_FIREBASE = Keys.APP_ID


    override val GLOBAL_NOTIFICATION_TOPIC = "rarime-stage"
    override val REWARD_NOTIFICATION_TOPIC: String = "rarime-rewardable-stage"

    override val APPSFLYER_DEV_KEY = Keys.APPSFLYER_DEV_KEY

    override val VOTING_RELAYER_URL: String = "https://api.stage.freedomtool.org"
    override val VOTING_REGISTRATION_SMT_CONTRACT_ADDRESS: String =
        "0xFbae44a113A6f07687b180605f425e43066a6179"
    override val VOTING_RPC_URL: String = "https://rpc.qtestnet.org"

    override val PROPOSAL_CONTRACT_ADDRESS: String = "0x4C61d7454653720DAb9e26Ca25dc7B8a5cf7065b"
    override val MULTICALL_CONTRACT_ADDRRESS: String = "0xcA11bde05977b3631167028862bE2a173976CA11"

    override val VOTING_WEBSITE_URL: String = "https://freedomtool.org"

    override val NOIR_TRUSTED_SETUP_URL: String =
        "https://storage.googleapis.com/zk-celestial-store/trusted-setup/ultraPlonkTrustedSetup.dat"


    override val FACE_REGISTRY_ADDRESS: String = "0x3C0f27AC1817820C1BA41337B53090652aE4F448"

    override val GUESS_CELEBRITY_CONTRACT_ADDRESS: String =
        "0x411AA3eF21AdC9e84c60e17451B0732119C8f0c7"


    override val FACE_REGISTRY_ZKEY_URL: String =
        "https://storage.googleapis.com/zk-celestial-store/zkey/circuit_final.zkey"
    override val FACE_RECOGNITION_MODEL_URL: String =
        "https://storage.googleapis.com/zk-celestial-store/face-recognition/face-recognition.tflite"


    override val registerIdentity_1_160_3_4_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_1_160_3_4_576_200_NA-download.zip"


    override val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_14_256_3_4_336_64_1_1480_5_296-download.zip"

    override val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_1_256_3_6_336_560_1_2744_4_256-download.zip"
    override val registerIdentity_20_256_3_5_336_72_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_20_256_3_5_336_72_NA-download.zip"

    override val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_4_160_3_3_336_216_1_1296_3_256-download.zip"
    override val registerIdentity_20_160_3_3_736_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_20_160_3_3_736_200_NA-download.zip"


    override val registerIdentityLight160: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight160-download.zip"
    override val registerIdentityLight224: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight224-download.zip"
    override val registerIdentityLight256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight256-download.zip"
    override val registerIdentityLight384: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight384-download.zip"
    override val registerIdentityLight512: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight512-download.zip"


    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_10_256_3_3_576_248_1_1184_5_264.json"
    override val registerIdentity_11_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_576_248_NA.json"
    override val registerIdentity_11_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_11_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_248_NA.json"
    override val registerIdentity_14_256_3_3_576_240_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_14_256_3_3_576_240_NA.json"
    override val registerIdentity_14_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_14_256_3_4_336_232_1_1480_5_296.json"
    override val registerIdentity_14_256_3_4_576_248_1_1496_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_14_256_3_4_576_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_4_576_232_1_1480_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_576_232_1_1480_3_256.json"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_1_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_5_336_248_1_2120_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_336_248_1_2120_4_256.json"
    override val registerIdentity_2_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_2_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_4_336_248_NA.json"
    override val registerIdentity_20_160_3_2_576_184_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_160_3_2_576_184_NA.json"
    override val registerIdentity_20_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_160_3_3_576_200_NA.json"
    override val registerIdentity_20_256_3_5_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_256_3_5_336_248_NA.json"
    override val registerIdentity_21_256_3_3_576_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_21_256_3_3_576_232_NA.json"
    override val registerIdentity_23_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_23_160_3_3_576_200_NA.json"
    override val registerIdentity_24_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_24_256_3_4_336_248_NA.json"
    override val registerIdentity_3_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_3_512_3_3_336_264_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_512_3_3_336_264_NA.json"

    override val registerIdentity_6_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_6_160_3_3_336_216_1_1080_3_256.json"
    override val registerIdentity_1_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_576_248_NA.json"
    override val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_6_576_264_1_2448_3_256.json"
    override val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_6_336_264_21_2448_6_2008.json"
    override val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_6_336_248_1_2432_3_256.json"
    override val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_6_576_248_1_2432_3_256.json"
    override val registerIdentity_20_256_3_3_336_224_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_256_3_3_336_224_NA.json"
    override val registerIdentity_21_256_3_4_576_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_4_576_232_NA.json"


    override val registerIdentity_11_256_3_5_584_264_1_2136_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_584_264_1_2136_4_256.json"

    override val registerIdentity_11_256_3_5_576_264_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_264_NA.json"

    override val registerIdentity_2_256_3_4_336_248_22_1496_7_2408: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_4_336_248_22_1496_7_2408.json"

    override val registerIdentity_1_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_232_NA.json"

    override val registerIdentity_25_384_3_3_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_3_336_232_NA.json"
    override val registerIdentity_25_384_3_4_336_264_1_2904_2_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_4_336_264_1_2904_2_256.json"
    override val registerIdentity_26_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_26_512_3_3_336_248_NA.json"
    override val registerIdentity_26_512_3_3_336_264_1_1968_2_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_26_512_3_3_336_264_1_1968_2_256.json"
    override val registerIdentity_27_512_3_4_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_27_512_3_4_336_248_NA.json"

    override val registerIdentity_1_256_3_5_336_248_1_2120_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_336_248_1_2120_3_256.json"
    override val registerIdentity_7_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_7_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_8_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_8_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_3_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_256_3_3_576_248_NA.json"

    override val registerIdentity_25_384_3_3_336_264_1_2024_3_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_3_336_264_1_2024_3_296.json"

    override val registerIdentity_28_384_3_3_576_264_24_2024_4_2792: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_28_384_3_3_576_264_24_2024_4_2792.json"
    override val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_6_576_248_1_2432_5_296.json"
    override val registerIdentity_25_384_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_3_336_248_NA.json"

    override val registerIdentity_1_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_160_3_3_576_200_NA.json"
    override val registerIdentity_1_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_3_576_248_NA.json"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_232_1_1480_5_296.json"

    override val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v/v0.1.0/registerIdentity_1_256_3_6_336_248_1_2744_4_256.json"
    override val registerIdentity_2_256_3_6_336_264_1_2448_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v/v0.1.0/registerIdentity_2_256_3_6_336_264_1_2448_3_256.json"
    override val registerIdentity_3_160_3_3_336_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v/v0.1.0/registerIdentity_3_160_3_3_336_200_NA.json"

    override val registerIdentity_3_160_3_4_576_216_1_1512_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_160_3_4_576_216_1_1512_3_256.json"
    override val registerIdentity_11_256_3_2_336_216_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_2_336_216_NA.json"
    override val registerIdentity_11_256_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_336_248_NA.json"

    override val registerIdentity_11_256_3_3_576_240_1_864_5_264: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_576_240_1_864_5_264.json"
    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_576_248_1_1184_5_264.json"
    override val registerIdentity_11_256_3_4_584_248_1_1496_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_4_584_248_1_1496_4_256.json"

    override val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_248_1_1808_5_296.json"
    override val registerIdentity_12_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_12_256_3_3_336_232_NA.json"
    override val registerIdentity_15_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_15_512_3_3_336_248_NA.json"

    override val registerIdentity_21_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_3_336_232_NA.json"
    override val registerIdentity_21_256_3_5_576_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_5_576_232_NA.json"
    override val registerIdentity_24_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_24_256_3_4_336_232_NA.json"

    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_248_1_1808_4_256.json"

    override val registerIdentity_25_384_3_5_576_248_20_3768_3_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_5_576_248_20_3768_3_2008.json"
    override val registerIdentity_1_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_6_336_248_1_2432_3_256.json"
    override val registerIdentity_2_256_3_5_336_248_22_1808_7_2408: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_5_336_248_22_1808_7_2408.json"

    override val registerIdentity_1_256_3_4_336_248_1_1496_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_248_1_1496_4_256.json"
    override val registerIdentity_11_256_3_4_576_248_1_1496_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_4_576_248_1_1496_5_296.json"

    override val registerIdentity_1_256_3_5_344_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_344_232_NA.json"
    override val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_7_336_264_21_3072_6_2008.json"

    override val registerIdentity_1_256_3_5_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_336_232_NA.json"

    override val registerIdentity_1_256_3_7_336_264_20_2760_6_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_7_336_264_20_2760_6_2008.json"

    override val registerIdentity_1_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_232_1_1480_4_256.json"

    override val registerIdentity_1_256_3_4_336_248_1_560_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_248_1_560_4_256.json"

    override val registerIdentity_26_512_3_2_336_248_1_1384_2_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_26_512_3_2_336_248_1_1384_2_256.json"

    override val queryIdentity: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/vv0.1.0/queryIdentity.json"


}

// Mainnet
object MainnetConfig : IConfig {
    override val RELAYER_URL = "https://registration-relayer-1062715874572.europe-west1.run.app"
    override val COSMOS_RPC_URL = "https://rpc-api.node1.mainnet-beta.rarimo.com"
    override val EVM_SERVICE_URL =
        "https://api.orgs.app.stage.rarime.com/integrations/evm-airdrop-svc-glo/"
    override val DISCORD_URL = "https://discord.gg/Bzjm5MDXrU"
    override val TWITTER_URL = "https://x.com/Rarimo_protocol"

    override val INVITATION_BASE_URL = "https://app.stage.rarime.com"

    override val POINTS_SVC_ID = "0x77fabbc6cb41a11d4fb6918696b3550d5d602f252436dd587f9065b7c4e62b"
    override val AIRDROP_SVC_ID = "0xac42d1a986804618c7a793fbe814d9b31e47be51e082806363dca6958f3062"

    override val ICAO_COSMOS_RPC = "core-api.node1.mainnet-beta.rarimo.com:443"
    override val MASTER_CERTIFICATES_FILENAME = "icaopkd-list.ldif"
    override val MASTER_CERTIFICATES_BUCKETNAME = "rarimo-temp"
    override val EVM_STABLE_COIN_RPC = "https://ethereum-sepolia-rpc.publicnode.com"
    override val STABLE_COIN_ADDRESS = "0xbd03f0fC994fd1015eAdc37c943055330e238Ad9"
    override val EXPLORER_API_URL = "https://explorer-eden-testnet.binarybuilders.services/api/"
    override val RARIMO_EXPLORER = "https://explorer-eden-testnet.binarybuilders.services"
    override val EVM_RPC_URL = "https://ev-reth-eden-testnet.binarybuilders.services:8545"
    override val REGISTER_CONTRACT_ADDRESS = "0x6BF01a93ED6134681BDDd63933867F982895ca8c"
    override val CERTIFICATES_SMT_CONTRACT_ADDRESS = "0xfe23b53440A3cF87F463C446EfAd128294085b93"
    override val REGISTRATION_SMT_CONTRACT_ADDRESS = "0x479F84502Db545FA8d2275372E0582425204A879"
    override val STATE_KEEPER_CONTRACT_ADDRESS = "0x91784Fd7FCC9D557e24e180016F1377120661475"
    override val REGISTRATION_SIMPLE_CONTRACT_ADRRESS = "0x497D6957729d3a39D43843BD27E6cbD12310F273"
    override val POINTS_SVC_SELECTOR = "23073"
    override val POINTS_SVC_ALLOWED_IDENTITY_TIMESTAMP = 1715688000L

    override val FEEDBACK_EMAIL = "apereliez1@gmail.com"
    override val CHAIN = RarimoChains.MainnetBeta
    override val lightVerificationSKHex = Keys.lightVerificationSKHex
    override val GOOGLE_WEB_KEY = Keys.GOOGLE_WEB_KEY
    override val APP_ID_FIREBASE = Keys.APP_ID


    override val GLOBAL_NOTIFICATION_TOPIC = "rarime-stage"
    override val REWARD_NOTIFICATION_TOPIC: String = "rarime-rewardable-stage"

    override val APPSFLYER_DEV_KEY = Keys.APPSFLYER_DEV_KEY

    override val VOTING_RELAYER_URL: String = "https://api.stage.freedomtool.org"
    override val VOTING_REGISTRATION_SMT_CONTRACT_ADDRESS: String =
        "0xFbae44a113A6f07687b180605f425e43066a6179"
    override val VOTING_RPC_URL: String = "https://rpc.qtestnet.org"

    override val PROPOSAL_CONTRACT_ADDRESS: String = "0x4C61d7454653720DAb9e26Ca25dc7B8a5cf7065b"
    override val MULTICALL_CONTRACT_ADDRRESS: String = "0xcA11bde05977b3631167028862bE2a173976CA11"

    override val VOTING_WEBSITE_URL: String = "https://freedomtool.org"

    override val NOIR_TRUSTED_SETUP_URL: String =
        "https://storage.googleapis.com/zk-celestial-store/trusted-setup/ultraPlonkTrustedSetup.dat"


    override val FACE_REGISTRY_ADDRESS: String = "0x3C0f27AC1817820C1BA41337B53090652aE4F448"

    override val GUESS_CELEBRITY_CONTRACT_ADDRESS: String =
        "0x411AA3eF21AdC9e84c60e17451B0732119C8f0c7"


    override val FACE_REGISTRY_ZKEY_URL: String =
        "https://storage.googleapis.com/zk-celestial-store/zkey/circuit_final.zkey"
    override val FACE_RECOGNITION_MODEL_URL: String =
        "https://storage.googleapis.com/zk-celestial-store/face-recognition/face-recognition.tflite"


    override val registerIdentity_1_160_3_4_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_1_160_3_4_576_200_NA-download.zip"


    override val registerIdentity_14_256_3_4_336_64_1_1480_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_14_256_3_4_336_64_1_1480_5_296-download.zip"

    override val registerIdentity_1_256_3_6_336_560_1_2744_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_1_256_3_6_336_560_1_2744_4_256-download.zip"
    override val registerIdentity_20_256_3_5_336_72_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_20_256_3_5_336_72_NA-download.zip"

    override val registerIdentity_4_160_3_3_336_216_1_1296_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_4_160_3_3_336_216_1_1296_3_256-download.zip"
    override val registerIdentity_20_160_3_3_736_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentity_20_160_3_3_736_200_NA-download.zip"


    override val registerIdentityLight160: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight160-download.zip"
    override val registerIdentityLight224: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight224-download.zip"
    override val registerIdentityLight256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight256-download.zip"
    override val registerIdentityLight384: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight384-download.zip"
    override val registerIdentityLight512: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits/v0.1.0/registerIdentityLight512-download.zip"


    override val registerIdentity_10_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_10_256_3_3_576_248_1_1184_5_264.json"
    override val registerIdentity_11_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_576_248_NA.json"
    override val registerIdentity_11_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_11_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_248_NA.json"
    override val registerIdentity_14_256_3_3_576_240_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_14_256_3_3_576_240_NA.json"
    override val registerIdentity_14_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_14_256_3_4_336_232_1_1480_5_296.json"
    override val registerIdentity_14_256_3_4_576_248_1_1496_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_14_256_3_4_576_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_4_576_232_1_1480_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_576_232_1_1480_3_256.json"
    override val registerIdentity_1_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_1_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_1_256_3_5_336_248_1_2120_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_336_248_1_2120_4_256.json"
    override val registerIdentity_2_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_4_336_232_1_1480_4_256.json"
    override val registerIdentity_2_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_4_336_248_NA.json"
    override val registerIdentity_20_160_3_2_576_184_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_160_3_2_576_184_NA.json"
    override val registerIdentity_20_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_160_3_3_576_200_NA.json"
    override val registerIdentity_20_256_3_5_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_256_3_5_336_248_NA.json"
    override val registerIdentity_21_256_3_3_576_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v1.0.4/registerIdentity_21_256_3_3_576_232_NA.json"
    override val registerIdentity_23_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_23_160_3_3_576_200_NA.json"
    override val registerIdentity_24_256_3_4_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_24_256_3_4_336_248_NA.json"
    override val registerIdentity_3_256_3_4_600_248_1_1496_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_256_3_4_600_248_1_1496_3_256.json"
    override val registerIdentity_3_512_3_3_336_264_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_512_3_3_336_264_NA.json"

    override val registerIdentity_6_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_6_160_3_3_336_216_1_1080_3_256.json"
    override val registerIdentity_1_256_3_5_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_576_248_NA.json"
    override val registerIdentity_1_256_3_6_576_264_1_2448_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_6_576_264_1_2448_3_256.json"
    override val registerIdentity_2_256_3_6_336_264_21_2448_6_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_6_336_264_21_2448_6_2008.json"
    override val registerIdentity_2_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_6_336_248_1_2432_3_256.json"
    override val registerIdentity_2_256_3_6_576_248_1_2432_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_6_576_248_1_2432_3_256.json"
    override val registerIdentity_20_256_3_3_336_224_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_20_256_3_3_336_224_NA.json"
    override val registerIdentity_21_256_3_4_576_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_4_576_232_NA.json"


    override val registerIdentity_11_256_3_5_584_264_1_2136_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_584_264_1_2136_4_256.json"

    override val registerIdentity_11_256_3_5_576_264_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_264_NA.json"

    override val registerIdentity_2_256_3_4_336_248_22_1496_7_2408: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_4_336_248_22_1496_7_2408.json"

    override val registerIdentity_1_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_232_NA.json"

    override val registerIdentity_25_384_3_3_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_3_336_232_NA.json"
    override val registerIdentity_25_384_3_4_336_264_1_2904_2_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_4_336_264_1_2904_2_256.json"
    override val registerIdentity_26_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_26_512_3_3_336_248_NA.json"
    override val registerIdentity_26_512_3_3_336_264_1_1968_2_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_26_512_3_3_336_264_1_1968_2_256.json"
    override val registerIdentity_27_512_3_4_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_27_512_3_4_336_248_NA.json"

    override val registerIdentity_1_256_3_5_336_248_1_2120_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_336_248_1_2120_3_256.json"
    override val registerIdentity_7_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_7_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_8_160_3_3_336_216_1_1080_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_8_160_3_3_336_216_1_1080_3_256.json"

    override val registerIdentity_3_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_256_3_3_576_248_NA.json"

    override val registerIdentity_25_384_3_3_336_264_1_2024_3_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_3_336_264_1_2024_3_296.json"

    override val registerIdentity_28_384_3_3_576_264_24_2024_4_2792: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_28_384_3_3_576_264_24_2024_4_2792.json"
    override val registerIdentity_1_256_3_6_576_248_1_2432_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_6_576_248_1_2432_5_296.json"
    override val registerIdentity_25_384_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_3_336_248_NA.json"

    override val registerIdentity_1_160_3_3_576_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_160_3_3_576_200_NA.json"
    override val registerIdentity_1_256_3_3_576_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_3_576_248_NA.json"
    override val registerIdentity_1_256_3_4_336_232_1_1480_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_232_1_1480_5_296.json"

    override val registerIdentity_1_256_3_6_336_248_1_2744_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v/v0.1.0/registerIdentity_1_256_3_6_336_248_1_2744_4_256.json"
    override val registerIdentity_2_256_3_6_336_264_1_2448_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v/v0.1.0/registerIdentity_2_256_3_6_336_264_1_2448_3_256.json"
    override val registerIdentity_3_160_3_3_336_200_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v/v0.1.0/registerIdentity_3_160_3_3_336_200_NA.json"

    override val registerIdentity_3_160_3_4_576_216_1_1512_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_3_160_3_4_576_216_1_1512_3_256.json"
    override val registerIdentity_11_256_3_2_336_216_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_2_336_216_NA.json"
    override val registerIdentity_11_256_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_336_248_NA.json"

    override val registerIdentity_11_256_3_3_576_240_1_864_5_264: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_576_240_1_864_5_264.json"
    override val registerIdentity_11_256_3_3_576_248_1_1184_5_264: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_3_576_248_1_1184_5_264.json"
    override val registerIdentity_11_256_3_4_584_248_1_1496_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_4_584_248_1_1496_4_256.json"

    override val registerIdentity_11_256_3_5_576_248_1_1808_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_248_1_1808_5_296.json"
    override val registerIdentity_12_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_12_256_3_3_336_232_NA.json"
    override val registerIdentity_15_512_3_3_336_248_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_15_512_3_3_336_248_NA.json"

    override val registerIdentity_21_256_3_3_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_3_336_232_NA.json"
    override val registerIdentity_21_256_3_5_576_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_5_576_232_NA.json"
    override val registerIdentity_24_256_3_4_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_24_256_3_4_336_232_NA.json"

    override val registerIdentity_11_256_3_5_576_248_1_1808_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_5_576_248_1_1808_4_256.json"

    override val registerIdentity_25_384_3_5_576_248_20_3768_3_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_25_384_3_5_576_248_20_3768_3_2008.json"
    override val registerIdentity_1_256_3_6_336_248_1_2432_3_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_6_336_248_1_2432_3_256.json"
    override val registerIdentity_2_256_3_5_336_248_22_1808_7_2408: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_2_256_3_5_336_248_22_1808_7_2408.json"

    override val registerIdentity_1_256_3_4_336_248_1_1496_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_248_1_1496_4_256.json"
    override val registerIdentity_11_256_3_4_576_248_1_1496_5_296: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_11_256_3_4_576_248_1_1496_5_296.json"

    override val registerIdentity_1_256_3_5_344_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_344_232_NA.json"
    override val registerIdentity_21_256_3_7_336_264_21_3072_6_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_21_256_3_7_336_264_21_3072_6_2008.json"

    override val registerIdentity_1_256_3_5_336_232_NA: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_5_336_232_NA.json"

    override val registerIdentity_1_256_3_7_336_264_20_2760_6_2008: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_7_336_264_20_2760_6_2008.json"

    override val registerIdentity_1_256_3_4_336_232_1_1480_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_232_1_1480_4_256.json"

    override val registerIdentity_1_256_3_4_336_248_1_560_4_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_1_256_3_4_336_248_1_560_4_256.json"

    override val registerIdentity_26_512_3_2_336_248_1_1384_2_256: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/registerIdentity_26_512_3_2_336_248_1_1384_2_256.json"

    override val queryIdentity: String =
        "https://storage.googleapis.com/zk-celestial-store/passport-zk-circuits-noir/v0.1.0/queryIdentity.json"

}