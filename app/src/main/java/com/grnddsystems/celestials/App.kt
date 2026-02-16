package com.grnddsystems.celestials

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.grnddsystems.celestials.util.ErrorHandler
import com.grnddsystems.celestials.util.FileIntegrityChecker
import dagger.hilt.android.HiltAndroidApp
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

@HiltAndroidApp
class App : Application() {
    private fun setupFireBase() {
        android.util.Log.d("App", "=== setupFireBase START ===")
        try {
            val app = FirebaseApp.initializeApp(this)
            android.util.Log.d("App", "✓ FirebaseApp initialized: ${app?.name}")
            android.util.Log.d("App", "✓ FirebaseApp options: ${app?.options}")

            // Verify that resources are available
            val apiKey = getString(resources.getIdentifier("google_api_key", "string", packageName))
            val appId = getString(resources.getIdentifier("google_app_id", "string", packageName))
            val dbUrl = getString(resources.getIdentifier("firebase_database_url", "string", packageName))
            android.util.Log.d("App", "✓ google_api_key resource: ${apiKey}")
            android.util.Log.d("App", "✓ google_app_id resource: ${appId}")
            android.util.Log.d("App", "✓ firebase_database_url resource: ${dbUrl}")

            // Initialize Firebase Auth and Database explicitly
            val auth = FirebaseAuth.getInstance()
            android.util.Log.d("App", "✓ FirebaseAuth instance: $auth")
            android.util.Log.d("App", "✓ FirebaseAuth currentUser: ${auth.currentUser}")

            val database = FirebaseDatabase.getInstance(dbUrl)
            android.util.Log.d("App", "✓ FirebaseDatabase instance: $database")
            android.util.Log.d("App", "✓ FirebaseDatabase reference: ${database.reference}")
        } catch (e: Exception) {
            android.util.Log.e("App", "✗ Failed to initialize Firebase", e)
        }
        android.util.Log.d("App", "=== setupFireBase END ===")
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("App", "=== App.onCreate START ===")
        ErrorHandler.initialize(this)
        FileIntegrityChecker.init(this)
        setupBouncyCastle()
        setupFireBase()
        android.util.Log.d("App", "=== App.onCreate END ===")
    }

    private fun setupBouncyCastle() {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up the provider lazily when it's first used.
            return
        if (provider::class.java.equals(BouncyCastleProvider::class.java)) {
            // BC with same package name, shouldn't happen in real life.
            throw IllegalStateException("BC with same package name")
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}