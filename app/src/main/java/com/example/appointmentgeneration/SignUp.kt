package com.example.appointmentgeneration

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class SignUp : Fragment() {
    private lateinit var nicknameEditText: EditText
    private lateinit var idEditText: EditText
    private lateinit var idCheckIcon: ImageView
    private lateinit var pwEditText: EditText
    private lateinit var pwCheckEditText: EditText
    private lateinit var pwCheckIcon: ImageView
    private lateinit var signupButton: Button

    private lateinit var nickname: String
    private lateinit var id: String
    private lateinit var pw: String
    private lateinit var pwCheck: String

    private var isIdUniqueFlag: Boolean = false
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

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

        // Password fields input type
        pwEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        pwCheckEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        // ID 입력 시 중복 체크
        idEditText.addTextChangedListener(object : TextWatcher {
            private val debounceHandler = android.os.Handler()
            private var debounceRunnable: Runnable? = null

            override fun afterTextChanged(s: Editable?) {
                debounceRunnable?.let { debounceHandler.removeCallbacks(it) }
                val inputId = s.toString()
                debounceRunnable = Runnable {
                    if (inputId.isNotEmpty()) {
                        checkIdUniqueness(inputId) { isUnique ->
                            isIdUniqueFlag = isUnique
                            if (isUnique) {
                                idCheckIcon.setImageResource(R.drawable.check_true)
                            } else {
                                idCheckIcon.setImageResource(R.drawable.check_grey)
                            }
                        }
                    } else {
                        idCheckIcon.setImageResource(R.drawable.check_grey)
                        isIdUniqueFlag = false
                    }
                }
                debounceHandler.postDelayed(debounceRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Password 확인
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

        // 회원가입 버튼 클릭
        signupButton.setOnClickListener {
            nickname = nicknameEditText.text.toString()
            id = idEditText.text.toString()
            pw = pwEditText.text.toString()
            pwCheck = pwCheckEditText.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                if (areFieldsValid()) {
                    if (!isIdUniqueFlag) {
                        // ID가 중복된 경우 Firestore에서 최종 확인
                        val isUnique = checkIdUniqueness(id)
                        if (!isUnique) {
                            Toast.makeText(requireContext(), "이미 사용 중인 ID입니다.", Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                    }
                    val success = createUser()
                    if (success) {
                        Toast.makeText(requireContext(), "회원가입 성공", Toast.LENGTH_SHORT).show()
                        loginSuccess(id)
                        findNavController().navigate(
                            R.id.action_signUp_to_nextPage,
                            null,
                            androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.loginPage, true) // loginPage 포함 백스택 정리
                                .build()
                        )
                    } else {
                        Toast.makeText(requireContext(), "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "올바른 값을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return rootView
    }

    // Firestore에서 ID 중복 확인
    private fun checkIdUniqueness(id: String, callback: (Boolean) -> Unit) {
        db.collection("users")
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                callback(querySnapshot.isEmpty) // 고유하면 true, 중복이면 false
            }
            .addOnFailureListener { exception ->
                println("Error checking ID uniqueness: $exception")
                callback(false)
            }
    }

    private suspend fun checkIdUniqueness(id: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    continuation.resume(querySnapshot.isEmpty)
                }
                .addOnFailureListener { exception ->
                    println("Error checking ID uniqueness: $exception")
                    continuation.resume(false)
                }
        }
    }

    private suspend fun createUser(): Boolean {
        val user = User(nickname, id, pw)
        return suspendCancellableCoroutine { continuation ->
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    println("DocumentSnapshot added with ID: ${documentReference.id}")
                    continuation.resume(true)
                }
                .addOnFailureListener { exception ->
                    println("Error adding user: $exception")
                    continuation.resume(false)
                }
        }
    }

    private fun areFieldsValid(): Boolean {
        return nickname.isNotEmpty() &&
                id.isNotEmpty() &&
                pw.isNotEmpty() &&
                pwCheck.isNotEmpty() &&
                pw == pwCheck
    }

    private fun loginSuccess(id: String) {
        // 로그인 성공 처리 (SharedPreferences 사용하여 상태 저장)
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("userId", id)
        editor.apply()

        Toast.makeText(requireContext(), "로그인 성공! 환영합니다, $id 님.", Toast.LENGTH_SHORT).show()
    }
}
