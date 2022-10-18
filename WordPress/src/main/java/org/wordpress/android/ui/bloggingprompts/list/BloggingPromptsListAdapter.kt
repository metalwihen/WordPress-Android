package org.wordpress.android.ui.bloggingprompts.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.RecyclerView
import org.wordpress.android.R
import org.wordpress.android.databinding.BloggingPromptsListItemBinding
import org.wordpress.android.util.extensions.getQuantityString
import org.wordpress.android.util.extensions.setVisible

class BloggingPromptsListAdapter() : RecyclerView.Adapter<BloggingPromptsListItemViewHolder>() {
    private var items = listOf<BloggingPromptsListItem>()

    fun update(newItems: List<BloggingPromptsListItem>) {
        val diffResult = DiffUtil.calculateDiff(
                BloggingPromptsListDiffCallback(
                        items,
                        newItems
                )
        )
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BloggingPromptsListItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BloggingPromptsListItemBinding.inflate(layoutInflater, parent, false)
        return BloggingPromptsListItemViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BloggingPromptsListItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}

class BloggingPromptsListItemViewHolder(
    private val binding: BloggingPromptsListItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(prompt: BloggingPromptsListItem) {
        with(binding) {
            promptTitle.text = prompt.title
            promptSubtitleAnswerCount.text = root.getQuantityString(
                    prompt.respondentsCount,
                    R.string.blogging_prompts_list_item_count_answers_zero,
                    R.string.blogging_prompts_list_item_count_answers_one,
                    R.string.blogging_prompts_list_item_count_answers_many
            )
            promptSubtitleDate.text = prompt.dateLabel
            groupAnsweredLabel.setVisible(prompt.isAnswered)
        }
    }
}

internal class BloggingPromptsListDiffCallback(
    val old: List<BloggingPromptsListItem>,
    val new: List<BloggingPromptsListItem>
) : Callback() {
    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            old[oldItemPosition] == new[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            old[oldItemPosition] == new[newItemPosition]
}
