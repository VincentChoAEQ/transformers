package com.awestruck.transformers.model

import android.os.Parcelable
import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Chris on 2018-09-29.
 */
@Parcelize
data class Transformer(
        @SerializedName("id") val id: String? = null,
        @SerializedName("name") val name: String,
        @SerializedName("strength") var strength: Int = 1,
        @SerializedName("intelligence") val intelligence: Int = 1,
        @SerializedName("speed") val speed: Int = 1,
        @SerializedName("endurance") val endurance: Int = 1,
        @SerializedName("rank") val rank: Int = 1,
        @SerializedName("courage") val courage: Int = 1,
        @SerializedName("firepower") val firepower: Int = 1,
        @SerializedName("skill") val skill: Int = 1,
        @SerializedName("team") val team: String,
        @SerializedName("team_icon") val icon: String? = null
) : Parcelable {

    constructor(name: String, average: Int, team: String)
            : this(null, name, average, average, average, average, average, average, average, average, team)
    
    fun isLeader(): Boolean {
        return when (name.toLowerCase()) {
            "optimus prime",
            "predaking" -> true
            else -> false
        }
    }

    val overallRating: Int
        get() = strength + intelligence + speed + endurance + firepower

    val isAutobot: Boolean
        get() = team == "A"

    val isDecepticon: Boolean
        get() = !isAutobot

    companion object {
        fun mock() = Transformer("-LLbrUN3dQkeejt9vTZc", "Megatron",
                10, 10, 4, 8, 10, 9, 10, 9,
                "D",
                "https://tfwiki.net/mediawiki/images2/archive/8/8d/20110410191659%21Symbol_decept_reg.png")

        fun empty() = Transformer(name = "", team = TEAM_AUTOBOT)
    }
}