package com.example.appointmentgeneration.ui.schedules

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appointmentgeneration.R

class SchedulesAdapter(private val schedulesList: List<SchedulesFragment.ScheduleItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return if (schedulesList[position].description == "날짜 구분선") VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
            ScheduleViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = schedulesList[position]

        if (holder is HeaderViewHolder) {
            holder.headerText.text = item.time // 날짜 헤더
        } else if (holder is ScheduleViewHolder) {
            holder.timeText.text = item.time
            holder.descriptionText.text = item.description
        }
    }

    override fun getItemCount(): Int = schedulesList.size

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.schedule_time)
        val descriptionText: TextView = view.findViewById(R.id.schedule_description)
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerText: TextView = view.findViewById(R.id.header_date)
    }
}
