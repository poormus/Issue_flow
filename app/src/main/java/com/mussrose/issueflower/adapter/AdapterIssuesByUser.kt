package com.mussrose.issueflower.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.viewpager2.widget.ViewPager2
import com.mussrose.issueflower.databinding.ItemIssueBinding
import com.mussrose.issueflower.databinding.ItemIssueUserBinding
import com.mussrose.issueflower.entities.Issue


class AdapterIssuesByUser(
    val context: Context,
) : RecyclerView.Adapter<AdapterIssuesByUser.ViewHolder>() {




    private val diffUtil = object : DiffUtil.ItemCallback<Issue>() {
        override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private var onIssueClicked:((Issue)->Unit)?=null

    var onDeleteIssueClicked:((Issue)->Unit)?=null

    var onUpdateClicked:((Issue)->Unit)?=null

    fun setOnIssueClickedListener(listener:(Issue)->Unit){
        onIssueClicked=listener
    }

    fun setOnDeleteIssueClickListener(listener:(Issue)->Unit){
        onDeleteIssueClicked=listener
    }
    fun setOnUpdateIssueClickListener(listener:(Issue)->Unit){
        onUpdateClicked=listener
    }

    private val listDiffer = AsyncListDiffer(this, diffUtil)

    var issues: List<Issue>
        get() = listDiffer.currentList
        set(value) = listDiffer.submitList(value)

    class ViewHolder(
        val binding: ItemIssueUserBinding,
        val context: Context,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(issue: Issue) {

            val adapterLabel = AdapterLabel(issue.labels)
            adapterLabel.labels = issue.labels
            binding.rvItemTagsUser.adapter = adapterLabel
            binding.rvItemTagsUser.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.tvIssueTitle.text = issue.title


        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = issues[position]
        holder.bind(issue)
        holder.binding.itemUserIssueDelete.setOnClickListener {
            onDeleteIssueClicked?.let { click->
                click(issue)
            }
        }
        holder.itemView.setOnClickListener {
            onIssueClicked?.let { click->
                click(issue)
            }
        }
        holder.binding.itemUserIssueEdit.setOnClickListener {
            onUpdateClicked?.let { click->
                click(issue)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIssueUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding, context)
    }

    override fun getItemCount(): Int = issues.size


}


