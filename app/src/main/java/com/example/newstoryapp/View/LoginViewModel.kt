package com.example.newstoryapp.View

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.newstoryapp.Api.ApiConfig
import com.example.newstoryapp.Pref.UserModel
import com.example.newstoryapp.Repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().login(email, password)
                if (response.error) {
                    _loginResult.value = Result.failure(Exception(response.message))
                } else {
                    val user = UserModel(email, password, response.loginResult.token, true)
                    Log.d("LoginViewModel", "Token received during login: ${response.loginResult.token}")
                    repository.saveSession(user)
                    _loginResult.value = Result.success(response.message)
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }
}