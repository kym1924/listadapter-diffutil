package com.kimym.puzzle.presentation.size

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kimym.puzzle.BR
import com.kimym.puzzle.R
import com.kimym.puzzle.databinding.ItemSizeBinding
import com.kimym.puzzle.util.BaseDiffUtilItemCallback

class SizeAdapter(
    private val sizeSelect: (size: Int) -> Unit,
) : ListAdapter<Int, SizeAdapter.SizeViewHolder>(
    BaseDiffUtilItemCallback(
        itemsTheSame = { oldItem, newItem -> oldItem == newItem },
        contentsTheSame = { oldItem, newItem -> oldItem == newItem })
) {
    init {
        submitList(listOf(3, 4, 5))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        return SizeViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bind(getItem(position), sizeSelect)
    }

    class SizeViewHolder(private val binding: ItemSizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(size: Int, sizeSelect: (size: Int) -> Unit) {
            with(binding) {
                setVariable(BR.size, size)
                executePendingBindings()
                root.setOnClickListener {
                    sizeSelect(size)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): SizeViewHolder {
                val binding = DataBindingUtil.inflate<ItemSizeBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_size,
                    parent,
                    false
                )
                return SizeViewHolder(binding)
            }
        }
    }
}
