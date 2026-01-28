package com.grnddsystems.celestials.modules.you

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.manager.IdentityManager
import com.grnddsystems.celestials.manager.WalletManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ZkIdentityPassportViewModel @Inject constructor(
    val identityManager: IdentityManager,
    val walletManager: WalletManager
) : ViewModel() {

    private var _progress = MutableStateFlow(0)

    val progress: StateFlow<Int>
        get() = _progress.asStateFlow()


    fun setProgress(currentProgress: Int) {
        _progress.value = currentProgress
    }


}