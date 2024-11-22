package com.example.appointmentgeneration.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appointmentgeneration.R

class MypageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        // 프로필 이미지 및 이름 설정
        val profileImage: ImageView = view.findViewById(R.id.image_profile)
        val username: TextView = view.findViewById(R.id.text_username)
        username.text = "홍길동" // 예: 사용자 이름 (실제로는 동적 데이터 사용)

        // 내 약속 보기 버튼
        val manageAppointmentsButton: Button = view.findViewById(R.id.button_manage_appointments)
        manageAppointmentsButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_mypage_to_navigation_schedules)
        }

        // 로그아웃 버튼
        val logoutButton: Button = view.findViewById(R.id.button_logout)
        logoutButton.setOnClickListener {
            Toast.makeText(context, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}

