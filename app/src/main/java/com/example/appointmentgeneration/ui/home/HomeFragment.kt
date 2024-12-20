package com.example.appointmentgeneration.ui.home


import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.databinding.FragmentHomeBinding
import com.example.appointmentgeneration.ui.address.AddressSearchActivity
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.fragment.findNavController
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val firestore = FirebaseFirestore.getInstance()

    private val addressSearchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val address = result.data?.getStringExtra("address")
            binding.editMyLocation.setText(address)
        }
    }

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
        setupListeners()
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

    private fun setupListeners() {
        with(binding) {
            // 주소 검색
            editMyLocation.setOnClickListener {
                addressSearchLauncher.launch(Intent(requireContext(), AddressSearchActivity::class.java))
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
            }

            // 친구 추가
            btnAddFriend.setOnClickListener {
                Toast.makeText(requireContext(), "친구 추가 기능은 구현 중입니다.", Toast.LENGTH_SHORT).show()
            }

            // 목적지 추가
            btnAddDestination.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_destinationSelectionFragment)
            }

            // 일정 생성
            btnCreateSchedule.setOnClickListener {
                saveScheduleToDatabase()
                // 네비게이션 동작 추가
                findNavController().navigate(R.id.action_navigation_home_to_scheduleGenerationFragment)
            }
        }
    }


}