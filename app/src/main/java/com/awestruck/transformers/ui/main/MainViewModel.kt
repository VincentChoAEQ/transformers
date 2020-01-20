package com.awestruck.transformers.ui.main

import androidx.lifecycle.ViewModel
import com.awestruck.transformers.data.TransformerRepository

class MainViewModel(repository: TransformerRepository) : ViewModel() {
    val transformers = repository.transformers
}