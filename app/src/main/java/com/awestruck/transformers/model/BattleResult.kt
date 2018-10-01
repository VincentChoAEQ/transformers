package com.awestruck.transformers.model

/**
 * Created by Chris on 2018-10-01.
 */
data class BattleResult(
        val lhs: Transformer,
        val rhs: Transformer,
        val result: String
)