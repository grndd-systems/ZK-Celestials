package com.rarilabs.celestial.modules.you

import com.rarilabs.celestial.data.enums.PassportStatus
import com.rarilabs.celestial.manager.PassportProofState

data class IdentityCardBottomBarUiState(
    val loadingState: PassportProofState = PassportProofState.READING_DATA,
    val proofError: Exception? = null,
    val passportStatus: PassportStatus? = null
)