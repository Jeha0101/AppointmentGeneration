package com.example.appointmentgeneration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LoginPage : Fragment() {

    private lateinit var idEditText: EditText
    private lateinit var pwEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login_page, container, false)
        initViews(rootView)
        setupListeners()
        return rootView
    }

    private fun initViews(rootView: View) {
        idEditText = rootView.findViewById(R.id.id_edit_text)
        pwEditText = rootView.findViewById(R.id.pw_edit_text)
        loginButton = rootView.findViewById(R.id.login_page_login_button)
        signupButton = rootView.findViewById(R.id.login_page_signup_button)
        pwEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun setupListeners() {
        loginButton.setOnClickListener { handleLogin() }
        signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginPage_to_signUpFragment)
        }
    }

    private fun handleLogin() {
        val id = idEditText.text.toString().trim()
        val password = pwEditText.text.toString().trim()

        if (id.isEmpty() || password.isEmpty()) {
            showToast("아이디와 비밀번호를 입력하세요.")
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                val success = checkCredentials(id, password)
                if (success) {
                    loginSuccess(id)
                    navigateToHome()
                } else {
                    showToast("아이디와 비밀번호가 일치하지 않습니다.")
                }
            }
        }
    }

    private suspend fun checkCredentials(id: String, password: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val storedPassword = querySnapshot.documents.firstOrNull()?.getString("pw")
                    continuation.resume(storedPassword == password)
                }
                .addOnFailureListener {
                    continuation.resume(false)
                }
        }
    }

    private fun loginSuccess(id: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        sharedPreferences.edit()
            .putBoolean("isLoggedIn", true)
            .putString("userId", id)
            .apply()
        showToast("로그인 성공! 환영합니다, $id 님.")
    }

    private fun navigateToHome() {
        findNavController().navigate(
            R.id.action_loginPage_to_navigation_home,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.loginPage, true)
                .build()
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}