package com.awestruck.transformers.ui.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.awestruck.transformers.model.Transformer

class DetailsViewModel(arg: Transformer?) : ViewModel() {

    companion object {
        const val STATE_CREATE = 0
        const val STATE_VIEW = 1
        const val STATE_EDIT = 2
    }

    val state = MediatorLiveData<Int>()

    var transformer = arg ?: Transformer()

    val isEditing: Boolean
        get() = state.value == STATE_CREATE || state.value == STATE_EDIT

    init {
        state.value = if (transformer.id != null) STATE_VIEW else STATE_CREATE
    }

}
