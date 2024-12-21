package com.example.appointmentgeneration.ui.schedules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointmentgeneration.R

class SchedulesAdapter(private val schedulesList: List<SchedulesFragment.ScheduleItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (schedulesList[position].description == "날짜 구분선") VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_schedule_header, parent, false))
            else -> ScheduleViewHolder(inflater.inflate(R.layout.item_schedule, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = schedulesList[position]
        when (holder) {
            is HeaderViewHolder -> holder.bind(item)
            is ScheduleViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = schedulesList.size

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val timeText: TextView = view.findViewById(R.id.schedule_time)
        private val descriptionText: TextView = view.findViewById(R.id.schedule_description)

        fun bind(item: SchedulesFragment.ScheduleItem) {
            timeText.text = item.time
            descriptionText.text = item.description
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val headerText: TextView = view.findViewById(R.id.header_date)

        fun bind(item: SchedulesFragment.ScheduleItem) {
            headerText.text = item.time
        }
    }
}