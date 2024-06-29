package com.example.newstoryapp.View

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newstoryapp.Pref.UserModel
import com.example.newstoryapp.Repository.UserRepository
import com.example.newstoryapp.Response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _token = MutableLiveData<String>()
    private val _stories = MutableLiveData<LiveData<PagingData<ListStoryItem>>>()

    val stories: LiveData<PagingData<ListStoryItem>> = _stories.switchMap { it }

    fun setToken(token: String) {
        _token.value = token
        _stories.value = repository.getPagingStory(token).cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}

