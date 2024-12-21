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
        initViews(rootView)
        setupListeners()
        return rootView
    }

    private fun initViews(rootView: View) {
        nicknameEditText = rootView.findViewById(R.id.nickname_edit_text)
        idEditText = rootView.findViewById(R.id.id_edit_text)
        idCheckIcon = rootView.findViewById(R.id.id_check_icon)
        pwEditText = rootView.findViewById(R.id.pw_edit_text)
        pwCheckEditText = rootView.findViewById(R.id.pw_check_edit_text)
        pwCheckIcon = rootView.findViewById(R.id.pw_check_icon)
        signupButton = rootView.findViewById(R.id.signup_page_signup_button)

        pwEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        pwCheckEditText.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun setupListeners() {
        setupIdInputListener()
        setupPasswordCheckListener()
        signupButton.setOnClickListener { handleSignUp() }
    }

    private fun setupIdInputListener() {
        idEditText.addTextChangedListener(object : TextWatcher {
            private val debounceHandler = android.os.Handler()
            private var debounceRunnable: Runnable? = null

            override fun afterTextChanged(s: Editable?) {
                debounceRunnable?.let { debounceHandler.removeCallbacks(it) }
                val inputId = s.toString()
                debounceRunnable = Runnable {
                    if (inputId.isNotEmpty()) {
                        lifecycleScope.launch {
                            isIdUniqueFlag = checkIdUniqueness(inputId)
                            idCheckIcon.setImageResource(
                                if (isIdUniqueFlag) R.drawable.check_true else R.drawable.check_grey
                            )
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
    }

    private fun setupPasswordCheckListener() {
        pwCheckEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                pwCheckIcon.setImageResource(
                    if (s.toString() == pwEditText.text.toString()) R.drawable.check_true
                    else R.drawable.check_grey
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun handleSignUp() {
        nickname = nicknameEditText.text.toString()
        id = idEditText.text.toString()
        pw = pwEditText.text.toString()
        pwCheck = pwCheckEditText.text.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            if (areFieldsValid()) {
                if (!isIdUniqueFlag && !checkIdUniqueness(id)) return@launch
                if (createUser()) {
                    loginSuccess(id)
                    navigateToNextPage()
                }
            }
        }
    }

    private suspend fun checkIdUniqueness(id: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnSuccessListener { continuation.resume(it.isEmpty) }
                .addOnFailureListener { continuation.resume(false) }
        }
    }

    private suspend fun createUser(): Boolean {
        val user = User(nickname, id, pw)
        return suspendCancellableCoroutine { continuation ->
            db.collection("users")
                .add(user)
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { continuation.resume(false) }
        }
    }

    private fun areFieldsValid(): Boolean {
        return nickname.isNotEmpty() && id.isNotEmpty() && pw.isNotEmpty() &&
                pwCheck.isNotEmpty() && pw == pwCheck
    }

    private fun loginSuccess(id: String) {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        sharedPreferences.edit()
            .putBoolean("isLoggedIn", true)
            .putString("userId", id)
            .apply()
    }

    private fun navigateToNextPage() {
        findNavController().navigate(
            R.id.action_signUp_to_nextPage,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.loginPage, true)
                .build()
        )
    }
}
