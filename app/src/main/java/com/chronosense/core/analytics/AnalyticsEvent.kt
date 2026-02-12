package com.chronosense.core.analytics

/**
 * Structured, type-safe analytics events.
 *
 * Platform-agnostic: implementations wire to Firebase Analytics, Amplitude,
 * Mixpanel, or any other provider. During development, [NoOpTracker] is used.
 *
 * Each event subclass carries strongly-typed [params] for guaranteed schema consistency.
 */
sealed class AnalyticsEvent(val name: String, val params: Map<String, Any> = emptyMap()) {

    class EntryCreated(mood: String?, tagCount: Int, descLen: Int) : AnalyticsEvent(
        "entry_created",
        mapOf("mood" to (mood ?: "none"), "tag_count" to tagCount, "desc_length" to descLen)
    )

    class EntryEdited(entryId: Long) : AnalyticsEvent(
        "entry_edited", mapOf("entry_id" to entryId)
    )

    class EntryDeleted(entryId: Long) : AnalyticsEvent(
        "entry_deleted", mapOf("entry_id" to entryId)
    )

    class DayViewed(date: String, completionRate: Float) : AnalyticsEvent(
        "day_viewed", mapOf("date" to date, "completion" to completionRate)
    )

    class MonthViewed(yearMonth: String) : AnalyticsEvent(
        "month_viewed", mapOf("year_month" to yearMonth)
    )

    class SettingsChanged(key: String, value: String) : AnalyticsEvent(
        "settings_changed", mapOf("key" to key, "value" to value)
    )

    class NotificationTapped(slot: String) : AnalyticsEvent(
        "notification_tapped", mapOf("slot" to slot)
    )
}

/**
 * Analytics tracker abstraction. Swap implementations per build variant.
 */
interface AnalyticsTracker {
    fun track(event: AnalyticsEvent)
    fun setUserProperty(key: String, value: String)
}

/** No-op tracker for debug builds and tests. */
class NoOpTracker : AnalyticsTracker {
    override fun track(event: AnalyticsEvent) = Unit
    override fun setUserProperty(key: String, value: String) = Unit
}
