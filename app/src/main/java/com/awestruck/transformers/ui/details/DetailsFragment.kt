package com.awestruck.transformers.ui.details

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import kotlinx.android.synthetic.main.details_fragment.*
import kotlinx.android.synthetic.main.details_fragment.view.*
import kotlinx.android.synthetic.main.view_stat.view.*

class DetailsFragment : Fragment() {

    companion object {


        private const val BACKGROUND_TRANSITION_DURATION = 150
        private const val EXTRA_TRANSFORMER = "EXTRA_TRANSFORMER"


        fun newInstance(transformer: Transformer?): DetailsFragment {
            val fragment = DetailsFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_TRANSFORMER, transformer)
            fragment.arguments = bundle

            return fragment
        }
    }

    private lateinit var viewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.details_fragment, container, false)
        val activity = activity as? AppCompatActivity
        activity?.setSupportActionBar(view.toolbar)
        activity?.supportActionBar?.setDisplayShowTitleEnabled(false)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.details_edit, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val arg = arguments?.getParcelable(EXTRA_TRANSFORMER) as? Transformer

        viewModel = ViewModelProviders.of(this, DetailsViewModelFactory(arg)).get(DetailsViewModel::class.java)

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
            val view = StatView(context, index, stat)

            view.seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, value: Int, p2: Boolean) {
                    viewModel.transformer.stats[index] = value
                    view.setValue(value)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            stats.addView(view)
        }


        save.setOnClickListener {
            saveTransformer()
        }
    }


    private fun selectTeam(team: String) {
        if (team == viewModel.transformer.team) {
            return
        }

        animateTeamIcon(team)
        animateBackgroundColor(team)

        viewModel.transformer.team = team
    }

    private fun animateBackgroundColor(team: String) {
        val color = if (team == TEAM_AUTOBOT) R.color.autobot_light else R.color.decepticon_light

        val colorFrom = (root.background as ColorDrawable).color
        val colorTo = ContextCompat.getColor(context ?: return, color)
        val animation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        animation.duration = BACKGROUND_TRANSITION_DURATION.toLong()
        animation.addUpdateListener { animator -> root.setBackgroundColor(animator.animatedValue as Int) }
        animation.start()
    }

    private fun animateTeamIcon(team: String) {
        val context = context ?: return


        val constraint = ConstraintSet()
        constraint.clone(icons_container)

        val selected = if (team == TEAM_AUTOBOT) R.id.icon_autobot else R.id.icon_decepticon
        val unselected = if (team == TEAM_AUTOBOT) R.id.icon_decepticon else R.id.icon_autobot

        val startSide = if (team == TEAM_AUTOBOT) ConstraintSet.START else ConstraintSet.END
        val endSide = if (team == TEAM_AUTOBOT) ConstraintSet.END else ConstraintSet.START


        val size = context.resources.getDimension(R.dimen.icon_size).toInt()
        val smallSize = context.resources.getDimension(R.dimen.icon_size_small).toInt()
        val margin = context.resources.getDimension(R.dimen.icon_margin).toInt()

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_edit -> {
                viewModel.state.value = DetailsViewModel.STATE_EDIT
            }
            R.id.action_delete -> {
                deleteTransformer()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTransformer() {

        val id = viewModel.transformer.id ?: return

        TransformerService.create()
                .delete(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                    MainActivity.deleteTransformer(id)
                    finish()
                }, {
                    Toast.makeText(context, "Error while deleting the transformer", Toast.LENGTH_SHORT).show()
                    MainActivity.deleteTransformer(id)
                    finish()
                })

    }

    private fun finish() {
        (activity as? MainActivity)?.showList()
    }

    private fun saveTransformer() {

        if (viewModel.transformer.name.isBlank()) {
            Toast.makeText(context, "Transformer must have a valid name", Toast.LENGTH_SHORT).show()
            return
        }

        TransformerService.create()
                .add(viewModel.transformer.toTransformer())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()

                    val value = MainActivity.transformers.value?.toMutableList() ?: arrayListOf()
                    value.add(it)

                    MainActivity.transformers.postValue(value)

                    finish()
                }, {
                    Toast.makeText(context, "Could not save the transformer", Toast.LENGTH_SHORT).show()
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


    internal class DetailsViewModelFactory(private val transformer: Transformer?) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DetailsViewModel(transformer) as T
        }
    }
}
