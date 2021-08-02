package com.mussrose.issueflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mussrose.issueflower.databinding.ItemLabelBinding


class AdapterLabel(var labels:List<String>): RecyclerView.Adapter<AdapterLabel.ViewHolder>() {

    class ViewHolder(private val binding: ItemLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: String) {
            binding.tvLabel.text = label
        }
    }

    private var onTagClicked:((String)->Unit)?=null

    fun setOnTagClickListener(listener:(String)->Unit){
        onTagClicked=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemLabelBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val label=labels[position]
        holder.bind(label)
        holder.itemView.setOnClickListener {
            onTagClicked?.let { click->
                click(label)
            }
        }

    }

    override fun getItemCount(): Int {
        return labels.size
    }
}