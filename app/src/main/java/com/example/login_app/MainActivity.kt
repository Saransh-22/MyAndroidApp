package com.example.login_app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var togglePassword: ImageView
    private lateinit var signupLink: TextView
    private var isPasswordVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        togglePassword = findViewById(R.id.toggle_password)
        signupLink = findViewById(R.id.signup_link)

        // Toggle password visibility
        togglePassword.setOnClickListener {
            if (isPasswordVisible) {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_visibility_off)
            } else {
                passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_visibility)
            }
            isPasswordVisible = !isPasswordVisible
            passwordInput.setSelection(passwordInput.text.length)
        }

        // Login button action
        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                if (username.isEmpty()) usernameInput.error = "Username required"
                if (password.isEmpty()) passwordInput.error = "Password required"
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
            } else {
                performLogin(username, password)
            }
        }

        // Redirect to signup activity
        signupLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun performLogin(username: String, password: String) {
        val url = "${Constants.BASE_URL}/login"

        val jsonBody = JSONObject().apply {
            put("username", username)
            put("password", password)
        }

        val queue = Volley.newRequestQueue(this)
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                val message = response.optString("message", "Login successful")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // Clear fields and proceed
                usernameInput.text.clear()
                passwordInput.text.clear()

                // You can navigate to a new activity here
                Log.i("LOGIN", "Login Successful")
                // Example: startActivity(Intent(this, HomeActivity::class.java))
            },
            { error ->
                val code = error.networkResponse?.statusCode
                val errorMsg = when (code) {
                    401 -> "Invalid username or password"
                    else -> "Error: ${code ?: "Network Error"}"
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                Log.e("LOGIN", "Error: $errorMsg")
            }
        )

        queue.add(request)
    }
}
