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
import com.mussrose.issueflower.entities.Issue


class AdapterIssuesForProject(
    val context: Context,
    private val listener:OnTagClicked
) : RecyclerView.Adapter<AdapterIssuesForProject.ViewHolder>() {



    private var onIssueClicked:((Issue)->Unit)?=null
    fun setOnIssueClickedListener(listener:(Issue)->Unit){
        onIssueClicked=listener
    }

    private var onUpVoteClicked:((Issue,Int)->Unit)?=null
    fun setOnUpVoteClickListener(listener: (Issue, Int) -> Unit){
        onUpVoteClicked=listener
    }


    private var onMoreClicked:((String,View)->Unit)?=null

    fun setOnMoreClickedListener(listener:(String,View)->Unit){
        onMoreClicked=listener
    }
    interface OnTagClicked{
        fun setOnTagClickForSearchListener(query:String)
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
        val binding: ItemIssueBinding,
        val context: Context,
        val listener: OnTagClicked
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(issue: Issue) {
            val adapterLabel = AdapterLabel(issue.labels)
            adapterLabel.labels = issue.labels

            adapterLabel.setOnTagClickListener { tag->
                listener.setOnTagClickForSearchListener(tag)
            }
            binding.rvItemTags.adapter = adapterLabel
            binding.rvItemTags.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.tvIssueTitle.text = issue.title
            binding.tvItemUser.text = issue.openedBy
            binding.tvTotalCommentNumber.text=issue.totalCommentNumber.size.toString()
            binding.tvTotalUpVote.text=issue.totalUpVotes.size.toString()
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
        holder.binding.ibUpvote.setOnClickListener {
            onUpVoteClicked?.let { click->
                if(!issue.isUpVoting) click(issue,holder.adapterPosition)
            }
        }
        holder.binding.ibProjectIssueMore.setOnClickListener { view->
            onMoreClicked?.let { click->
                click(issue.id,view)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIssueBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding, context,listener)
    }

    override fun getItemCount(): Int =issues.size


}


