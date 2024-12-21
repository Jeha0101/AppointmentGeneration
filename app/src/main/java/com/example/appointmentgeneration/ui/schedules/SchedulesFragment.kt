package com.example.appointmentgeneration.ui.schedules

import DividerItemDecoration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appointmentgeneration.databinding.FragmentSchedulesBinding
import com.google.firebase.firestore.FirebaseFirestore

class SchedulesFragment : Fragment() {

    private var _binding: FragmentSchedulesBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val schedulesList = mutableListOf<ScheduleItem>()
    private lateinit var adapter: SchedulesAdapter

    // 일정 데이터 클래스
    data class ScheduleItem(
        val time: String = "",
        val description: String = ""
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadAllSchedules() // 페이지 진입 시 모든 일정 불러오기
        setupCalendarView()
    }

    private fun setupRecyclerView() {
        adapter = SchedulesAdapter(schedulesList)
        binding.recyclerViewSchedules.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSchedules.adapter = adapter

        // Divider 추가
        binding.recyclerViewSchedules.addItemDecoration(
            DividerItemDecoration(requireContext(), height = 2) // 2px 높이의 Divider 추가
        )
    }


    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateString = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            loadSchedulesForDate(dateString) // 선택한 날짜만 보기
        }
    }

    private fun loadAllSchedules() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            Log.d("Stemp", ": LoadAll 1")
            db.collection("users")
                .whereEqualTo("id", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        for (document in documents) {
                            val userDocId = document.id
                            db.collection("users")
                                .document(userDocId)
                                .collection("schedules")
                                .get()
                                .addOnSuccessListener { schedules ->
                                    schedulesList.clear()
                                    for (schedule in schedules) {
                                        val date = schedule.getString("date") ?: ""
                                        val time = schedule.getString("time") ?: ""
                                        val description = schedule.getString("response") ?: ""

                                        Log.d("LoadAllSchedules", "date: $date, time: $time, response: $description")

                                        // 날짜별 구분선 추가
                                        val headerItem = ScheduleItem(date, "날짜 구분선")
                                        if (!schedulesList.contains(headerItem)) {
                                            schedulesList.add(headerItem)
                                        }

                                        // 일정 추가
                                        schedulesList.add(ScheduleItem(time, description))
                                    }

                                    // 어댑터 업데이트
                                    adapter.notifyDataSetChanged()
                                    Log.d("AdapterData", "Items count: ${adapter.itemCount}")

                                    if (schedulesList.isEmpty()) {
                                        Toast.makeText(context, "저장된 일정이 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "일정 로드 실패", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "사용자 조회 실패", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadSchedulesForDate(dateString: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val userId = sharedPreferences.getString("userId", null)
        Log.d("Stemp","userId: $userId")
        Log.d("Stemp",": 2")

        if (userId != null) {
            Log.d("Stemp",": 3")
            db.collection("users")
                .whereEqualTo("id", userId)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        Log.d("Stemp",": 4")
                        for (document in documents) {
                            val userDocId = document.id
                            db.collection("users")
                                .document(userDocId)
                                .collection("schedules")
                                .get()
                                .addOnSuccessListener { schedules ->
                                    schedulesList.clear()
                                    for (schedule in schedules) {
                                        val date = schedule.getString("date") ?: ""
                                        val time = schedule.getString("time") ?: ""
                                        val description = schedule.getString("response") ?: ""
                                        Log.d("Stemp",": 5")
                                        Log.d("FirestoreData", "date: $date, time: $time, response: $description")

                                        // 날짜별 구분을 위한 데이터 추가
                                        val headerItem = ScheduleItem(date, "날짜 구분선") // 날짜 표시용
                                        if (!schedulesList.contains(headerItem)) {
                                            Log.d("Stemp",": 6")
                                            schedulesList.add(headerItem) // 새로운 날짜인 경우 구분선 추가
                                        }

                                        // 일정 추가
                                        schedulesList.add(ScheduleItem(time, description))
                                    }

                                    adapter.notifyDataSetChanged()
                                    Log.d("AdapterData", "Items count: ${adapter.itemCount}")

                                    if (schedulesList.isEmpty()) {
                                        Log.d("Stemp",": 7")
                                        Toast.makeText(context, "저장된 일정이 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Log.d("Stemp",": 8")
                                    Toast.makeText(context, "일정 로드 실패", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
                .addOnFailureListener {
                    Log.d("Stemp",": 9")
                    Toast.makeText(context, "사용자 조회 실패", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("Stemp",": 10")
            Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
