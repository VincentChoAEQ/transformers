package com.awestruck.transformers.model

import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON
import java.util.*

/**
 * Created by Chris on 2018-09-29.
 */
class LocalTransformer(transformer: Transformer) {


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
        private const val SKILLS_DEFAULT = 1
        const val SKILLS_MAX = 10
        const val SKILLS_MIN = 1
    }

    val id = transformer.id
    var name = transformer.name
    var team = transformer.team
    val icon = transformer.icon

    val stats = Array(SKILLS_SIZE, init = { SKILLS_DEFAULT })

    constructor() : this(Transformer.empty())


    init {
        stats[STRENGTH] = transformer.strength
        stats[INTELLIGENCE] = transformer.intelligence
        stats[SPEED] = transformer.speed
        stats[ENDURANCE] = transformer.endurance
        stats[RANK] = transformer.rank
        stats[COURAGE] = transformer.courage
        stats[FIREPOWER] = transformer.firepower
        stats[SKILL] = transformer.skill
    }

    fun randomize() {
        val random = Random()

        val names = listOf("Optimus Prime", "PREDAKING", "Rat Trap", "Solaire", "Ash",
                "Cyrpto", "Cena", "Vlad the Impaler", "HULK", "Batman", "Megatron", "Megatron v2.0", "UNKNOWN")

        name = names[random.nextInt(names.size)]

        team = if (random.nextBoolean()) TEAM_AUTOBOT else TEAM_DECEPTICON

        for (i in 0 until SKILLS_SIZE) {
            val value = SKILLS_MIN + random.nextInt(SKILLS_MAX - SKILLS_MIN)
            stats[i] = value
        }
    }

    fun toTransformer(): Transformer {
        return Transformer(
                id,
                name,
                stats[STRENGTH],
                stats[INTELLIGENCE],
                stats[SPEED],
                stats[ENDURANCE],
                stats[RANK],
                stats[COURAGE],
                stats[FIREPOWER],
                stats[SKILL],
                team,
                icon
        )
    }
}