package com.example.appointmentgeneration.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appointmentgeneration.ScheduleData

class HomeViewModel : ViewModel() {
    private val scheduleData = ScheduleData()

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    private val _time = MutableLiveData<String>()
    val time: LiveData<String> get() = _time

    private val _mood = MutableLiveData<String>()
    val mood: LiveData<String> get() = _mood

    private val _price = MutableLiveData<String>()
    val price: LiveData<String> get() = _price

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

    fun setBudget(budget: Int) {
        scheduleData.budget = budget
    }

    fun setDate(date: String) {
        _date.value = date
    }

    fun setTime(time: String) {
        _time.value = time
    }

    fun setMood(mood: String) {
        _mood.value = mood
        updateScheduleData()
    }

    fun setPrice(price: String) {
        _price.value = price
        updateScheduleData()
    }

    fun updateScheduleData() {
        scheduleData.apply {
            scheduleDate = _date.value
            scheduleTime = _time.value
            moods.clear()
            _mood.value?.split(", ")?.let { moods.addAll(it) }
            budget = _price.value?.filter { it.isDigit() }?.toIntOrNull()
        }
    }

    fun getScheduleData(): ScheduleData {
        updateScheduleData()
        return scheduleData
    }
}
