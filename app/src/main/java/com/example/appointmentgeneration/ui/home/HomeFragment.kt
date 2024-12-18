package com.example.appointmentgeneration.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appointmentgeneration.BuildConfig
import com.example.appointmentgeneration.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val naver = BuildConfig.NAVER_MAP_KEY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        with(binding) {
            // 친구 위치 추가
            btnAddFriend.setOnClickListener {
                Toast.makeText(context, "친구 위치 추가 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            // 목적지 추가
            btnAddDestination.setOnClickListener {
                Toast.makeText(context, "목적지 추가 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            // 날짜 선택
            btnDate.setOnClickListener {
                Toast.makeText(context, "날짜 선택 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            // 시간 선택
            btnTime.setOnClickListener {
                Toast.makeText(context, "시간 선택 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            // 가격대 설정
            radioGroupPrice.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    radioNoPrice.id -> {
                        Toast.makeText(context, "가격대 무관", Toast.LENGTH_SHORT).show()
                    }
                    radioSetPrice.id -> {
                        Toast.makeText(context, "가격대 설정", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // 일정 생성
            btnCreateSchedule.setOnClickListener {
                Toast.makeText(context, "일정 생성 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}