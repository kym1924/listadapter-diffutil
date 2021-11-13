package com.kimym.puzzle.presentation.puzzle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kimym.puzzle.BR
import com.kimym.puzzle.R
import com.kimym.puzzle.databinding.ItemPuzzleBinding
import com.kimym.puzzle.util.BaseDiffUtilItemCallback

class PuzzleAdapter(
    private val move: (position: Int, max: Int) -> Unit,
) : ListAdapter<Int, PuzzleAdapter.PuzzleViewHolder>(
    BaseDiffUtilItemCallback(
        itemsTheSame = { oldItem, newItem -> oldItem == newItem },
        contentsTheSame = { oldItem, newItem -> oldItem == newItem }
    )
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuzzleViewHolder {
        return PuzzleViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: PuzzleViewHolder, position: Int) {
        holder.bind(getItem(position), currentList.size, move)
    }

    class PuzzleViewHolder(private val binding: ItemPuzzleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(puzzle: Int, max: Int, move: (position: Int, max: Int) -> Unit) {
            with(binding) {
                setVariable(BR.puzzle, puzzle)
                setVariable(BR.max, max)
                executePendingBindings()
                root.setOnClickListener {
                    move(adapterPosition, max)
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): PuzzleViewHolder {
                val binding = DataBindingUtil.inflate<ItemPuzzleBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_puzzle,
                    parent,
                    false
                )
                return PuzzleViewHolder(binding)
            }
        }
    }
}
