package com.awestruck.transformers.ui.battle

import androidx.lifecycle.ViewModel
import com.awestruck.transformers.model.Battle
import com.awestruck.transformers.model.Transformers

/**
 * Created by Chris on 2018-10-01.
 */
class BattleViewModel() : ViewModel() {

    lateinit var battle: Battle

    fun start(transformers : Transformers){
        battle = Battle(transformers.transformers)
    }



}