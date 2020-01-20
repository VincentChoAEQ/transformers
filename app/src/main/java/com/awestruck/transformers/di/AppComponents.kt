package com.awestruck.transformers.di

val appComponent= listOf(createRemoteModule("https://transformers-api.firebaseapp.com"), localModule, repositoryModule, viewModelModule)