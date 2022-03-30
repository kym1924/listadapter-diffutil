package com.kimym.puzzle.presentation.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kimym.puzzle.R
import com.kimym.puzzle.databinding.FragmentStartBinding
import com.kimym.puzzle.util.navigate

class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setStartClickListener()
    }

    private fun setStartClickListener() {
        binding.btnStart.setOnClickListener {
            navigate(R.id.action_startFragment_to_sizeBottomSheet)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
