package com.awestruck.transformers.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.awestruck.transformers.MainActivity
import com.awestruck.transformers.R
import com.awestruck.transformers.model.Transformer
import kotlinx.android.synthetic.main.list_fragment.*

class ListFragment : Fragment(), ListAdapter.OnTransformerClickListener {


    companion object {
        private const val EXTRA_TEAM = "EXTRA_TEAM"

        fun newInstance(team: String): ListFragment {
            val fragment = ListFragment()

            val bundle = Bundle()
            bundle.putString(EXTRA_TEAM, team)
            fragment.arguments = bundle

            return fragment
        }
    }

    private lateinit var viewModel: ListViewModel

    private val adapter = ListAdapter(this)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val team = arguments?.getString(EXTRA_TEAM)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)

        viewModel.transformers.observe(this, Observer {

            val team = it.filter { it.team == team }

            adapter.setTransformers(team)

            list.adapter = adapter
            list.layoutManager = LinearLayoutManager(context)
        })
    }

    override fun onClick(transformer: Transformer) {
        (activity as? MainActivity)?.showDetails(transformer)
    }
}
