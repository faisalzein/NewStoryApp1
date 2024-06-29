package com.example.newstoryapp.Pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.newstoryapp.Api.ApiConfig
import com.example.newstoryapp.Api.ApiService
import com.example.newstoryapp.Database.User.UserDatabase
import com.example.newstoryapp.Pref.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(
    private val dataStore: DataStore<Preferences>,
    private val userDatabase: UserDatabase
){
    private val apiService: ApiService = ApiConfig.getApiService()

    suspend fun saveSession(user: UserModel){
        dataStore.edit{ preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[PASSWORD_KEY] = user.password
            preferences[TOKEN_KEY] = user.token.toString()
            preferences[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map{ preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[PASSWORD_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
            )
        }
    }

    suspend fun logout(){
        dataStore.edit{preferences ->
            preferences.clear()
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }


    companion object{
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")

        private const val TAG = "StoryViewModel"

        fun getInstance(dataStore: DataStore<Preferences>, userDatabase: UserDatabase): UserPreference{
            return INSTANCE ?: synchronized(this){
                val instance =UserPreference(dataStore, userDatabase)
                INSTANCE =instance
                instance
            }
        }
    }
}