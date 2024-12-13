package com.example.appointmentgeneration.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appointmentgeneration.databinding.FragmentHomeBinding
import java.util.*
import com.example.appointmentgeneration.R

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        with(binding) {
            btnAddFriend.setOnClickListener {
                Toast.makeText(context, "친구 위치 추가를 선택했습니다.", Toast.LENGTH_SHORT).show()
            }

            btnDate.setOnClickListener {
                val calendar = Calendar.getInstance()
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        Toast.makeText(context, "날짜: $year-${month + 1}-$dayOfMonth", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            btnTime.setOnClickListener {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        Toast.makeText(context, "시간: $hour:$minute", Toast.LENGTH_SHORT).show()
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }

            btnCreateSchedule.setOnClickListener {
                Toast.makeText(context, "일정 생성 완료!", Toast.LENGTH_SHORT).show()
            }

            radioGroupPrice.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radio_no_price -> Toast.makeText(context, "가격 제한 없음 선택", Toast.LENGTH_SHORT).show()
                    R.id.radio_set_price -> Toast.makeText(context, "가격 설정 선택", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
