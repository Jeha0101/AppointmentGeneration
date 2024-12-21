package com.example.appointmentgeneration.ui.schedules

import DividerItemDecoration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        loadAllSchedules()
        setupCalendarView()
    }

    private fun setupRecyclerView() {
        adapter = SchedulesAdapter(schedulesList)
        binding.recyclerViewSchedules.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewSchedules.adapter = adapter
        binding.recyclerViewSchedules.addItemDecoration(
            DividerItemDecoration(requireContext(), height = 2)
        )
    }

    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val dateString = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            loadSchedulesForDate(dateString)
        }
    }

    private fun loadAllSchedules() {
        val userId = getUserId() ?: return

        db.collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) return@addOnSuccessListener

                documents.forEach { document ->
                    loadUserSchedules(document.id)
                }
            }
    }

    private fun loadSchedulesForDate(dateString: String) {
        val userId = getUserId() ?: return

        db.collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) return@addOnSuccessListener

                documents.forEach { document ->
                    loadUserSchedules(document.id, dateFilter = dateString)
                }
            }
    }

    private fun loadUserSchedules(userDocId: String, dateFilter: String? = null) {
        db.collection("users")
            .document(userDocId)
            .collection("schedules")
            .get()
            .addOnSuccessListener { schedules ->
                schedulesList.clear()
                schedules.forEach { schedule ->
                    val date = schedule.getString("date") ?: ""
                    val time = schedule.getString("time") ?: ""
                    val description = schedule.getString("response") ?: ""

                    if (dateFilter == null || date == dateFilter) {
                        addScheduleItem(date, time, description)
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun addScheduleItem(date: String, time: String, description: String) {
        val headerItem = ScheduleItem(date, "날짜 구분선")
        if (!schedulesList.contains(headerItem)) {
            schedulesList.add(headerItem)
        }
        schedulesList.add(ScheduleItem(time, description))
    }

    private fun getUserId(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        return sharedPreferences.getString("userId", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
