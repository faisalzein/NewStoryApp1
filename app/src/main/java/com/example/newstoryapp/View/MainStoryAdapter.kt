package com.example.newstoryapp.View

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newstoryapp.Response.ListStoryItem
import com.example.newstoryapp.databinding.ItemRowStoryBinding

class MainStoryAdapter : PagingDataAdapter<ListStoryItem, MainStoryAdapter.ViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallback: ((ListStoryItem) -> Unit)? = null

    fun setOnItemClickCallback(listener: (ListStoryItem) -> Unit) {
        onItemClickCallback = listener
    }

    class ViewHolder(private val binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem?) {
            item?.let {
                Log.d("MainStoryAdapter", "Binding item: ${it.id}")
                Glide.with(itemView.context)
                    .load(it.photoUrl)
                    .into(binding.imgItemPhoto)
                binding.tvItemName.text = it.name
                binding.tvItemDescription.text = it.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            item?.let { onItemClickCallback?.invoke(it) }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
