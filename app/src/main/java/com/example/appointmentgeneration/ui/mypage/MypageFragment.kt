package com.example.appointmentgeneration.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        binding.buttonManageAppointments.setOnClickListener {
            findNavController().navigate(R.id.navigation_schedules)
        }

        binding.buttonLogout.setOnClickListener {
            logout()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logout(){
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", false)
        editor.remove("userId")
        editor.apply()
        findNavController().navigate(
            R.id.loginPage,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true) // navigation_home을 포함한 모든 스택 제거
                .build()
        )
    }
}
