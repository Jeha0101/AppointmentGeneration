package com.example.appointmentgeneration.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appointmentgeneration.databinding.ScheduleCreationFragmentBinding

class ScheduleCreationFragment : Fragment() {
    private var _binding: ScheduleCreationFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScheduleCreationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSearchLocations.setOnClickListener {
                // 장소 검색 기능 구현
                Toast.makeText(context, "장소 검색 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            btnAddFriend.setOnClickListener {
                // 친구 추가 기능 구현
                Toast.makeText(context, "친구 추가 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            btnCreateSchedule.setOnClickListener {
                createSchedule()
            }
        }
    }

    private fun createSchedule() {
        // 입력값 가져오기
        val date = binding.editDate.text.toString()
        val time = binding.editTime.text.toString()
        val place = binding.editPlace.text.toString()

        // 입력값 검증
        if (date.isEmpty() || time.isEmpty() || place.isEmpty()) {
            Toast.makeText(context, "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // 약속 생성 로직 구현
        Toast.makeText(context, "약속이 생성되었습니다", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}