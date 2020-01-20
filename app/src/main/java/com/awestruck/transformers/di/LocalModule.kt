package com.awestruck.transformers.di

import com.awestruck.transformers.data.TransformerRepository
import com.awestruck.transformers.data.AppDatabase
import com.awestruck.transformers.ui.MainActivityViewModel
import com.awestruck.transformers.ui.battle.BattleViewModel
import com.awestruck.transformers.ui.details.DetailsViewModel
import com.awestruck.transformers.ui.list.ListViewModel
import com.awestruck.transformers.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DATABASE = "DATABASE"

val localModule = module {
    single(named(DATABASE)) { AppDatabase.buildDatabase(androidContext()) }
    factory { (get(named(DATABASE)) as AppDatabase).transformerDao() }
}

val repositoryModule = module {
    single {
        TransformerRepository(get(), get())
    }
}

val viewModelModule = module {
    viewModel { DetailsViewModel(get()) }
    viewModel { ListViewModel(get()) }
    viewModel { BattleViewModel() }
    viewModel { MainViewModel(get()) }
    viewModel { MainActivityViewModel(get())}
}