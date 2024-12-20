package com.example.appointmentgeneration.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appointmentgeneration.ScheduleData

class HomeViewModel : ViewModel() {
    private val scheduleData = ScheduleData()

    fun setMyLocation(location: String) {
        scheduleData.myLocation = location
    }

    fun addFriendLocation(location: String) {
        if (scheduleData.friendLocations.size < 3) {
            scheduleData.friendLocations.add(location)
        }
    }

    fun addDestination(destination: String) {
        if (scheduleData.desiredDestinations.size < 10) {
            scheduleData.desiredDestinations.add(destination)
        }
    }

    fun setDate(date: String) {
        scheduleData.scheduleDate = date
    }

    fun setTime(time: String) {
        scheduleData.scheduleTime = time
    }

    fun setBudget(budget: Int) {
        scheduleData.budget = budget
    }

    fun addMood(mood: String) {
        scheduleData.moods.add(mood)
    }

    fun getScheduleData(): ScheduleData {
        return scheduleData
    }

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

    private val _mood = MutableLiveData<String>()
    val mood: LiveData<String> get() = _mood

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