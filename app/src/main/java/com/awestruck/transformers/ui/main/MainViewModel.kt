package com.awestruck.transformers.ui.main

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Chris on 2018-09-30.
 */
class MainViewModel : ViewModel() {

    val team = MediatorLiveData<String>()

}