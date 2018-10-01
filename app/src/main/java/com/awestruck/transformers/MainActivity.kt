package com.awestruck.transformers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MediatorLiveData
import com.awestruck.transformers.model.LocalTransformer
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.networking.TransformerService
import com.awestruck.transformers.ui.battle.BattleFragment
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


    private val mainFragment = MainFragment.newInstance()
    private var detailsFragment: DetailsActivity? = null

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
            if (showingDetails()) {
                randomizeDetails()
            } else {
                showDetails()
            }
        }
    }

    private fun showingDetails() = detailsFragment != null

    private fun randomizeDetails() {
        detailsFragment?.randomize()
    }

    private fun showInitialScreen() {
        if (Preferences.isNewUser) {
            showSplash()
        } else {
            showMain()
        }
    }

    private fun showSplash() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SplashFragment.newInstance())
                .commitNow()
    }

    private fun showMain() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, mainFragment)
                .commitNow()
    }

    fun showList() {
        supportFragmentManager.popBackStackImmediate()

        fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                fab?.setImageResource(R.drawable.ic_add_black_24dp)
                fab?.show()
            }
        })
    }

    override fun onBackPressed() {
        if (detailsFragment != null) {
            detailsFragment = null
        }

        fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton?) {
                super.onHidden(fab)
                fab?.setImageResource(R.drawable.ic_add_black_24dp)
                fab?.show()
            }
        })


        super.onBackPressed()
    }

    fun showDetails(transformer: Transformer? = null) {
        DetailsActivity.startActivity(this, transformer)
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

                    showMain()
                }, {
                    Logger.e(it, "Could not work.")
                })
    }


}
