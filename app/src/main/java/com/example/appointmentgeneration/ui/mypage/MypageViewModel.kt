package com.example.appointmentgeneration.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MypageViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is MyPage Fragment"
    }
    val text: LiveData<String> = _text
}