package com.example.appointmentgeneration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SignUp : Fragment() {
    private lateinit var nicknameEditText: EditText
    private lateinit var idEditText: EditText
    private lateinit var idCheckIcon: ImageView
    private lateinit var pwEditText: EditText
    private lateinit var pwCheckEditText: EditText
    private lateinit var pwCheckIcon: ImageView
    private lateinit var signupButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_sign_up, container, false)

        nicknameEditText = rootView.findViewById(R.id.nickname_edit_text)
        idEditText = rootView.findViewById(R.id.id_edit_text)
        idCheckIcon = rootView.findViewById(R.id.id_check_icon)
        pwEditText = rootView.findViewById(R.id.pw_edit_text)
        pwCheckEditText = rootView.findViewById(R.id.pw_check_edit_text)
        pwCheckIcon = rootView.findViewById(R.id.pw_check_icon)
        signupButton = rootView.findViewById(R.id.signup_page_signup_button)

        nicknameEditText.isEnabled = true
        idEditText.isEnabled = true

        pwEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        pwCheckEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Add listeners
        idEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isIdUnique(s.toString())) {
                    idCheckIcon.setImageResource(R.drawable.check_true)
                } else {
                    idCheckIcon.setImageResource(R.drawable.check_grey)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        pwCheckEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() == pwEditText.text.toString()) {
                    pwCheckIcon.setImageResource(R.drawable.check_true)
                } else {
                    pwCheckIcon.setImageResource(R.drawable.check_grey)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        signupButton.setOnClickListener {
            if (areFieldsValid()) {
                createUser()
                findNavController().navigate(R.id.action_signUp_to_nextPage)
                loginSuccess(idEditText.text.toString())
            } else {
                Toast.makeText(requireContext(), "올바른 값을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }
        return rootView
    }

    private fun isIdUnique(id: String): Boolean {
        // 사용자의 아이디와 중복되는 아이디가 있는지 파이어베이스에서 검사하는 동작을 구현할 것
        return true
    }

    private fun areFieldsValid(): Boolean {
        val nickname = nicknameEditText.text.toString()
        val id = idEditText.text.toString()
        val pw = pwEditText.text.toString()
        val pwCheck = pwCheckEditText.text.toString()

        return nickname.isNotEmpty() &&
                id.isNotEmpty() &&
                pw.isNotEmpty() &&
                pwCheck.isNotEmpty() &&
                isIdUnique(id) &&
                pw == pwCheck
    }

    private fun createUser() {
        // 사용자를 파이어베이스에 추가하는 코드를 구현할 것
    }

    private fun loginSuccess(id: String) {
        // 로그인 성공 시 처리
        // 아마도 id 전달
    }
}
