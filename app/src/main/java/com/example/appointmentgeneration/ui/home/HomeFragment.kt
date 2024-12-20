package com.example.appointmentgeneration.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.appointmentgeneration.ui.home.HomeViewModel
import java.util.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        with(binding) {
            // 나의 위치 입력
            editMyLocation.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val myLocation = editMyLocation.text.toString()
                    homeViewModel.setMyLocation(myLocation)
                }
            }

            // 친구 위치 입력
            btnAddFriend.setOnClickListener {
                val friendLocation = getFriendLocation() // 임시 데이터 반환
                homeViewModel.addFriendLocation(friendLocation)
                Toast.makeText(requireContext(), "친구 위치 추가: $friendLocation", Toast.LENGTH_SHORT).show()
            }

            // 희망 목적지 추가
            btnAddDestination.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_destinationSelectionFragment)
                val destination = getDesiredDestination() // 임시 데이터 반환
                homeViewModel.addDestination(destination)
                Toast.makeText(requireContext(), "목적지 추가: $destination", Toast.LENGTH_SHORT).show()
            }

            // 날짜 선택
            btnDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, day ->
                        val selectedDate = "$year-${month + 1}-$day"
                        homeViewModel.setDate(selectedDate)
                        Toast.makeText(requireContext(), "날짜 선택: $selectedDate", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()

                val selectedDate = selectDate() // 임시 데이터 반환
                homeViewModel.setDate(selectedDate)
                Toast.makeText(requireContext(), "날짜 선택: $selectedDate", Toast.LENGTH_SHORT).show()
            }

            // 시간 선택
            btnTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        val selectedTime = String.format("%02d:%02d", hour, minute)
                        homeViewModel.setTime(selectedTime)
                        Toast.makeText(requireContext(), "시간 선택: $selectedTime", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()

                val selectedTime = selectTime() // 임시 데이터 반환
                homeViewModel.setTime(selectedTime)
                Toast.makeText(requireContext(), "시간 선택: $selectedTime", Toast.LENGTH_SHORT).show()
            }

            // 예산 입력
            radioGroupPrice.setOnCheckedChangeListener { _, checkedId ->
                val budget = when (checkedId) {
                    R.id.radio_no_price -> null // 상관없음
                    R.id.radio_set_price -> getBudget() // 임시 데이터 반환
                    else -> null
                }
                if (budget != null) {
                    homeViewModel.setBudget(budget)
                    Toast.makeText(requireContext(), "예산 설정: $budget", Toast.LENGTH_SHORT).show()
                }
            }

            // 분위기 추가
            chipGroupMood.setOnCheckedChangeListener { group, checkedId ->
                val mood = when (checkedId) {
                    R.id.chip_romantic -> "로맨틱"
                    R.id.chip_casual -> "캐주얼"
                    R.id.chip_unique -> "독특한"
                    else -> null
                }
                if (mood != null) {
                    homeViewModel.addMood(mood)
                    Toast.makeText(requireContext(), "분위기 추가: $mood", Toast.LENGTH_SHORT).show()
                }
            }

            // 일정 생성
            btnCreateSchedule.setOnClickListener {
                saveScheduleToDatabase()
            }
        }
    }

    private fun saveScheduleToDatabase() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val userId = sharedPreferences.getString("userId", null)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val scheduleData = homeViewModel.getSchedule().toMutableMap()
        scheduleData["user_id"] = userId

        firestore.collection("schedules")
            .add(scheduleData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "일정이 성공적으로 저장되었습니다!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "일정 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getFriendLocation(): String {
        // 임시 함수: 나중에 사용자 입력을 받아 구현
        return "친구 위치 예시"
    }

    private fun getDesiredDestination(): String {
        // 임시 함수: 나중에 사용자 입력을 받아 구현
        return "숙대입구"
    }

    private fun selectDate(): String {
        // 임시 함수: 나중에 DatePickerDialog로 구현
        return "2024-12-25"
    }

    private fun selectTime(): String {
        // 임시 함수: 나중에 TimePickerDialog로 구현
        return "14:30"
    }
    private fun getBudget(): Int {
        // 임시 함수: 나중에 사용자 입력을 받아 구현
        return 30000
    }
}
