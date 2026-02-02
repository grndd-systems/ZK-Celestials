package com.rarilabs.celestial.modules.votes.voteProcessScreen

import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.api.voting.models.Poll
import com.rarilabs.celestial.manager.IdentityManager
import com.rarilabs.celestial.manager.PassportManager
import com.rarilabs.celestial.manager.RegistrationManager
import com.rarilabs.celestial.manager.SettingsManager
import com.rarilabs.celestial.manager.VotingManager
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

