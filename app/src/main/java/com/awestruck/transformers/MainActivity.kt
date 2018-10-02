package com.awestruck.transformers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.networking.TransformerService
import com.awestruck.transformers.ui.battle.BattleActivity
import com.awestruck.transformers.ui.details.DetailsActivity
import com.awestruck.transformers.ui.details.MainFragment
import com.awestruck.transformers.ui.splash.SplashFragment
import com.awestruck.transformers.util.Preferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.orhanobut.logger.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    companion object {
        fun deleteTransformer(id: String) {
            val value = transformers.value as? MutableList
            value?.remove(value.firstOrNull { it.id == id })
            transformers.postValue(value)
        }

        fun updateTransformer(transformer: Transformer) {
            val value = transformers.value as? MutableList
            value?.remove(value.firstOrNull { it.id == transformer.id })
            value?.add(transformer)
            transformers.postValue(value)
        }

        fun addTransformer(transformer: Transformer) {
            val value = transformers.value as? MutableList
            value?.add(transformer)
            transformers.postValue(value)
        }

        val transformers = MediatorLiveData<List<Transformer>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            showInitialScreen()
        }

        if (Preferences.isNewUser) {
            getToken()
        } else {
            getTransformers()
        }

        fab.setOnClickListener {
            showDetails()
        }
    }

    private fun showInitialScreen() {
        showMain()
    }

    private fun showMain() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
    }

    fun showDetails(transformer: Transformer? = null) {
        DetailsActivity.startActivity(this, transformer)
    }

    fun startBattle() {
        BattleActivity.startActivity(this)
    }

    private fun getToken() {
        TransformerService.create()
                .getAllSpark()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Logger.d("Got token $it")
                    Preferences.token = it
                    getTransformers()
                }, {
                    Logger.e(it, "Could not work.")
                })
    }

    private fun getTransformers() {
        TransformerService.create()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    transformers.postValue(it.transformers)
                    showMain()
                }, {
                    Logger.e(it, "Could not work.")
                })
    }
}
