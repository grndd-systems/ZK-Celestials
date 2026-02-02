package com.rarilabs.celestial.modules.notifications

import androidx.lifecycle.ViewModel
import com.rarilabs.celestial.manager.NotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsManager: NotificationManager,
) : ViewModel() {

    val notificationsList = notificationsManager.notificationList

    val readNotifications = notificationsManager::readNotifications

    val deleteNotifications =
        notificationsManager::deleteNotifications

    val addNotifications = notificationsManager::addNotification

}