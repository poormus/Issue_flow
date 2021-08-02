package com.mussrose.issueflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mussrose.issueflower.databinding.ItemTourBinding
import com.mussrose.issueflower.entities.tour.Tour

class AdapterTour(private val tourItem:List<Tour>):RecyclerView.Adapter<AdapterTour.TourViewHolder>() {

    class TourViewHolder(private val binding:ItemTourBinding):RecyclerView.ViewHolder(binding.root){
            fun bind(tour: Tour){
                binding.ivTour.setBackgroundResource(tour.resourceId)
                binding.tvTour.text=tour.text
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val binding=ItemTourBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TourViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val tour=tourItem[position]
        holder.bind(tour)
    }

    override fun getItemCount(): Int =tourItem.size
}