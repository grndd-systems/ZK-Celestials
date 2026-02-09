package com.grnddsystems.celestials.di

import android.content.Context
import com.grndd.celestials.webrtc.core.WebRTCManager
import com.grndd.celestials.webrtc.firebase.FirebaseSignalingManager
import com.grndd.celestials.webrtc.models.WebRTCConfig
import com.grndd.celestials.webrtc.signaling.SignalingManager
import com.google.firebase.FirebaseApp
import com.grnddsystems.celestials.BuildConfig
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
        val databaseUrl = FirebaseApp.getInstance().options.databaseUrl
            ?: throw IllegalStateException("firebase_url not found in google-services.json")
        return FirebaseSignalingManager(
            databaseUrl = databaseUrl,
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