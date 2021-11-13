package com.kimym.puzzle.presentation.puzzle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.sqrt

class PuzzleViewModel : ViewModel() {
    private val _puzzle = MutableStateFlow<List<Int>>(listOf())
    val puzzle = _puzzle.asStateFlow()

    private val _isPause = MutableStateFlow(false)
    val isPause = _isPause.asStateFlow()

    private val _time = MutableStateFlow<Long>(0)
    val time = _time.asStateFlow()

    private val _movingCount = MutableStateFlow(0)
    val movingCount = _movingCount.asStateFlow()

    private val _isClear = MutableSharedFlow<Boolean>()
    val isClear = _isClear.asSharedFlow()

    private var job: Job? = getTime()

    fun againGame() {
        _time.value = 0
        _movingCount.value = 0
    }

    fun setPuzzle(size: Int) {
        _puzzle.value = initPuzzleList(size * size)
    }

    private fun initPuzzleList(max: Int): MutableList<Int> {
        return mutableListOf<Int>().apply {
            for (i in 1..max) {
                add(i)
            }
            shuffle()
        }
    }

    fun move(position: Int, max: Int) {
        val sqrt = sqrt(max.toDouble()).toInt()
        when (val maxIndex = _puzzle.value.indexOf(max)) {
            position + 1 -> if ((position + 1) % sqrt != 0) {
                swap(position, maxIndex)
            }
            position - 1 -> if (position % sqrt != 0) {
                swap(position, maxIndex)
            }
            position + sqrt, position - sqrt -> {
                swap(position, maxIndex)
            }
        }
    }

    private fun swap(position: Int, maxIndex: Int) {
        _movingCount.value += 1
        _puzzle.value = _puzzle.value.toList().apply {
            Collections.swap(this, position, maxIndex)
        }
    }

    fun clearCheck() {
        var isClear = true
        for (i in _puzzle.value.indices) {
            if (_puzzle.value[i] != i + 1) {
                isClear = false
                break
            }
        }
        viewModelScope.launch {
            _isClear.emit(isClear)
        }
    }

    fun setPause() {
        _isPause.value = !(_isPause.value)
        when (_isPause.value) {
            true -> job?.cancel()
            false -> job = getTime()
        }
    }

    fun isBackground() {
        when (_isPause.value) {
            false -> {
                job?.cancel()
                _isPause.value = true
            }
        }
    }

    private fun getTime() = viewModelScope.launch {
        while (true) {
            delay(1000)
            _time.value += 1000
        }
    }

    fun getMoveCount(): Int {
        return _movingCount.value
    }
}
