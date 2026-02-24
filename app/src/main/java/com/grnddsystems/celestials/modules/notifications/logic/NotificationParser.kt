package com.grnddsystems.celestials.modules.notifications.logic

import com.google.gson.Gson
import com.grnddsystems.celestials.modules.notifications.models.NotificationRewardContent
import com.grnddsystems.celestials.modules.notifications.models.NotificationType
import com.grnddsystems.celestials.modules.notifications.models.NotificationUniversalContent
import com.grnddsystems.celestials.store.room.notifications.models.NotificationEntityData

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
