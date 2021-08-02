package com.mussrose.issueflower.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.viewpager2.widget.ViewPager2
import com.mussrose.issueflower.databinding.ItemIssueBinding
import com.mussrose.issueflower.databinding.ItemIssueBookmarkBinding
import com.mussrose.issueflower.entities.Issue


class AdapterBookMarkedIssues(
    val context: Context,

) : RecyclerView.Adapter<AdapterBookMarkedIssues.ViewHolder>() {




    private var onIssueClicked:((Issue)->Unit)?=null

    fun setOnIssueClickedListener(listener:(Issue)->Unit){
        onIssueClicked=listener
    }

    private var onBookMarkClicked:((Issue)->Unit)?=null

    fun setOnBookMarkClickListener(listener: (Issue) -> Unit){
        onBookMarkClicked=listener
    }


    private val diffUtil = object : DiffUtil.ItemCallback<Issue>() {
        override fun areItemsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Issue, newItem: Issue): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val listDiffer = AsyncListDiffer(this, diffUtil)

    var issues: List<Issue>
        get() = listDiffer.currentList
        set(value) = listDiffer.submitList(value)



    class ViewHolder(
        val binding: ItemIssueBookmarkBinding,
        val context: Context,

    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(issue: Issue) {
            val adapterLabel = AdapterLabel(issue.labels)
            adapterLabel.labels = issue.labels
            binding.rvItemTags.adapter = adapterLabel
            binding.rvItemTags.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.tvIssueTitle.text = issue.title
            binding.tvTotalCommentNumber.text=issue.totalCommentNumber.size.toString()

        }


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val issue = issues[position]
        holder.bind(issue)
        holder.itemView.setOnClickListener {
            onIssueClicked?.let { click->
                click(issue)
            }
        }
        holder.binding.ibProjectIssueBookmark.setOnClickListener {
            onBookMarkClicked?.let { click->
                click(issue)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIssueBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding, context)
    }

    override fun getItemCount(): Int =issues.size


}


