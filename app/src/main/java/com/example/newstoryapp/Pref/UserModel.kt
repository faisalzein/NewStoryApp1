package com.example.newstoryapp.Pref

data class UserModel(
    val email: String,
    val password: String,
    val token: String,
    val isLogin: Boolean
)