package com.awestruck.transformers.ui.list

import androidx.lifecycle.ViewModel
import com.awestruck.transformers.data.TransformerRepository

class ListViewModel(repository: TransformerRepository) : ViewModel() {
    val transformers = repository.transformers
}
