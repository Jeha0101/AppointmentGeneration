package com.example.appointmentgeneration.ui.schedules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.databinding.FragmentSchedulesBinding

// 일정 관리를 위한 Fragment 클래스
class SchedulesFragment : Fragment() {
    private var _binding: FragmentSchedulesBinding? = null
    // binding 객체 지연 초기화
    private val binding get() = _binding!!
    // Firestore 인스턴스 초기화
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 일정 데이터를 담을 데이터 클래스 정의
    data class ScheduleItem(
        val time: String = "",         // 일정 시간
        val place: String = "",        // 일정 장소
        val description: String = ""    // 일정 설명
    )

    // Fragment View 생성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchedulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    // View가 생성된 후 호출되는 라이프사이클 메서드
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCalendarView() // 달력 뷰 설정
    }

    // 달력 뷰 설정 및 날짜 선택 리스너 설정
    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 선택된 날짜를 "YYYY-MM-DD" 형식의 문자열로 변환
            val dateString = String.format("%d-%d-%d", year, month + 1, dayOfMonth)
            loadSchedulesForDate(dateString) // 해당 날짜의 일정 로드
        }
    }

    // 선택된 날짜의 일정을 파이어스토어에서 불러오는 함수
    private fun loadSchedulesForDate(dateString: String) {
        // SharedPreferences에서 현재 로그인한 사용자의 ID 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val userId = sharedPreferences.getString("userId", null)

        if (userId != null) {
            // 파이어스토어에서 해당 사용자의 선택된 날짜 일정 조회
            db.collection("schedules")
                .whereEqualTo("user_id", userId)    // 현재 사용자의 일정만 필터링
                .whereEqualTo("date", dateString)   // 선택된 날짜의 일정만 필터링
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // 일정이 존재하는 경우 텍스트 구성
                        val schedulesText = buildString {
                            appendLine("$dateString 의 일정:")
                            appendLine()
                            documents.forEachIndexed { index, document ->
                                appendLine("${index + 1}. 시간: ${document.getString("time")}")
                                appendLine("   장소: ${document.getString("place")}")
                                appendLine()
                            }
                        }
                        binding.textSchedules.text = schedulesText
                    } else {
                        // 일정이 없는 경우 메시지 표시
                        binding.textSchedules.text = buildString {
                            appendLine("$dateString")
                            appendLine("저장된 일정이 없습니다.")
                        }
                    }
                    binding.textSchedules.visibility = View.VISIBLE
                }
                .addOnFailureListener { exception ->
                    // 데이터 로드 실패 시 에러 메시지 표시
                    Toast.makeText(context, "일정을 불러오는데 실패했습니다: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // 로그인되지 않은 경우 알림 메시지 표시
            Toast.makeText(context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // Fragment 파괴 시 바인딩 객체 정리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}