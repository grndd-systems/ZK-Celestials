package com.rarilabs.celestial.modules.home.v3

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.rarilabs.celestial.api.points.models.PointsEventData
import com.rarilabs.celestial.manager.NotificationManager
import com.rarilabs.celestial.manager.PassportManager
import com.rarilabs.celestial.manager.SettingsManager
import com.rarilabs.celestial.modules.manageWidgets.ManageWidgetsManager
import com.rarilabs.celestial.store.SecureSharedPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    app: Application,
    passportManager: PassportManager,
    // private val pointsManager: PointsManager,
    private val widgetsManager: ManageWidgetsManager,
    notificationManager: NotificationManager,
    settingsManager: SettingsManager,
    private val sharedPrefsManager: SecureSharedPrefsManager
) : AndroidViewModel(app) {

    val colorScheme = settingsManager.colorScheme
    var visibleWidgets = widgetsManager.visibleWidgets

    val passport = passportManager.passport

    private var _pointsEventData = MutableStateFlow<PointsEventData?>(null)

    val notifications = notificationManager.notificationList


//    suspend fun initHomeData() = withContext(Dispatchers.IO) {
//        coroutineScope {
//            try {
//                val pointsDeferred = async { loadPointsEvent() }
//                pointsDeferred.await()
//            } catch (e: Exception) {
//            }
//        }
//    }


    fun saveIsShownWelcome(boolean: Boolean) {
        sharedPrefsManager.saveIsShownWelcome(boolean)
    }

    fun getIsShownWelcome(): Boolean {
        return sharedPrefsManager.getIsShownWelcome()
    }

//    private suspend fun loadPointsEvent() {
//        val activeTasksEvents = pointsManager.getActiveEvents().data
//
//        val points = activeTasksEvents.filter {
//            it.attributes.meta.static.name == BaseEvents.REFERRAL_COMMON.value
//        }
//
//        _pointsEventData.value = points.getOrNull(0)
//    }

}