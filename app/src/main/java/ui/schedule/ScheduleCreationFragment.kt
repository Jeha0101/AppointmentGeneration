package com.example.appointmentgeneration.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.databinding.ScheduleCreationFragmentBinding
import java.util.*
import android.widget.TextView



class ScheduleCreationFragment : Fragment() {
    private var _binding: ScheduleCreationFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleAdapter: ScheduleAdapter
    private val schedules = mutableListOf<Schedule>()

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
        setupCalendar()
        setupRecyclerView()
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSearchLocations.setOnClickListener {
                Toast.makeText(context, "장소 검색 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            btnAddFriend.setOnClickListener {
                Toast.makeText(context, "친구 추가 기능 구현 예정", Toast.LENGTH_SHORT).show()
            }

            btnCreateSchedule.setOnClickListener {
                createSchedule()
            }
        }
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            updateScheduleForDate(selectedDate)
        }
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(schedules)
        binding.recyclerLocations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = scheduleAdapter
        }
    }

    private fun updateScheduleForDate(date: Calendar) {
        // 여기서 선택된 날짜에 해당하는 일정을 가져와 schedules 리스트를 업데이트합니다.
        // 실제 앱에서는 데이터베이스나 API에서 데이터를 가져와야 합니다.
        schedules.clear()
        schedules.add(Schedule("오전 회의", "09:00 - 10:30", "회의실 A"))
        schedules.add(Schedule("점심 식사", "12:00 - 13:00", "구내 식당"))
        schedules.add(Schedule("프로젝트 미팅", "14:00 - 16:00", "회의실 B"))
        scheduleAdapter.notifyDataSetChanged()
    }

    private fun createSchedule() {
        val date = binding.editDate.text.toString()
        val time = binding.editTime.text.toString()
        val place = binding.editPlace.text.toString()

        if (date.isEmpty() || time.isEmpty() || place.isEmpty()) {
            Toast.makeText(context, "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // 약속 생성 로직 구현
        Toast.makeText(context, "약속이 생성되었습니다", Toast.LENGTH_SHORT).show()

        // 생성된 약속을 schedules 리스트에 추가하고 RecyclerView 갱신
        schedules.add(Schedule(place, time, place))
        scheduleAdapter.notifyItemInserted(schedules.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

data class Schedule(val title: String, val time: String, val location: String)

class ScheduleAdapter(private val schedules: List<Schedule>) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.textViewTitle)
        val timeTextView: TextView = view.findViewById(R.id.textViewTime)
        val locationTextView: TextView = view.findViewById(R.id.textViewLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.titleTextView.text = schedule.title
        holder.timeTextView.text = schedule.time
        holder.locationTextView.text = schedule.location
    }

    override fun getItemCount() = schedules.size
}