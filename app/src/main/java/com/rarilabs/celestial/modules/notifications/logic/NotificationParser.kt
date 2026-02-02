package com.rarilabs.celestial.modules.notifications.logic

import com.google.gson.Gson
import com.rarilabs.celestial.modules.notifications.models.NotificationRewardContent
import com.rarilabs.celestial.modules.notifications.models.NotificationType
import com.rarilabs.celestial.modules.notifications.models.NotificationUniversalContent
import com.rarilabs.celestial.store.room.notifications.models.NotificationEntityData

fun resolveNotificationType(notificationTypeString: String): NotificationType {
    return when (notificationTypeString) {
        "reward" -> NotificationType.REWARD
        "universal" -> NotificationType.UNIVERSAL
        else -> NotificationType.INFO
    }
}

fun parseRewardNotification(notificationEntityData: NotificationEntityData): NotificationRewardContent? {
    val response = try {
        Gson().fromJson(notificationEntityData.data, NotificationRewardContent::class.java)
    } catch (e: Exception) {
        null
    }
    return response
}

fun parseUniversalNotification(notificationEntityData: NotificationEntityData): NotificationUniversalContent? {
    val response = try {
        Gson().fromJson(notificationEntityData.data, NotificationUniversalContent::class.java)
    } catch (e: Exception) {
        null
    }
    return response
}
