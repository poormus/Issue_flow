package com.mussrose.issueflower.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mussrose.issueflower.databinding.ItemProjectBinding
import com.mussrose.issueflower.entities.Project


class AdapterProject() :
    RecyclerView.Adapter<AdapterProject.ViewHolder>() {

    private var onProjectAdapterClick:((Project)->Unit)?=null

    fun setOnProjectAdapterClick( listener:(Project)->Unit){
        onProjectAdapterClick=listener
    }

    class ViewHolder(private val binding: ItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.itemProjectTitle.text = project.name
            binding.itemProjectDesc.text = project.description
            binding.itemProjectState.text = project.state
            if (binding.itemProjectState.text == "open") {
                binding.ivProjectPulsate.visibility = View.VISIBLE
                val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                    binding.ivProjectPulsate,
                    PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                    PropertyValuesHolder.ofFloat("scaleY", 1.2f)
                )
                scaleDown.duration = 310
                scaleDown.repeatCount = ObjectAnimator.INFINITE
                scaleDown.repeatMode = ObjectAnimator.REVERSE
                scaleDown.start()
            } else {
                binding.ivProjectPulsate.visibility = View.GONE
            }

        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val listDiffer = AsyncListDiffer(this, diffUtil)

    var projects: List<Project>
        get() = listDiffer.currentList
        set(value) = listDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
        holder.itemView.setOnClickListener {
            onProjectAdapterClick?.let { click->
                click(project)
            }
        }

    }

    override fun getItemCount(): Int = projects.size
}