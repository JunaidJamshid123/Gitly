package com.example.gitly.data.model

import com.google.gson.annotations.SerializedName

data class ContributionCalendar(
    val totalContributions: Int,
    val weeks: List<Week>
)

data class Week(
    val contributionDays: List<ContributionDay>
)

data class ContributionDay(
    val date: String,              // "2026-01-07"
    val contributionCount: Int,    // number of contributions
    val weekday: Int,              // 0-6 (Sunday to Saturday)
    val level: Int,                // 0-4 intensity level (computed)
    val color: String? = null      // Optional: hex color from API
) {
    fun getColorInt(): Int {
        return when (level) {
            0 -> android.graphics.Color.parseColor("#EBEDF0")
            1 -> android.graphics.Color.parseColor("#9BE9A8")
            2 -> android.graphics.Color.parseColor("#40C463")
            3 -> android.graphics.Color.parseColor("#30A14E")
            else -> android.graphics.Color.parseColor("#216E39")
        }
    }
}

// GraphQL response wrappers for the raw API response
data class ContributionCalendarResponse(
    val data: ContributionData
)

data class ContributionData(
    val user: UserContribution?
)

data class UserContribution(
    val contributionsCollection: ContributionsCollection
)

data class ContributionsCollection(
    val contributionCalendar: RawContributionCalendar
)

// Raw API response structure
data class RawContributionCalendar(
    val totalContributions: Int,
    val weeks: List<RawWeek>
)

data class RawWeek(
    val contributionDays: List<RawContributionDay>
)

data class RawContributionDay(
    val date: String,
    val contributionCount: Int,
    val weekday: Int,
    val color: String  // GitHub returns hex color string
)
