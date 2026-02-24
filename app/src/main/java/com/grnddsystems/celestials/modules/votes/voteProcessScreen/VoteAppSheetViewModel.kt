package com.grnddsystems.celestials.modules.votes.voteProcessScreen

import androidx.lifecycle.ViewModel
import com.grnddsystems.celestials.api.voting.models.Poll
import com.grnddsystems.celestials.manager.IdentityManager
import com.grnddsystems.celestials.manager.PassportManager
import com.grnddsystems.celestials.manager.RegistrationManager
import com.grnddsystems.celestials.manager.SettingsManager
import com.grnddsystems.celestials.manager.VotingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VoteAppSheetViewModel @Inject constructor(
    private val votingManager: VotingManager,
    private val settingsManager: SettingsManager,
    private val registrationManager: RegistrationManager,
    private val passportManager: PassportManager,
    private val identityManager: IdentityManager
) : ViewModel() {

    val checkIsVoted = votingManager::checkIsVoted

    val vote = votingManager::vote

    val currentSchema = settingsManager.colorScheme

    val passport = passportManager.passport

    val a = identityManager.registrationProof

    val registrationProof = registrationManager.registrationProof

    fun setSelectedPoll(poll: Poll?) {
        votingManager.setSelectedPoll(poll)
    }

}

