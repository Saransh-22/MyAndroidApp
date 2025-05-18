package com.example.login_app

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var signupBtn: Button
    private lateinit var passwordToggle: ImageView
    private lateinit var confirmPasswordToggle: ImageView

    private var passwordVisible = false
    private var confirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        usernameInput = findViewById(R.id.signup_username)
        passwordInput = findViewById(R.id.signup_password)
        confirmPasswordInput = findViewById(R.id.signup_confirm_password)
        signupBtn = findViewById(R.id.signup_btn)
        passwordToggle = findViewById(R.id.password_toggle)
        confirmPasswordToggle = findViewById(R.id.confirm_password_toggle)

        // Password visibility toggles
        passwordToggle.setOnClickListener {
            passwordVisible = !passwordVisible
            togglePasswordVisibility(passwordInput, passwordToggle, passwordVisible)
        }

        confirmPasswordToggle.setOnClickListener {
            confirmPasswordVisible = !confirmPasswordVisible
            togglePasswordVisibility(confirmPasswordInput, confirmPasswordToggle, confirmPasswordVisible)
        }

        // Watchers to enable signup button
        setupTextWatchers()

        // Signup action
        signupBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                performSignup(username, password)
            }
        }
    }

    private fun togglePasswordVisibility(editText: EditText, toggle: ImageView, visible: Boolean) {
        if (visible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggle.setImageResource(R.drawable.ic_visibility)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggle.setImageResource(R.drawable.ic_visibility_off)
        }
        editText.setSelection(editText.text.length)
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                signupBtn.isEnabled = passwordInput.text.toString() == confirmPasswordInput.text.toString() &&
                        passwordInput.text.isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        passwordInput.addTextChangedListener(textWatcher)
        confirmPasswordInput.addTextChangedListener(textWatcher)
    }

    private fun performSignup(username: String, password: String) {
        val url = "${Constants.BASE_URL}/signup"

        val jsonBody = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                val message = response.optString("message", "Signup successful")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            { error ->
                val code = error.networkResponse?.statusCode
                val errorMsg = when (code) {
                    400 -> "User already exists"
                    else -> "Error: ${code ?: "Network Error"}"
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(request)
    }
}
