package com.example.appointmentgeneration.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// 사용자 데이터 모델
data class UserData(
    val name: String,
    val profileImage: String? = null
)

class MypageViewModel : ViewModel() {
    // 사용자 데이터를 관리하는 LiveData
    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData

    // 로딩 상태를 관리하는 LiveData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // 초기 사용자 데이터 로드
        loadUserData()
    }

    // 사용자 데이터를 로드하는 함수
    private fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: API나 로컬 DB에서 사용자 데이터를 가져오는 로직 구현
                _userData.value = UserData(name = "홍길동")
            } catch (e: Exception) {
                // 에러 처리
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 사용자 데이터를 새로고침하는 함수
    fun refreshUserData() {
        loadUserData()
    }
}