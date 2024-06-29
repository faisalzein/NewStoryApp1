package com.example.newstoryapp.View

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newstoryapp.Repository.UserRepository
import com.example.newstoryapp.Response.ListStoryItem

import kotlinx.coroutines.launch

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _stories = MutableLiveData<List<ListStoryItem>?>()
    val stories: MutableLiveData<List<ListStoryItem>?> get() = _stories

    init {
        getStoriesWithLocation()
    }

    private fun getStoriesWithLocation() {
        viewModelScope.launch {
            try {
                userRepository.getSession().collect { user ->
                    if (user.token.isNotEmpty()) {
                        val locations = userRepository.getStoriesWithLocation(user.token)
                        _stories.postValue(locations)
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions as needed
                Log.e("MapsViewModel", "getStoriesWithLocation error: ${e.message}", e)
            }
        }
    }
}