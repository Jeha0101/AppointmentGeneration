package com.example.appointmentgeneration.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

    private val _mood = MutableLiveData<String>()
    val mood: LiveData<String> get() = _mood

    fun setDate(date: String) {
        _date.value = date
    }

    fun setTime(time: String) {
        _time.value = time
    }

    fun setMood(mood: String) {
        _mood.value = mood
    }

    fun getSchedule(): Map<String, Any> {
        return mapOf(
            "date" to (_date.value ?: ""),
            "time" to (_time.value ?: ""),
            "mood" to (_mood.value ?: "없음"),
        )
    }
}