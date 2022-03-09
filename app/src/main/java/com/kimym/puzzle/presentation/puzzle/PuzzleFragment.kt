package com.kimym.puzzle.presentation.puzzle

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.kimym.puzzle.R
import com.kimym.puzzle.databinding.FragmentPuzzleBinding
import com.kimym.puzzle.util.navigate
import com.kimym.puzzle.util.popBackStack
import com.kimym.puzzle.util.repeatOnLifecycle
import kotlinx.coroutines.flow.collect

class PuzzleFragment : Fragment() {
    private var _binding: FragmentPuzzleBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel by viewModels<PuzzleViewModel>()
    private val args by navArgs<PuzzleFragmentArgs>()
    private val puzzleAdapter by lazy {
        PuzzleAdapter { position, max -> viewModel.move(position, max) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPuzzleBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentResultListener()
        initRvPuzzle()
        setBackClickListener()
        setCollect()
        viewModel.setPuzzle(args.size)
    }

    private fun initFragmentResultListener() {
        setFragmentResultListener(getString(R.string.again)) { _, bundle ->
            if (bundle.getBoolean(getString(R.string.again_bundle_key))) {
                viewModel.againGame()
                viewModel.setPause()
            }
        }
    }

    private fun initRvPuzzle() {
        with(binding.rvPuzzle) {
            adapter = puzzleAdapter
            layoutManager = GridLayoutManager(requireContext(), args.size)
        }
    }

    private fun setBackClickListener() {
        binding.btnPuzzleBack.setOnClickListener {
            popBackStack()
        }
    }

    private fun setCollect() {
        repeatOnLifecycle {
            viewModel.puzzle.collect { puzzle ->
                puzzleAdapter.submitList(puzzle) {
                    viewModel.clearCheck()
                }
            }
        }

        repeatOnLifecycle {
            viewModel.isClear.collect { clear ->
                when (clear) {
                    true -> {
                        val clearTime = binding.tvPuzzleTime.text.toString()
                        val moveCount = viewModel.getMoveCount()
                        navigate(PuzzleFragmentDirections.clearPuzzle(clearTime, moveCount))
                    }
                }
            }
        }

        repeatOnLifecycle {
            viewModel.isPause.collect { isPause ->
                isPause?.let {
                    val duration = resources.getInteger(android.R.integer.config_mediumAnimTime)
                    startRvPuzzleAnimation(isPause, duration)
                    startImagePauseAnimation(duration)
                }
            }
        }
    }

    private fun startRvPuzzleAnimation(isPause: Boolean, duration: Int) {
        binding.rvPuzzle.apply {
            alpha = getAlphaForIsPause(isPause)
            animate()
                .alpha(getAlphaForIsPause(!isPause))
                .setDuration(duration.toLong())
                .setListener(null)
        }
    }

    private fun startImagePauseAnimation(duration: Int) {
        val alpha = binding.rvPuzzle.alpha
        with(binding.imgPuzzlePause) {
            animate()
                .alpha(if (alpha == 1f) 1f else 0f)
                .setDuration(duration.toLong())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        isVisible = (alpha == 1f)
                    }
                })
        }
    }

    private fun getAlphaForIsPause(isPause: Boolean): Float {
        return if (isPause) 1f else 0.2f
    }

    override fun onStop() {
        super.onStop()
        viewModel.isBackground()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
