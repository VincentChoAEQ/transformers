package com.awestruck.transformers.ui.details

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.awestruck.transformers.MainActivity
import com.awestruck.transformers.R
import com.awestruck.transformers.model.Transformer
import com.awestruck.transformers.networking.TransformerService
import com.awestruck.transformers.ui.StatView
import com.awestruck.transformers.util.TEAM_AUTOBOT
import com.awestruck.transformers.util.TEAM_DECEPTICON
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.details_activity.*
import kotlinx.android.synthetic.main.view_stat.view.*

class DetailsActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    companion object {

        private const val BACKGROUND_TRANSITION_DURATION = 150
        private const val EXTRA_TRANSFORMER = "EXTRA_TRANSFORMER"

        fun startActivity(context: Context, transformer: Transformer?) {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(EXTRA_TRANSFORMER, transformer)
            context.startActivity(intent)
        }
    }

    private lateinit var viewModel: DetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_activity)

        val transformer = intent.getSerializableExtra(EXTRA_TRANSFORMER) as? Transformer

        viewModel = ViewModelProviders.of(this, DetailsViewModelFactory(transformer)).get(DetailsViewModel::class.java)
        viewModel.state.observe(this, Observer {
            setEditMode(it)
        })

        icon_decepticon.setOnClickListener {
            selectTeam(TEAM_DECEPTICON)
        }

        icon_autobot.setOnClickListener {
            selectTeam(TEAM_AUTOBOT)
        }

        name.setText(viewModel.transformer.name)
        animateTeamIcon(viewModel.transformer.team)
        animateBackgroundColor(viewModel.transformer.team)

        name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null)
                    viewModel.transformer.name = p0.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        })

        viewModel.transformer.stats.forEachIndexed { index, stat ->
            val view = StatView(this, index, stat)

            view.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                    val value = Math.max(Transformer.SKILLS_MIN, value)
                    viewModel.transformer.stats[index] = value
                    view.setValue(value)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            stats.addView(view)
        }

        close.setOnClickListener {
            finish()
        }

        options.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.inflate(R.menu.details_edit)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }

        save.setOnClickListener {
            saveTransformer()
        }

        fab.setOnClickListener {
            randomize()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun selectTeam(team: String) {
        animateTeamIcon(team)
        animateBackgroundColor(team)

        viewModel.transformer.team = team
    }

    private fun animateBackgroundColor(team: String) {
        val color = if (team == TEAM_AUTOBOT) R.color.autobot_light else R.color.decepticon_light

        val colorFrom = (root.background as ColorDrawable).color
        val colorTo = ContextCompat.getColor(this, color)
        val animation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        animation.duration = BACKGROUND_TRANSITION_DURATION.toLong()
        animation.addUpdateListener { animator -> root.setBackgroundColor(animator.animatedValue as Int) }
        animation.start()
    }

    private fun animateTeamIcon(team: String) {


        val constraint = ConstraintSet()
        constraint.clone(icons_container)

        val selected = if (team == TEAM_AUTOBOT) R.id.icon_autobot else R.id.icon_decepticon
        val unselected = if (team == TEAM_AUTOBOT) R.id.icon_decepticon else R.id.icon_autobot

        val startSide = if (team == TEAM_AUTOBOT) ConstraintSet.START else ConstraintSet.END
        val endSide = if (team == TEAM_AUTOBOT) ConstraintSet.END else ConstraintSet.START


        val size = resources.getDimension(R.dimen.icon_size).toInt()
        val smallSize = resources.getDimension(R.dimen.icon_size_small).toInt()
        val margin = resources.getDimension(R.dimen.icon_margin).toInt()

        constraint.clear(selected)
        constraint.constrainHeight(selected, size)
        constraint.constrainWidth(selected, size)
        constraint.centerHorizontally(selected, R.id.icons_container)
        constraint.centerVertically(selected, R.id.icons_container)

        constraint.clear(unselected)
        constraint.constrainHeight(unselected, smallSize)
        constraint.constrainWidth(unselected, smallSize)
        constraint.centerVertically(unselected, R.id.icons_container)
        constraint.connect(unselected, startSide, selected, endSide, margin)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(icons_container)
            constraint.applyTo(icons_container)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.action_edit -> {
                viewModel.state.value = DetailsViewModel.STATE_EDIT
                true
            }
            R.id.action_delete -> {
                deleteTransformer()
                true
            }
            else -> false
        }
    }

    private fun deleteTransformer() {

        val id = viewModel.transformer.id ?: return

        TransformerService.create()
                .delete(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                    MainActivity.deleteTransformer(id)
                    finish()
                }, {
                    Toast.makeText(this, "Error while deleting the transformer", Toast.LENGTH_SHORT).show()
                    MainActivity.deleteTransformer(id)
                    finish()
                })

    }

    private fun saveTransformer() {
        val transformer = viewModel.transformer

        if (transformer.name.isBlank()) {
            Toast.makeText(this, "Transformer must have a valid name", Toast.LENGTH_SHORT).show()
            return
        }

        if (transformer.id != null) {
            updateTransformer(transformer)
        } else {
            createTransformer(transformer)
        }
    }

    private fun createTransformer(transformer: Transformer) {
        TransformerService.create()
                .add(transformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    MainActivity.addTransformer(it)
                    viewModel.state.postValue(DetailsViewModel.STATE_VIEW)
                }, {
                    Toast.makeText(this, "Could not create the transformer", Toast.LENGTH_SHORT).show()
                })
    }

    private fun updateTransformer(transformer: Transformer) {
        TransformerService.create()
                .update(transformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                    MainActivity.updateTransformer(it)
                    viewModel.state.postValue(DetailsViewModel.STATE_VIEW)
                }, {
                    Toast.makeText(this, "Could not update the transformer", Toast.LENGTH_SHORT).show()
                })
    }

    private fun setEditMode(state: Int) {

        val isEditing = state == DetailsViewModel.STATE_EDIT || state == DetailsViewModel.STATE_CREATE

        for (i in 0 until stats.childCount) {
            val view = stats.getChildAt(i) as StatView
            if (isEditing) {
                view.enable()
            } else {
                view.disable()
            }
        }

        if (isEditing) {
            fab.show()
        } else {
            fab.hide()
        }

        options.visibility = if (state == DetailsViewModel.STATE_VIEW) View.VISIBLE else View.GONE

        save.visibility = if (isEditing) View.VISIBLE else View.INVISIBLE

        if (isEditing) {
            icon_autobot.visibility = View.VISIBLE
            icon_decepticon.visibility = View.VISIBLE
        } else {
            val team = viewModel.transformer.team
            if (team == TEAM_AUTOBOT) {
                icon_decepticon.visibility = View.GONE
            } else {
                icon_autobot.visibility = View.GONE
            }
        }

        name.isEnabled = isEditing
    }

    fun randomize() {
        viewModel.transformer.let { transformer ->
            transformer.randomize()

            name.setText(transformer.name)
            selectTeam(transformer.team)

            transformer.stats.forEachIndexed { index, value ->
                (stats.getChildAt(index) as StatView).setValue(value)
            }
        }
    }


    internal class DetailsViewModelFactory(private val transformer: Transformer?) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailsViewModel(transformer) as T
        }
    }
}
