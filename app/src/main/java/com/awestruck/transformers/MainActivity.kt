package com.awestruck.transformers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.model.Transformers
import com.awestruck.transformers.ui.MainActivityViewModel
import com.awestruck.transformers.ui.battle.BattleActivity
import com.awestruck.transformers.ui.details.DetailsActivity
import com.awestruck.transformers.ui.details.MainFragment
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel : MainActivityViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            showMain()
        }

        //register app.
        viewModel.allSpark()

        fab.setOnClickListener {
            showDetails()
        }
    }

    private fun showMain() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
    }

    fun showDetails(transformer: Transformer? = null) {
        DetailsActivity.startActivity(this, transformer)
    }

    fun startBattle(transformers: Transformers? = null) {
        BattleActivity.startActivity(this, transformers)
    }
}
