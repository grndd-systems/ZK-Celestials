package com.rarilabs.celestial.store

import android.graphics.Bitmap
import com.rarilabs.celestial.api.registration.models.LightRegistrationData
import com.rarilabs.celestial.data.enums.AppColorScheme
import com.rarilabs.celestial.data.enums.AppLanguage
import com.rarilabs.celestial.data.enums.PassportCardLook
import com.rarilabs.celestial.data.enums.PassportIdentifier
import com.rarilabs.celestial.data.enums.PassportStatus
import com.rarilabs.celestial.data.enums.SecurityCheckState
import com.rarilabs.celestial.manager.LikenessRule
import com.rarilabs.celestial.manager.WalletAsset
import com.rarilabs.celestial.modules.home.v3.model.WidgetType
import com.rarilabs.celestial.modules.passportScan.models.EDocument
import com.rarilabs.celestial.modules.wallet.models.Transaction
import com.rarilabs.celestial.util.data.GrothProof
import com.rarilabs.celestial.util.data.UniversalProof

interface SecureSharedPrefsManager {

    fun savePrivateKey(privateKey: String)

    fun readPrivateKey(): String?

    fun readPasscodeState(): SecurityCheckState
    fun savePasscodeState(state: SecurityCheckState)

    fun readVisibleWidgets(): List<WidgetType>?
    fun saveVisibleWidgets(visibleCard: List<WidgetType>)

    fun readBiometricsState(): SecurityCheckState
    fun saveBiometricsState(state: SecurityCheckState)

    fun readPassportCardLook(): PassportCardLook
    fun savePassportCardLook(look: PassportCardLook)

    fun readIsPassportIncognitoMode(): Boolean
    fun saveIsPassportIncognitoMode(isIncognito: Boolean)

    fun readPassportIdentifiers(): List<PassportIdentifier>
    fun savePassportIdentifiers(identifiers: List<PassportIdentifier>)

    fun readColorScheme(): AppColorScheme
    fun saveColorScheme(scheme: AppColorScheme)

    fun readLanguage(): AppLanguage
    fun saveLanguage(language: AppLanguage)

    fun readWalletAssets(assetsToPopulate: List<WalletAsset>): List<WalletAsset>
    fun saveWalletAssets(walletAssets: List<WalletAsset>)

    fun readSelectedWalletAsset(walletAssets: List<WalletAsset>): WalletAsset

    fun saveSelectedWalletAsset(walletAsset: WalletAsset)

    fun saveEDocument(eDocument: EDocument)
    fun readEDocument(): EDocument?

    @Deprecated("use Universal proof")
    fun saveRegistrationProof(proof: GrothProof)

    @Deprecated("use Universal proof")
    fun readRegistrationProof(): GrothProof?

    fun readTransactions(): List<Transaction>
    fun addTransaction(transaction: Transaction)

    fun readPasscode(): String
    fun savePasscode(passcode: String)

    fun readLockTimestamp(): Long
    fun saveLockTimestamp(timestamp: Long)

    fun savePassportStatus(passportStatus: PassportStatus)
    fun readPassportStatus(): PassportStatus

    fun saveAccessToken(accessToken: String)
    fun readAccessToken(): String?

    fun saveRefreshToken(refreshToken: String)
    fun readRefreshToken(): String?
    fun clearAllData()

    fun readIsInWaitlist(): Boolean
    fun saveIsInWaitlist(isInWaitlist: Boolean)
    fun deletePassport()

    fun readIsLogsDeleted(): Boolean
    fun saveIsLogsDeleted(isLogsDeleted: Boolean)

    fun saveDeferredReferralCode(referralCode: String)
    fun getDeferredReferralCode(): String?

    fun saveGuessReferralCode(referralCode: String)
    fun getGuessReferralCode(): String?

    fun saveLightRegistrationData(lightRegistrationData: LightRegistrationData)
    fun getLightRegistrationData(): LightRegistrationData?


    fun saveIsAlreadyReserved(isAlreadyReserved: Boolean)
    fun getIsAlreadyReserved(): Boolean

    fun saveSelectedLikenessRule(likenessRule: LikenessRule)
    fun getSelectedLikenessRule(): LikenessRule?

    fun saveLikenessFace(face: Bitmap)
    fun getLikenessFace(): Bitmap?

    fun saveLivenessProof(proof: GrothProof)
    fun getLivenessProof(): GrothProof?


    fun saveUniversalProof(proof: UniversalProof)
    fun readUniversalProof(): UniversalProof?

    fun saveIsShownWelcome(isShown: Boolean)
    fun getIsShownWelcome(): Boolean

    fun saveIdentityLegalScreensShown(isShown: Boolean)
    fun getIdentityLegalScreensShown(): Boolean

}