package com.grnddsystems.celestials.modules.profile

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.grnddsystems.celestials.manager.DriveBackupManager
import com.grnddsystems.celestials.manager.IdentityManager
import com.grnddsystems.celestials.manager.PassportManager
import com.grnddsystems.celestials.manager.SettingsManager
import com.grnddsystems.celestials.manager.WalletManager
import com.grnddsystems.celestials.store.SecureSharedPrefsManager
import com.grnddsystems.celestials.store.room.notifications.NotificationsRepository
import com.grnddsystems.celestials.store.room.voting.VotingRepository
import com.grnddsystems.celestials.util.AddressFormatter
//import com.grnddsystems.celestials.util.AddressFormatter
import com.grnddsystems.celestials.util.ErrorHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val walletManager: WalletManager,
    val identityManager: IdentityManager,
    val passportManager: PassportManager,
    val dataStoreManager: SecureSharedPrefsManager,
    private val driveBackupManager: DriveBackupManager,
    private val notificationsRepository: NotificationsRepository,
    private val votingRepository: VotingRepository
) : ViewModel() {


    val evmAddress = identityManager.evmAddress()
    val privateKey = identityManager.privateKey

    val language = settingsManager.language
    val colorScheme = settingsManager.colorScheme

    // State flow to hold the formatted address
    private val _formattedAddress = MutableStateFlow("Not set")
    val formattedAddress: StateFlow<String> = _formattedAddress.asStateFlow()

    /**
     * Loads the private key and formats it into an address
     * Applies the rule: FIRST 3 + "..." + LAST 8
     * Uses privateKey.value directly from IdentityManager StateFlow
     */
    fun loadAddress() {
        viewModelScope.launch {
            try {
                // Get private key directly from StateFlow
                val key = identityManager.privateKey.value

                // Format the key into address format: FIRST 3 + "..." + LAST 8
                _formattedAddress.value = AddressFormatter.formatAddress(key)

            } catch (e: Exception) {
                // If there's an error, show "Not set"
                ErrorHandler.logError("ProfileViewModel", "Failed to load address", e)
                _formattedAddress.value = "Not set"
            }
        }
    }

    fun getImage(): Bitmap? {
        val passport = passportManager.passport.value

        return passport?.personDetails?.getPortraitImage()
    }

    suspend fun clearAllData(context: Context) {
        dataStoreManager.clearAllData()

        notificationsRepository.deleteAllNotifications()
        votingRepository.deleteAllVoting()

        delay(1000L)

        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent?.component

        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    fun getDecryptedFeedbackFile(): File {
        val pointsNullifier = identityManager.getUserPointsNullifier()

        ErrorHandler.logDebug("sendFeedback", "pointsNullifier: $pointsNullifier")

        val logFile = ErrorHandler.getLogFile()

        return logFile
    }


    private val signedInAccount = driveBackupManager.signedInAccount

    private val _isInit = MutableStateFlow(false)
    val isInit: StateFlow<Boolean> = _isInit.asStateFlow()


    private val _isDriveButtonEnabled = MutableStateFlow(true)
    val isDriveButtonEnabled: StateFlow<Boolean> = _isDriveButtonEnabled.asStateFlow()


    init {
        viewModelScope.launch {
            _isInit.value = false
            driveBackupManager.checkIfUserIsSignedIn()
            _isInit.value = true
        }
    }


    fun handleSignInResult(
        task: Task<GoogleSignInAccount>, onError: (e: Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    driveBackupManager.setSignedInAccount(account)
                }
            } catch (e: ApiException) {
                onError(e)
                ErrorHandler.logError("handleSignInResult", "Failed to sign in", e)
            }
        }
    }

    fun backupPrivateKey() {
        val account = signedInAccount.value ?: return
        val pk = privateKey.value ?: return

        viewModelScope.launch {
            _isDriveButtonEnabled.value = false
            try {
                driveBackupManager.backupPrivateKey(account, pk)
            } catch (e: UserRecoverableAuthIOException) {

            } catch (e: IOException) {
                ErrorHandler.logError("backupPrivateKey", "Cannot back up private key", e)
            } finally {
                _isDriveButtonEnabled.value = true
            }
        }
    }

    fun deleteBackup() {
        val account = signedInAccount.value ?: return

        viewModelScope.launch {
            _isDriveButtonEnabled.value = false
            try {
                driveBackupManager.deleteBackup(account)
            } catch (e: IOException) {
                ErrorHandler.logError("deleteBackup", "Cannot delete backup", e)
            } finally {
                _isDriveButtonEnabled.value = true
            }
        }
    }
}