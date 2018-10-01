package com.awestruck.transformers.ui.battle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awestruck.transformers.R
import com.awestruck.transformers.model.Battle
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.util.NO_WINNER
import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON
import kotlinx.android.synthetic.main.item_battle.view.*

/**
 * Created by Chris on 2018-10-01.
 */
class BattleAdapter : RecyclerView.Adapter<BattleAdapter.ViewHolder>() {

    private val battles = ArrayList<Pair<Transformer, Boolean>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_battle, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = battles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transformer = battles[position]

        holder.view.name.text = transformer.first.name

        holder.view.destroyed.visibility = if (transformer.second) View.VISIBLE else View.GONE


    }


    fun addBattle(lhs: Transformer, rhs: Transformer) {
        var lhsDestroyed = false
        var rhsDestroyed = false

        val winner = Battle.getWinner(lhs, rhs)
        when (winner) {
            TEAM_AUTOBOT -> rhsDestroyed = true
            TEAM_DECEPTICON -> lhsDestroyed = true
            NO_WINNER -> {
                lhsDestroyed = true
                rhsDestroyed = true
            }
        }


        battles.add(0, lhs to lhsDestroyed)
        battles.add(1, rhs to rhsDestroyed)
        notifyItemRangeInserted(0, 2)
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}