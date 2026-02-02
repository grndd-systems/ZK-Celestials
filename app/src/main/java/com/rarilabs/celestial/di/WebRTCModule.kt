package com.rarilabs.celestial.di

import android.content.Context
import com.grndd.celestials.webrtc.core.WebRTCManager
import com.grndd.celestials.webrtc.firebase.FirebaseSignalingManager
import com.grndd.celestials.webrtc.models.WebRTCConfig
import com.grndd.celestials.webrtc.signaling.SignalingManager
import com.rarilabs.celestial.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for WebRTC dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object WebRTCModule {

    @Provides
    @Singleton
    fun provideSignalingManager(): SignalingManager {
        return FirebaseSignalingManager(
            databaseUrl = "https://testground-3159d-default-rtdb.europe-west1.firebasedatabase.app",
            basePath = "signals",
            enableDebugLogging = BuildConfig.DEBUG
        )
    }

    @Provides
    @Singleton
    fun provideWebRTCManager(
        @ApplicationContext context: Context,
        signalingManager: SignalingManager
    ): WebRTCManager {
        return WebRTCManager(
            context = context,
            signalingManager = signalingManager,
            config = WebRTCConfig(
                enableDebugLogging = BuildConfig.DEBUG,
                dataChannelLabel = "RarimeDataChannel"
            )
        )
    }
}
