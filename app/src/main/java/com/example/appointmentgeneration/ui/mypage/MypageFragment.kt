package com.example.appointmentgeneration.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {
    // View Binding 설정
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    // ViewModel 설정
    private val viewModel: MypageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    // UI 초기 설정
    private fun setupUI() {
        // 약속 관리 버튼 클릭 리스너
        binding.buttonManageAppointments.setOnClickListener {
            findNavController().navigate(R.id.navigation_schedules)
        }

        // 로그아웃 버튼 클릭 리스너
        binding.buttonLogout.setOnClickListener {
            logout()
        }
    }

    // ViewModel 관찰 설정
    private fun observeViewModel() {
        // 사용자 데이터 변경 관찰
        viewModel.userData.observe(viewLifecycleOwner) { userData ->
            binding.textViewUserName.text = userData.name
            // TODO: 프로필 이미지 로딩 로직 추가
        }

        // 로딩 상태 관찰
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // TODO: 로딩 상태에 따른 UI 처리
        }
    }

    // 로그아웃 처리 함수
    private fun logout() {
        // SharedPreferences에서 사용자 정보 삭제
        requireActivity().getSharedPreferences("UserPrefs", 0).edit().apply {
            putBoolean("isLoggedIn", false)
            remove("userId")
            apply()
        }

        // 로그인 페이지로 이동하고 백스택 제거
        findNavController().navigate(
            R.id.loginPage,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)
                .build()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}