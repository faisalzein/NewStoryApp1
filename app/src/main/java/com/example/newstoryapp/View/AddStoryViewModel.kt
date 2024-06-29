package com.example.newstoryapp.View

import androidx.lifecycle.ViewModel
import com.example.newstoryapp.Repository.UserRepository
import java.io.File

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadAddStory(file: File, description: String) = repository.uploadAddStory(file, description)
}