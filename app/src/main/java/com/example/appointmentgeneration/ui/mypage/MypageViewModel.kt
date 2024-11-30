package com.example.appointmentgeneration.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class UserData(val name: String)

class MypageViewModel : ViewModel() {

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> get() = _userData

    init {
        // 초기 데이터 로드
        _userData.value = UserData(name = "홍길동")
    }

    fun refreshUserData() {
        // 데이터를 갱신하는 로직
        _userData.value = UserData(name = "업데이트된 이름") // 예시: 데이터 갱신
    }

    fun logout() {
        // 로그아웃 처리
    }
}
