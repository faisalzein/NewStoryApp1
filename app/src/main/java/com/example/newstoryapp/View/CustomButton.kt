package com.example.newstoryapp.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.newstoryapp.R
import com.example.newstoryapp.View.LoginActivity
import com.example.newstoryapp.View.SignupActivity

class CustomButton: AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var txtColor: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable

    init {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if(isEnabled) enabledBackground else disabledBackground
        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER

        val isAllFieldsFilled = when (context) {
            is LoginActivity -> {
                val loginActivity = context as LoginActivity
                loginActivity.myEditTextLogin.all { it.text.toString().isNotEmpty() }
            }
            is SignupActivity -> {
                val signupActivity = context as SignupActivity
                signupActivity.myEditTextSignup.all { it.text.toString().isNotEmpty() }
            }
            else -> false
        }

        text = when {
            !isAllFieldsFilled -> "Isi Dulu"
            context is LoginActivity -> "Sign In"
            context is SignupActivity -> "Sign Up"
            else -> "Submit"
        }
    }
}