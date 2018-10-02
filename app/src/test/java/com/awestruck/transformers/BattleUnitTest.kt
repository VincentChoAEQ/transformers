package com.awestruck.transformers

import com.awestruck.transformers.model.Battle
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.util.MASS_EXTINCTION
import com.awestruck.transformers.util.NO_WINNER
import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON
import org.junit.Assert.assertEquals
import org.junit.Test

class BattleUnitTest {

    private val optimus = Transformer("Optimus Prime", 10, TEAM_AUTOBOT)
    private val predaking = Transformer("PREDAKING", 10, TEAM_DECEPTICON)

    private val weakAutobot = Transformer("Weak Autobot", 1, TEAM_AUTOBOT)
    private val weakDecepticon = Transformer("Weak Deception", 1, TEAM_DECEPTICON)

    private val strongAutobot = Transformer("Strong Autobot", 10, TEAM_AUTOBOT)
    private val strongDecepticon = Transformer("Strong Deception", 10, TEAM_DECEPTICON)


    @Test
    fun `test is leader`() {
        assertEquals(optimus.isLeader(), true)
        assertEquals(predaking.isLeader(), true)
    }

    @Test
    fun `test is not leader`() {
        val nonLeader = Transformer("other", 1, TEAM_AUTOBOT)
        assertEquals(nonLeader.isLeader(), false)
    }

    @Test
    fun `test overall rating`() {
        assertEquals(optimus.total, 50)
        assertEquals(predaking.total, 50)
    }

    @Test
    fun `test overall rating battle`() {
        assertEquals(Battle.isStronger(strongAutobot, weakDecepticon), true)
        assertEquals(Battle.isStronger(strongDecepticon, weakAutobot), true)
        assertEquals(Battle.isStronger(weakAutobot, strongDecepticon), false)
        assertEquals(Battle.isStronger(weakAutobot, weakDecepticon), false)
    }

    @Test
    fun `test is powering`() {
        val overpowered = Transformer(name = "Overpowered", average = 10, team = TEAM_AUTOBOT)
        val underpowered = Transformer(name = "Underpowered", average = 1, team = TEAM_DECEPTICON)
        val even = Transformer(name = "Even", average = 8, team = TEAM_AUTOBOT)

        assertEquals(Battle.isOverpowering(overpowered, underpowered), true)
        assertEquals(Battle.isOverpowering(underpowered, overpowered), false)
        assertEquals(Battle.isOverpowering(even, even), false)
        assertEquals(Battle.isOverpowering(overpowered, even), false)
    }

    @Test
    fun `test is intimidating`() {
        val intimidating = Transformer(name = "Intimidating", average = 10, team = TEAM_DECEPTICON)
        val coward = Transformer(name = "Coward", average = 1, team = TEAM_AUTOBOT)
        val normal = Transformer(name = "Normal", average = 7, team = TEAM_DECEPTICON)

        assertEquals(Battle.isIntimidating(intimidating, coward), true)
        assertEquals(Battle.isIntimidating(coward, intimidating), false)
        assertEquals(Battle.isIntimidating(intimidating, normal), false)
        assertEquals(Battle.isIntimidating(normal, normal), false)
    }

    @Test
    fun `test is too skilled`() {
        val skilled = Transformer(name = "Skilled", average = 8, team = TEAM_DECEPTICON)
        val unskilled = Transformer(name = "git gud", average = 1, team = TEAM_AUTOBOT)
        val normal = Transformer(name = "Normal", average = 7, team = TEAM_DECEPTICON)

        assertEquals(Battle.isTooSkilled(skilled, unskilled), true)
        assertEquals(Battle.isTooSkilled(unskilled, skilled), false)
        assertEquals(Battle.isTooSkilled(skilled, normal), false)
        assertEquals(Battle.isTooSkilled(normal, normal), false)
    }

    @Test
    fun `test equal power`() {
        val powerful = Transformer(name = "Powerful", average = 8, team = TEAM_AUTOBOT)
        val weak = Transformer(name = "Weak", average = 1, team = TEAM_AUTOBOT)
        val average = Transformer(name = "Average", average = 5, team = TEAM_AUTOBOT)

        assertEquals(Battle.equalPower(powerful, weak), false)
        assertEquals(Battle.equalPower(weak, powerful), false)
        assertEquals(Battle.equalPower(powerful, average), false)
        assertEquals(Battle.equalPower(average, average), true)
    }

    @Test
    fun `test is strong`() {
        val powerful = Transformer(name = "Powerful", average = 8, team = TEAM_AUTOBOT)
        val weak = Transformer(name = "Weak", average = 1, team = TEAM_AUTOBOT)
        val average = Transformer(name = "Average", average = 5, team = TEAM_AUTOBOT)

        assertEquals(Battle.isStronger(powerful, weak), true)
        assertEquals(Battle.isStronger(weak, powerful), false)
        assertEquals(Battle.isStronger(powerful, average), true)
        assertEquals(Battle.isStronger(average, average), false)
    }

    @Test
    fun `test winner`() {
        val powerful = Transformer(name = "Powerful", average = 8, team = TEAM_AUTOBOT)
        val weak = Transformer(name = "Weak", average = 1, team = TEAM_DECEPTICON)
        val average = Transformer(name = "Average", average = 5, team = TEAM_DECEPTICON)


        assertEquals(Battle.getWinner(powerful, weak), TEAM_AUTOBOT)
        assertEquals(Battle.getWinner(weak, powerful), TEAM_DECEPTICON)
        assertEquals(Battle.getWinner(powerful, average), TEAM_AUTOBOT)
        assertEquals(Battle.getWinner(average, powerful), TEAM_DECEPTICON)
        assertEquals(Battle.getWinner(optimus, powerful), TEAM_AUTOBOT)
        assertEquals(Battle.getWinner(weak, weak), NO_WINNER)
        assertEquals(Battle.getWinner(weak, predaking), TEAM_DECEPTICON)
        assertEquals(Battle.getWinner(optimus, predaking), MASS_EXTINCTION)
    }
}
