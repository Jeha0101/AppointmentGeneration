package com.example.appointmentgeneration.ui.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appointmentgeneration.BuildConfig
import com.example.appointmentgeneration.R
import com.example.appointmentgeneration.ScheduleData
import com.example.appointmentgeneration.databinding.FragmentScheduleGenerationBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ScheduleGenerationFragment : Fragment() {

    private var _binding: FragmentScheduleGenerationBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val apiKey = BuildConfig.GPT_API_KEY
    private var generatedSchedule: String = "" // GPT 결과 저장

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleGenerationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달된 데이터 수신
        val scheduleData = arguments?.getParcelable<ScheduleData>("scheduleData")

        scheduleData?.let {
            showLoading(true) // 로딩 화면 표시
            fetchScheduleFromGPT(it) // GPT 호출
        }

        // 홈으로 돌아가기 버튼
        binding.btnBackToHome.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // 일정 저장 버튼
        binding.btnSaveSchedule.setOnClickListener {
            scheduleData?.let {
                saveScheduleToFirebase(it) // Firebase 저장 호출
            } ?: Toast.makeText(requireContext(), "저장할 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * GPT API 호출
     */
    private fun fetchScheduleFromGPT(scheduleData: ScheduleData) {
        val prompt = createPrompt(scheduleData)
        Log.d("GPTPrompt", prompt)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = callGPTApi(prompt)
                withContext(Dispatchers.Main) {
                    showLoading(false) // 로딩 화면 숨기기
                    generatedSchedule = response // 응답 저장
                    binding.tvScheduleResponse.text = response // 응답 표시
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(requireContext(), "일정 생성 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * GPT 프롬프트 생성
     */
    private fun createPrompt(scheduleData: ScheduleData): String {
        return buildString {
            append("사용자 일정 추천을 도와주세요.\n")
            append("목적지: ${scheduleData.desiredDestinations.joinToString(", ")}\n")
            append("날짜: ${scheduleData.scheduleDate ?: "미정"}\n")
            append("시간: ${scheduleData.scheduleTime ?: "미정"}\n")
            append("예산: ${scheduleData.budget ?: "상관없음"}\n")
            append("분위기: ${scheduleData.moods.joinToString(", ")}\n")
            append("추천 일정을 제안해주세요.")
        }
    }

    /**
     * GPT API 호출
     */
    private suspend fun callGPTApi(prompt: String): String {
        val client = OkHttpClient()

        val requestBody = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are a helpful assistant that suggests schedules.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
            put("temperature", 0.7)
        }

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(RequestBody.create("application/json".toMediaType(), requestBody.toString()))
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val responseBody = response.body?.string() ?: ""
                val jsonObject = JSONObject(responseBody)
                val choices = jsonObject.getJSONArray("choices")
                choices.getJSONObject(0).getJSONObject("message").getString("content")
            }
        }
    }

    /**
     * 일정 저장 (Firebase)
     */
    private fun saveScheduleToFirebase(scheduleData: ScheduleData) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val userId = sharedPreferences.getString("userId", null)

        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val scheduleMap = hashMapOf(
            "date" to (scheduleData.scheduleDate ?: "미정"),
            "time" to (scheduleData.scheduleTime ?: "미정"),
            "response" to generatedSchedule
        )

        firestore.collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection("users")
                        .document(document.id)
                        .collection("schedules")
                        .add(scheduleMap)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "일정 저장 완료!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    /**
     * 로딩 화면 처리
     */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
