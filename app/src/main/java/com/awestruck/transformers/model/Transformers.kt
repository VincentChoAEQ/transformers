package com.awestruck.transformers.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Chris on 2018-09-29.
 */
data class Transformers(
        @SerializedName("transformers") val transformers: List<Transformer>
)