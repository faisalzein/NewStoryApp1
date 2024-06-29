package com.example.newstoryapp.View

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.newstoryapp.databinding.ActivitySignupBinding
import com.example.newstoryapp.View.ViewModelFactory
import com.example.newstoryapp.View.CustomButton
import com.example.newstoryapp.View.CustomEditText
import com.example.newstoryapp.View.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var myButtonSignup: CustomButton
    lateinit var myEditTextSignup: Array<CustomEditText>
    private lateinit var myEditTextSignupString: Array<String>
    private val viewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setMyButtonEnable() {
        var allFieldsFilled = true
        for (editText in myEditTextSignup) {
            if (editText.text.toString().isEmpty()) {
                allFieldsFilled = false
                break
            }
        }

        val result = myEditTextSignup[2].text
        myButtonSignup.isEnabled = allFieldsFilled && result != null && result.toString().isNotEmpty() && result.length >= 8
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameText = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEdit = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(100)
        val emailText = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEdit = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(100)
        val passwordText = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.myButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(title, nameText, nameEdit, emailText, emailEdit, passwordText, passwordEdit, signup)
            start()
        }
    }

    private fun setupAction() {
        val username: CustomEditText = binding.edRegisterName
        val email: CustomEditText = binding.edRegisterEmail
        val password: CustomEditText = binding.edRegisterPassword

        myButtonSignup = binding.myButton
        myEditTextSignup = arrayOf(
            username,
            email,
            password
        )

        setMyButtonEnable()

        for (editText in myEditTextSignup) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    setMyButtonEnable()
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }

        myEditTextSignupString = myEditTextSignup.map { it.text.toString() }.toTypedArray()

        binding.myButton.setOnClickListener {
            val nameSignup = username.text.toString()
            val emailSignup = email.text.toString()
            val passwordSignup = password.text.toString()

            viewModel.signup(nameSignup, emailSignup, passwordSignup)
            binding.progressbar.visibility = View.VISIBLE
        }

        viewModel.signupResult.observe(this, Observer { result ->
            binding.progressbar.visibility = View.GONE
            result.onSuccess { user ->
                AlertDialog.Builder(this).apply {
                    setTitle("Yeah!")
                    setMessage("Akun dengan ${user.email} sudah jadi nih. Yuk, login dan belajar coding.")
                    setPositiveButton("Lanjut") { _, _ ->
                        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            }
            result.onFailure { error ->
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    //setMessage(error.message)
                    setMessage("Akun tersebut sudah terdaftar!")
                    setPositiveButton("OK", null)
                    create()
                    show()
                }
            }
            //binding.progressbar.visibility = View.INVISIBLE
        })
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        supportActionBar?.hide()
    }
}
