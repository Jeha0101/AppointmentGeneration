package com.example.appointmentgeneration.ui.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appointmentgeneration.BuildConfig
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
    private var generatedSchedule: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleGenerationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scheduleData = arguments?.getParcelable<ScheduleData>("scheduleData")

        scheduleData?.let {
            showLoading(true)
            fetchScheduleFromGPT(it)
        }

        binding.btnBackToHome.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSaveSchedule.setOnClickListener {
            scheduleData?.let { saveScheduleToFirebase(it) }
        }
    }

    private fun fetchScheduleFromGPT(scheduleData: ScheduleData) {
        val prompt = createPrompt(scheduleData)
        Log.d("GPTPrompt", prompt)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = callGPTApi(prompt)
                updateUI(response)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

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

    private suspend fun callGPTApi(prompt: String): String {
        val client = OkHttpClient()
        val requestBody = buildRequestBody(prompt)
        val request = buildRequest(requestBody)

        return withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                parseResponse(response)
            }
        }
    }

    private fun buildRequestBody(prompt: String): RequestBody {
        val body = JSONObject().apply {
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
        return RequestBody.create("application/json".toMediaType(), body.toString())
    }

    private fun buildRequest(requestBody: RequestBody): Request {
        return Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .post(requestBody)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()
    }

    private fun parseResponse(response: Response): String {
        val responseBody = response.body?.string() ?: ""
        val jsonObject = JSONObject(responseBody)
        val choices = jsonObject.getJSONArray("choices")
        return choices.getJSONObject(0).getJSONObject("message").getString("content")
    }

    private fun saveScheduleToFirebase(scheduleData: ScheduleData) {
        val userId = getUserId() ?: return
        val scheduleMap = buildScheduleMap(scheduleData)

        firestore.collection("users")
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach { document ->
                    firestore.collection("users")
                        .document(document.id)
                        .collection("schedules")
                        .add(scheduleMap)
                }
            }
    }

    private fun buildScheduleMap(scheduleData: ScheduleData): Map<String, Any> {
        return mapOf(
            "date" to (scheduleData.scheduleDate ?: "미정"),
            "time" to (scheduleData.scheduleTime ?: "미정"),
            "response" to generatedSchedule
        )
    }

    private fun getUserId(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        return sharedPreferences.getString("userId", null)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private suspend fun updateUI(response: String) {
        withContext(Dispatchers.Main) {
            showLoading(false)
            generatedSchedule = response
            binding.tvScheduleResponse.text = response
        }
    }

    private suspend fun handleError(e: Exception) {
        withContext(Dispatchers.Main) {
            showLoading(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
