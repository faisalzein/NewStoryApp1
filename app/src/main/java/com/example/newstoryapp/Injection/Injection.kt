package com.example.newstoryapp.Injection

import android.content.Context
import com.example.newstoryapp.Api.ApiConfig
import com.example.newstoryapp.Database.User.UserDatabase
import com.example.newstoryapp.Pref.UserPreference
import com.example.newstoryapp.Pref.dataStore
import com.example.newstoryapp.Repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val database = UserDatabase.getDatabase(context)
        val pref = UserPreference.getInstance(context.dataStore, database)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(database, pref, apiService)
    }
}