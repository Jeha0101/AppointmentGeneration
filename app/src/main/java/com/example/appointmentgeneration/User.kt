package com.example.appointmentgeneration

class User(val nickname: String, val id: String, val pw: String) {
    fun getNickname(): String {
        return nickname
    }
}