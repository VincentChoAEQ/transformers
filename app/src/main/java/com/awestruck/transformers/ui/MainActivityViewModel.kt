package com.awestruck.transformers.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awestruck.transformers.networking.TransformerService
import com.awestruck.transformers.util.Preferences
import com.orhanobut.logger.Logger
import kotlinx.coroutines.launch

class MainActivityViewModel(private val service: TransformerService) : ViewModel() {

    fun allSpark(){
        if (Preferences.isNewUser) {
            viewModelScope.launch {
                Preferences.token = service.getAllSpark()
                Logger.d("Token: ${Preferences.token}")
            }
        }
    }
}
