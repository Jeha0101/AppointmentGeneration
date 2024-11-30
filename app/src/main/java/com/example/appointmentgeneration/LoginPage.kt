package com.example.appointmentgeneration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LoginPage : Fragment() {
    private lateinit var idEditText: EditText
    private lateinit var pwEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_login_page, container, false)

        idEditText = rootView.findViewById(R.id.id_edit_text)
        pwEditText = rootView.findViewById(R.id.pw_edit_text)
        loginButton = rootView.findViewById(R.id.login_page_login_button)
        signupButton = rootView.findViewById(R.id.login_page_signup_button)

        pwEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        loginButton.setOnClickListener {
            handleLogin()
        }

        signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginPage_to_signUpFragment)
        }

        return rootView
    }

    private fun handleLogin() {
        val id = idEditText.text.toString().trim()
        val password = pwEditText.text.toString().trim()

        when {
            id.isEmpty() && password.isEmpty() -> {
                Toast.makeText(requireContext(), "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            id.isEmpty() -> {
                Toast.makeText(requireContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(requireContext(), "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                if (checkCredentials(id, password)) {
                    findNavController().navigate(R.id.action_loginPage_to_navigation_home)
                } else {
                    Toast.makeText(requireContext(), "아이디와 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkCredentials(id: String, password: String): Boolean {
        // 파이어베이스에서 사용자 아이디 정보를 읽어오기
        // 일치하는 아이디를 찾아서 비밀번호 읽어오기
        // 비밀번호가 일치하면 true, 아니면 false
        return true
    }
}
