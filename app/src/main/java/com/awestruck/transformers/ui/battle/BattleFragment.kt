package com.awestruck.transformers.ui.battle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.awestruck.transformers.MainActivity
import com.awestruck.transformers.R
import com.awestruck.transformers.model.Battle
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.util.NO_WINNER
import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON
import kotlinx.android.synthetic.main.battle_fragment.*
import java.util.*


/**
 * Created by Chris on 2018-10-01.
 */
class BattleFragment : Fragment() {

    companion object {
        private const val BATTLE_DELAY = 1250L

        fun newInstance() = BattleFragment()
    }

    private lateinit var viewModel: BattleViewModel
    private val adapter = BattleAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.battle_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val elements = MainActivity.transformers.value ?: emptyList()
        viewModel = ViewModelProviders.of(this, BattleViewModelFactory(elements)).get(BattleViewModel::class.java)

        val battle = viewModel.battle


        list.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, true)
        list.adapter = adapter

        val count = battle.battles

        val autobots = battle.autobots.take(count)
        val decepticons = battle.decepticons.take(count)

        var index = 0

        var lhs = 0
        var rhs = 0

        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {

                    if (index >= count - 1) {
                        cancel()
                        announceWinner()
                    }

                    adapter.addBattle(autobots[index], decepticons[index])
                    // TODO: Update any text of total kills.

                    val winner = Battle.getWinner(autobots[index], decepticons[index])
                    when (winner) {
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
                }
            }
        }

        timer.scheduleAtFixedRate(task, BATTLE_DELAY, BATTLE_DELAY)

    }

    private fun announceWinner() {

    }

    internal class BattleViewModelFactory(private val transformers: List<Transformer>) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return BattleViewModel(transformers) as T
        }
    }
}