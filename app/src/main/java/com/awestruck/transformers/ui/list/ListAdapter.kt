package com.awestruck.transformers.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awestruck.transformers.R
import com.awestruck.transformers.model.Transformer
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_transformer.view.*

/**
 * Created by Chris on 2018-09-29.
 */
class ListAdapter(private val onClickListener: OnTransformerClickListener) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val transformers = ArrayList<Transformer>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transformer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = transformers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transformer = transformers[position]

        holder.view.apply {
            context?.let {
                Glide.with(it).load(transformer.icon)
                        .into(icon)
            }

            name.text = transformer.name

            setOnClickListener {
                onClickListener.onClick(transformer)
            }
        }
    }

    fun setTransformers(team: List<Transformer>) {
        transformers.clear()
        transformers.addAll(team)

        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)


    interface OnTransformerClickListener {
        fun onClick(transformer: Transformer)
    }

}