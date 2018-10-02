package com.awestruck.transformers.model

import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON
import java.io.Serializable
import java.util.*

/**
 * Created by Chris on 2018-09-29.
 */
data class Transformer(
        val id: String? = null,
        var name: String,
        var team: String,
        val icon: String? = null
) : Serializable {

    val specs: Specs = Specs()

    constructor(name: String, average: Int, team: String) : this(null, name, team) {
        specs.setAverage(average)
    }

    constructor(id: String?, name: String, specs: Specs, team: String, icon: String?): this(id, name, team, icon) {
        this.specs.clone(specs)
    }

    constructor() : this(name = "", team = TEAM_AUTOBOT)

    companion object {

        const val STRENGTH = 0
        const val INTELLIGENCE = 1
        const val SPEED = 2
        const val ENDURANCE = 3
        const val RANK = 4
        const val COURAGE = 5
        const val FIREPOWER = 6
        const val SKILL = 7

        const val SKILLS_SIZE = 8
        const val SKILLS_MAX = 10
        const val SKILLS_MIN = 1
        private const val SKILLS_DEFAULT = SKILLS_MIN
    }

    fun isLeader(): Boolean {
        return when (name.toLowerCase()) {
            "optimus prime",
            "predaking" -> true
            else -> false
        }
    }

    val isAutobot: Boolean
        get() = team == "A"

    val isDecepticon: Boolean
        get() = !isAutobot

    val stats: Array<Int>
        get() = specs.array

    val total: Int
        get() = specs.total

    fun randomize() {
        val random = Random()

        val names = listOf("Optimus Prime", "PREDAKING", "Rat Trap", "Solaire", "Ash",
                "Cyrpto", "Cena", "Vlad the Impaler", "HULK", "Batman", "Megatron", "Megatron v2.0", "UNKNOWN")

        name = names[random.nextInt(names.size)]

        team = if (random.nextBoolean()) TEAM_AUTOBOT else TEAM_DECEPTICON

        specs.randomize()
    }

    operator fun get(position: Int) = specs[position]

    class Specs(strength: Int, intelligence: Int, speed: Int, endurance: Int,
                      rank: Int, courage: Int, firepower: Int, skill: Int) : Serializable {

        constructor(average: Int) : this(average, average, average, average, average, average, average, average)

        constructor() : this(SKILLS_DEFAULT)

        internal val array = Array(SKILLS_SIZE) { SKILLS_DEFAULT }

        internal val total: Int
            get() = array[STRENGTH] + array[INTELLIGENCE] + array[SPEED] + array[ENDURANCE] + array[FIREPOWER]

        init {
            array[STRENGTH] = strength
            array[INTELLIGENCE] = intelligence
            array[SPEED] = speed
            array[ENDURANCE] = endurance
            array[RANK] = rank
            array[COURAGE] = courage
            array[FIREPOWER] = firepower
            array[SKILL] = skill
        }

        fun setAverage(average: Int) {
            for (i in 0 until SKILLS_SIZE) {
                array[i] = average
            }
        }

        fun clone(specs: Specs) {
            for (i in 0 until SKILLS_SIZE) {
                array[i] = specs[i]
            }
        }

        fun randomize() {
            val random = Random()

            for (i in 0 until SKILLS_SIZE) {
                array[i] = SKILLS_MIN + random.nextInt(SKILLS_MAX - SKILLS_MIN)
            }
        }

        operator fun get(position: Int) = array[position]

    }
}