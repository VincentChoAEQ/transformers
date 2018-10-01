package com.awestruck.transformers

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import com.awestruck.transformers.model.Battle
import com.awestruck.transformers.model.LocalTransformer
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.networking.TransformerService
import com.awestruck.transformers.ui.battle.BattleFragment
import com.awestruck.transformers.ui.details.DetailsFragment
import com.awestruck.transformers.ui.details.MainFragment
import com.awestruck.transformers.ui.list.ListFragment
import com.awestruck.transformers.util.Preferences
import com.awestruck.transformers.util.TEAM_AUTOBOT
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

        val transformers = MediatorLiveData<List<Transformer>>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
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

    fun showList() {
        supportFragmentManager.popBackStackImmediate()

        fab.show()
    }

    fun showDetails(transformer: Transformer? = null) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailsFragment.newInstance(transformer))
                .addToBackStack("details_fragment")
                .commit()

        fab.hide()
    }

    fun startBattle() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, BattleFragment.newInstance())
                .addToBackStack("battle_fragment")
                .commit()

        fab.hide()
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


                    val local = it.transformers.map { LocalTransformer(it) }

                    transformers.postValue(it.transformers)
                }, {
                    Logger.e(it, "Could not work.")
                })
    }


}
