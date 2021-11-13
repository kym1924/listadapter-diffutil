package com.kimym.puzzle.presentation.size

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kimym.puzzle.databinding.LayoutSizeBottomSheetBinding
import com.kimym.puzzle.util.navigate

class SizeBottomSheet : BottomSheetDialogFragment() {
    private var _binding: LayoutSizeBottomSheetBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LayoutSizeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        with(binding.rvSize) {
            adapter = SizeAdapter { size -> sizeSelect(size) }
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
        }
    }

    private fun sizeSelect(size: Int) {
        dismiss()
        navigate(SizeBottomSheetDirections.actionSizeBottomSheetToPuzzleFragment(size))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
