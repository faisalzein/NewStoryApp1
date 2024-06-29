package com.example.newstoryapp.View

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newstoryapp.Api.ApiConfig
import com.example.newstoryapp.Pref.UserModel
import com.example.newstoryapp.Repository.UserRepository
import kotlinx.coroutines.launch

class SignupViewModel(private val repository: UserRepository) : ViewModel() {

    private val _signupResult = MutableLiveData<Result<UserModel>>()
    val signupResult: LiveData<Result<UserModel>> = _signupResult

    fun signup(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().register(name, email, password)
                if (response.error) {
                    _signupResult.value = Result.failure(Exception(response.message))
                } else {
                    val user = UserModel(email, password, "", true)
                    repository.saveSession(user)
                    _signupResult.value = Result.success(user)
                }
            } catch (e: Exception) {
                _signupResult.value = Result.failure(e)
            }
        }
    }
}
