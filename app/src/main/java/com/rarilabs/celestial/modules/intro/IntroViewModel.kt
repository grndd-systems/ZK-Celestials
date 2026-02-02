package com.rarilabs.celestial.modules.intro

import android.util.Log
import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.manager.IdentityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val identityManager: IdentityManager,
) : ViewModel() {

    /**
     * Generates a new private key and saves it to secure storage
     * This restores the original Rarime behavior where keys are auto-generated
     * Runs on IO thread to avoid blocking UI
     *
     * @return The generated private key
     */
    suspend fun generateAndSavePrivateKey(): String {
        return withContext(Dispatchers.IO) {
            try {
                // Generate new private key using IdentityManager
                val newPrivateKey = identityManager.genPrivateKey()
                
                // Save the generated key
                identityManager.savePrivateKey(newPrivateKey)
                
                Log.d("IntroViewModel", "Private key generated and saved successfully")
                
                newPrivateKey
            } catch (e: Exception) {
                Log.e("IntroViewModel", "Failed to generate private key", e)
                throw e
            }
        }
    }

    /**
     * Saves the private key to secure storage
     * Kept for backward compatibility if needed elsewhere
     * Runs on IO thread to avoid blocking UI
     *
     * @param privateKey The private key to save
     */
    suspend fun savePrivateKey(privateKey: String) {
        withContext(Dispatchers.IO) {
            Log.d("IntroViewModel", "Saving key...")
            try {
                identityManager.savePrivateKey(privateKey)
                Log.d("IntroViewModel", "Key saved successfully")
            } catch (e: Exception) {
                Log.e("IntroViewModel", "Error saving key", e)
                throw IllegalArgumentException("Failed to save key: ${e.message ?: "Unknown error"}", e)
            }
        }
    }

}