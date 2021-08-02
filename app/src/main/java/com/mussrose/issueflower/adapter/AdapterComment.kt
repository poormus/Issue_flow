package com.mussrose.issueflower.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.mussrose.issueflower.R
import com.mussrose.issueflower.databinding.ItemCommentBinding
import com.mussrose.issueflower.entities.Comment
import io.noties.markwon.Markwon
import javax.inject.Inject


class AdapterComment @Inject constructor(
    private val glide: RequestManager, private val context: Context
) :
    RecyclerView.Adapter<AdapterComment.ViewHolder>() {


    private var onCommentDelete: ((Comment) -> Unit)? = null

    fun setOnCommentDeleteListener(listener: (Comment) -> Unit) {
        onCommentDelete = listener
    }

    private var onLikeClicked:((Comment,Int)->Unit)?=null

    fun setOnLikeClickedListener(listener: (Comment,Int)->Unit){
        onLikeClicked=listener
    }

    class ViewHolder(
        val binding: ItemCommentBinding,
        val glide: RequestManager,
        val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            glide.load(comment.profilePictureUrl).into(binding.ivUserPic)

            //binding.tvCommentAdded.text = comment.comment
            setMArkDownText(comment.comment)
            binding.ibDeleteComment.isVisible = comment.uid == FirebaseAuth.getInstance().uid!!
            binding.tvCommentedBy.text = comment.userName + " - ${comment.commentDate}"
            val likeCount = comment.totalLikes.size.toString()
            binding.tvTotalLike.text = "   $likeCount"

            binding.ivTotalLike.setImageResource(
                if(comment.isLiked){
                    R.drawable.ic_baseline_thumb_up_24
                }else{
                    R.drawable.ic_outline_thumb
                }
            )

        }
        private fun setMArkDownText(text:String){
            val markWon= Markwon.create(context)
            val markDown=markWon.toMarkdown(text)
            markWon.setParsedMarkdown(binding.tvCommentAdded,markDown)
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()

        }


    }

    private val listDiffer = AsyncListDiffer(this, diffUtil)
    var comments: List<Comment>
        get() = listDiffer.currentList
        set(value) = listDiffer.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, glide,context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
        holder.binding.ibDeleteComment.setOnClickListener {
            onCommentDelete?.let { click ->
                click(comment)
            }
        }
        holder.binding.ivTotalLike.setOnClickListener {
            onLikeClicked?.let { click->
                if(!comment.isLiking) click(comment,holder.layoutPosition)
            }
        }

    }

    override fun getItemCount(): Int = comments.size



}