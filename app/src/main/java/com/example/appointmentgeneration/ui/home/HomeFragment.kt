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
import android.widget.EditText
import android.widget.LinearLayout

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

        parentFragmentManager.setFragmentResultListener(
            "selectedTagsKey", this
        ) { _, bundle ->
            val selectedTags = bundle.getStringArrayList("selectedTags") ?: emptyList()

            // ScheduleData에 추가
            selectedTags.forEach {
                homeViewModel.addDestination(it) // ViewModel에 목적지 추가
            }

            Toast.makeText(requireContext(), "선택한 목적지 추가 완료!", Toast.LENGTH_SHORT).show()
        }

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
            // 내 위치 텍스트 입력 저장
            editMyLocation.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) { // 포커스가 벗어났을 때 동작
                    val location = editMyLocation.text.toString()
                    if (location.isNotEmpty()) {
                        homeViewModel.setMyLocation(location) // ViewModel에 저장
                        Toast.makeText(requireContext(), "내 위치가 저장되었습니다: $location", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "위치를 입력해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
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
            // 일정 생성 버튼 클릭 시 동작
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

                // 일정 데이터 객체 생성
                val scheduleData = homeViewModel.getScheduleData()

                // 기존 저장 로직 실행
                saveScheduleToDatabase()

                // 데이터 전달을 위한 Bundle 생성
                val bundle = Bundle().apply {
                    putParcelable("scheduleData", scheduleData)
                }

                // 페이지 이동 및 데이터 전달
                findNavController().navigate(
                    R.id.action_navigation_home_to_scheduleGenerationFragment,
                    bundle
                )
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
                        homeViewModel.setTime(selectedTime) // 시간 저장
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
                if (homeViewModel.getScheduleData().friendLocations.size >= 3) {
                    Toast.makeText(requireContext(), "최대 3명의 친구 위치만 추가 가능합니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 새로운 EditText 생성
                val editText = EditText(requireContext()).apply {
                    hint = "친구 위치 입력"
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 8) // 여백 추가
                    }
                }

                // 입력창을 컨테이너에 추가
                binding.containerFriendLocations.addView(editText)

                // 포커스 해제 시 ViewModel에 저장
                editText.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        val friendLocation = editText.text.toString()
                        if (friendLocation.isNotEmpty()) {
                            homeViewModel.addFriendLocation(friendLocation)
                            Toast.makeText(requireContext(), "친구 위치 저장: $friendLocation", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "친구 위치를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }


            // 목적지 추가
            btnAddDestination.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_destinationSelectionFragment)
            }

        }
    }


}