package com.grnddsystems.celestials.config

import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys as Web3Keys

object Keys {
    // These are mock values for local development
    // Replace with real values from your project configuration

    const val lightVerificationSKHex = "0000000000000000000000000000000000000000000000000000000000000000"
    const val GOOGLE_WEB_KEY = "YOUR_GOOGLE_WEB_KEY"
    const val APP_ID = "YOUR_FIREBASE_APP_ID"
    const val APPSFLYER_DEV_KEY = "YOUR_APPSFLYER_KEY"
    const val joinProgram = "0000000000000000000000000000000000000000000000000000000000000000"
    const val genesisReferralCode = "GENESIS"

    fun createEcKeyPair(): ECKeyPair {
        return Web3Keys.createEcKeyPair()
    }
}
