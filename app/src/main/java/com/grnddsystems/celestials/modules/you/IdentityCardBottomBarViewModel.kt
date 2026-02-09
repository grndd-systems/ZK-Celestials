package com.grnddsystems.celestials.modules.you

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.PassportManager
import com.grnddsystems.celestials.manager.ProofGenerationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IdentityCardBottomBarViewModel @Inject constructor(
    private val proofGenerationManager: ProofGenerationManager,
    private val passportManager: PassportManager
) : ViewModel()

