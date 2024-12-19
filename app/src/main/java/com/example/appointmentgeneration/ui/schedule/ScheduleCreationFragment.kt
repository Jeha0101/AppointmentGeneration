package com.example.appointmentgeneration.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appointmentgeneration.databinding.ScheduleCreationFragmentBinding
import java.util.*

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
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                updateSelectedDate(selectedDate)
            }

            btnSearchLocations.setOnClickListener {
                searchNearbyLocations()
            }

            btnAddFriend.setOnClickListener {
                addFriend()
            }

            btnCreateSchedule.setOnClickListener {
                createSchedule()
            }

            recyclerLocations.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun updateSelectedDate(date: Calendar) {
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.editDate.setText(dateFormat.format(date.time))
    }

    private fun searchNearbyLocations() {
        Toast.makeText(context, "주변 장소 검색 기능 구현 예정", Toast.LENGTH_SHORT).show()
    }

    private fun addFriend() {
        Toast.makeText(context, "친구 추가 기능 구현 예정", Toast.LENGTH_SHORT).show()
    }

    private fun createSchedule() {
        val date = binding.editDate.text.toString()
        val time = binding.editTime.text.toString()
        val place = binding.editPlace.text.toString()

        if (date.isEmpty() || time.isEmpty() || place.isEmpty()) {
            Toast.makeText(context, "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(context, "일정이 생성되었습니다: $date $time at $place", Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}