package com.awestruck.transformers.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.awestruck.transformers.R
import com.awestruck.transformers.model.LocalTransformer
import kotlinx.android.synthetic.main.view_stat.view.*

/**
 * Created by Chris on 2018-09-29.
 */
class StatView(context: Context?, index: Int, value: Int) : LinearLayout(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_stat, null, false)

        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val name = when (index) {
            LocalTransformer.STRENGTH -> R.string.strength
            LocalTransformer.INTELLIGENCE -> R.string.intelligence
            LocalTransformer.SPEED -> R.string.speed
            LocalTransformer.ENDURANCE -> R.string.endurance
            LocalTransformer.RANK -> R.string.rank
            LocalTransformer.COURAGE -> R.string.courage
            LocalTransformer.FIREPOWER -> R.string.firepower
            LocalTransformer.SKILL -> R.string.skill
            else -> R.string.unknown
        }

        view.name.setText(name)
        view.seek.progress = value
        view.progress.progress = value

        addView(view)
    }

    fun disable() {
        seek.isEnabled = false
        seek.visibility = View.INVISIBLE
    }

    fun enable() {
        seek.isEnabled = true
        seek.visibility = View.VISIBLE
    }

    fun setValue(value: Int) {
        progress.progress = value
        seek.progress = value
    }
}