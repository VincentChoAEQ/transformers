package com.awestruck.transformers.ui.battle

import androidx.lifecycle.ViewModel
import com.awestruck.transformers.model.Battle
import com.awestruck.transformers.model.Transformer

/**
 * Created by Chris on 2018-10-01.
 */
class BattleViewModel(transformers: List<Transformer>) : ViewModel() {

    val battle = Battle(transformers)

}