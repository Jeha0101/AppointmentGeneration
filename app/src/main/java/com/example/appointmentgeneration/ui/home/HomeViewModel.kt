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

    // 가격 정보를 위한 LiveData 추가
    private val _price = MutableLiveData<String>()
    val price: LiveData<String> get() = _price
    // 날짜 설정 함수
    fun setDate(date: String) {
        _date.value = date
    }
    // 시간 설정 함수
    fun setTime(time: String) {
        _time.value = time
    }
    // 분위기 설정 함수
    fun setMood(mood: String) {
        _mood.value = mood
    }

    // 가격 설정 함수 추가
    fun setPrice(price: String) {
        _price.value = price
    }

    // 일정 정보를 Map으로 반환하는 함수
    fun getSchedule(): Map<String, Any> {
        return mapOf(
            "date" to (_date.value ?: ""),
            "time" to (_time.value ?: ""),
            "mood" to (_mood.value ?: "없음"),
            "price" to (_price.value ?: "상관없음")  // 가격 정보 추가
        )
    }
}