package com.awestruck.transformers.ui.battle

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awestruck.transformers.MainActivity
import com.awestruck.transformers.R
import com.awestruck.transformers.model.Battle
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.util.MASS_EXTINCTION
import com.awestruck.transformers.util.NO_WINNER
import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON
import kotlinx.android.synthetic.main.battle_activity.*
import java.util.*


/**
 * Created by Chris on 2018-10-01.
 */
class BattleActivity : AppCompatActivity() {

    companion object {
        private const val BATTLE_DELAY = 1250L
        private const val BATTLE_DELAY_MASS_EXTINCTION = 250L

        fun startActivity(context: Context) {
            val intent = Intent(context, BattleActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: BattleViewModel
    private lateinit var adapter: BattleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.battle_activity)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_close_white_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val elements = MainActivity.transformers.value
                ?: Array(30) { Transformer().also { it.randomize() } }.toList()
        viewModel = ViewModelProviders.of(this, BattleViewModelFactory(elements)).get(BattleViewModel::class.java)

        val battle = viewModel.battle

        adapter = BattleAdapter(this)

        list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        list.adapter = adapter

        var index = 0

        var lhs = 0
        var rhs = 0

        val delay = if (battle.victor == MASS_EXTINCTION) BATTLE_DELAY_MASS_EXTINCTION else BATTLE_DELAY

        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    val result = battle.results[index]

                    if(result.result == MASS_EXTINCTION) {
                        adapter.addNotification(BattleAdapter.BattleNotification(getString(R.string.battle_mass_extinction)))
                    }
                    adapter.addBattle(result)

                    when (result.result) {
                        TEAM_AUTOBOT -> lhs++
                        TEAM_DECEPTICON -> rhs++
                        NO_WINNER -> {
                            lhs++
                            rhs++
                        }
                    }

                    autobots_score.text = lhs.toString()
                    decepticons_score.text = rhs.toString()

                    index++

                    if (index >= battle.results.size) {
                        adapter.addNotification(BattleAdapter.BattleNotification(getString(R.string.battle_end)))

                        cancel()
                        announceWinner(battle)
                    }

                    list.layoutManager?.smoothScrollToPosition(list, RecyclerView.State(), 0)
                }
            }
        }

        timer.scheduleAtFixedRate(task, delay, delay)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun announceWinner(battle: Battle) {
        Toast.makeText(this, battle.victor, Toast.LENGTH_SHORT).show()
    }

    internal class BattleViewModelFactory(private val transformers: List<Transformer>) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BattleViewModel(transformers) as T
        }
    }
}