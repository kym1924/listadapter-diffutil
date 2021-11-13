package com.kimym.puzzle.presentation.clear

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.kimym.puzzle.R
import com.kimym.puzzle.databinding.FragmentClearBinding
import com.kimym.puzzle.util.navigate
import com.kimym.puzzle.util.popBackStack

class ClearFragment : Fragment() {
    private var _binding: FragmentClearBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val args by navArgs<ClearFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentClearBinding.inflate(inflater, container, false)
        binding.clearTime = args.clearTime
        binding.moveCount = args.clearMoveCount
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.btnAgain.setOnClickListener {
            setFragmentResult(getString(R.string.again), bundleOf(getString(R.string.again_bundle_key) to true))
            popBackStack()
        }

        binding.btnNewGame.setOnClickListener {
            navigate(R.id.action_clearFragment_to_startFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
