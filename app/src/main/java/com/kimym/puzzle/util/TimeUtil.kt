package com.kimym.puzzle.util

fun timeToString(time: Long): String {
    val seconds = (time / 1000) % 60
    val minutes = (time / 1000) / 60 % 60
    val hours = (time / 1000) / (60 * 60)
    if (hours >= 1) {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    return String.format("%02d:%02d", minutes, seconds)
}
