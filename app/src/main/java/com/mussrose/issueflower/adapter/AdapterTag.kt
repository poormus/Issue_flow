package com.mussrose.issueflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mussrose.issueflower.databinding.ItemLabelBinding


class AdapterTag(private val tags:MutableList<String>) : RecyclerView.Adapter<AdapterTag.ViewHolder>() {


    class ViewHolder(private val binding: ItemLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label:String) {
            binding.tvLabel.text = label

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemLabelBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val label=tags[position]
        holder.bind(label)
    }

    override fun getItemCount(): Int {
        return tags.size
    }
}