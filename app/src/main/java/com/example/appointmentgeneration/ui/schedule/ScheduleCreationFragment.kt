package com.example.appointmentgeneration.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appointmentgeneration.databinding.ScheduleCreationFragmentBinding
import java.text.SimpleDateFormat
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
        setupCalendarView()
        setupButtons()
        setupRecyclerView()
    }

    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            updateSelectedDate(selectedDate)
        }
    }

    private fun setupButtons() {
        binding.btnSearchLocations.setOnClickListener { searchNearbyLocations() }
        binding.btnAddFriend.setOnClickListener { addFriend() }
        binding.btnCreateSchedule.setOnClickListener { createSchedule() }
    }

    private fun setupRecyclerView() {
        binding.recyclerLocations.layoutManager = LinearLayoutManager(context)
    }

    private fun updateSelectedDate(date: Calendar) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.editDate.setText(dateFormat.format(date.time))
    }

    private fun searchNearbyLocations() {
        // 검색 기능 구현 예정
    }

    private fun addFriend() {
        // 친구 추가 기능 구현 예정
    }

    private fun createSchedule() {
        val date = binding.editDate.text.toString()
        val time = binding.editTime.text.toString()
        val place = binding.editPlace.text.toString()

        if (date.isEmpty() || time.isEmpty() || place.isEmpty()) {
            showMessage("모든 필드를 입력해주세요")
            return
        }

        showMessage("일정이 생성되었습니다: $date $time at $place")
        requireActivity().onBackPressed()
    }

    private fun showMessage(message: String) {
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
