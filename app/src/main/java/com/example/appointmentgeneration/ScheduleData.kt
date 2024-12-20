package com.example.appointmentgeneration

data class ScheduleData (
    var myLocation: String = "", // 내 위치
    var friendLocations: MutableList<String> = mutableListOf(), // 친구 위치 (최대 3명)
    var desiredDestinations: MutableList<String> = mutableListOf(), // 희망 목적지 (최대 10개)
    var scheduleDate: String? = null, // 약속 날짜
    var scheduleTime: String? = null, // 약속 시간
    var budget: Int? = null, // 예산
    var moods: MutableList<String> = mutableListOf()
)