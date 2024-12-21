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
import com.google.android.material.chip.Chip


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

            // 가격대 라디오 버튼 리스너
            radioGroupPrice.setOnCheckedChangeListener { _, checkedId ->
                // 가격대 입력 레이아웃 표시/숨김 처리
                layoutPriceInput.visibility = when (checkedId) {
                    R.id.radio_set_price -> View.VISIBLE
                    else -> View.GONE
                }
            }
            // 일정 생성 버튼 클릭시 가격 정보도 포함하여 저장
            btnCreateSchedule.setOnClickListener {
                val priceInfo = when (radioGroupPrice.checkedRadioButtonId) {
                    R.id.radio_no_price -> "상관없음"
                    R.id.radio_set_price -> {
                        val minPrice = editPriceMin.text.toString()
                        val maxPrice = editPriceMax.text.toString()
                        if (minPrice.isNotEmpty() && maxPrice.isNotEmpty()) {
                            "$minPrice ~ $maxPrice 원"
                        } else {
                            Toast.makeText(requireContext(), "가격을 입력해주세요", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                    else -> "상관없음"
                }

                // ViewModel에 가격 정보 저장
                homeViewModel.setPrice(priceInfo)

                // 기존 저장 로직 실행
                saveScheduleToDatabase()
                findNavController().navigate(R.id.action_navigation_home_to_scheduleGenerationFragment)
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

            // 분위기 칩 선택 리스너 추가
            chipGroupMood.setOnCheckedChangeListener { group, _ ->
                // 선택된 모든 칩의 텍스트를 리스트로 모음
                val selectedMoods = mutableListOf<String>()

                // 선택된 각 Chip의 ID를 확인하고 해당하는 텍스트 수집
                for (chipId in group.checkedChipIds) {
                    val chip = group.findViewById<Chip>(chipId)
                    selectedMoods.add(chip.text.toString())
                }

                // 선택된 분위기가 있는 경우에만 ViewModel에 저장
                if (selectedMoods.isNotEmpty()) {
                    homeViewModel.setMood(selectedMoods.joinToString(", "))
                } else {
                    homeViewModel.setMood("없음")
                }
            }


            // 친구 추가
            btnAddFriend.setOnClickListener {
                Toast.makeText(requireContext(), "친구 추가 기능은 구현 중입니다.", Toast.LENGTH_SHORT).show()
            }

            // 목적지 추가
            btnAddDestination.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_destinationSelectionFragment)
            }

        }
    }


}