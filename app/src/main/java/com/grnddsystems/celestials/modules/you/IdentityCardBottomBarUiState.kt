package com.grnddsystems.celestials.modules.you

import com.grnddsystems.celestials.data.enums.PassportStatus
import com.grnddsystems.celestials.manager.PassportProofState

data class IdentityCardBottomBarUiState(
    val loadingState: PassportProofState = PassportProofState.READING_DATA,
    val proofError: Exception? = null,
    val passportStatus: PassportStatus? = null
)