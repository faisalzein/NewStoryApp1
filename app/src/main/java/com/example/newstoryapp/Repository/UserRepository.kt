package com.example.newstoryapp.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.newstoryapp.Api.ApiService
import com.example.newstoryapp.Database.User.UserDatabase
import com.example.newstoryapp.Pref.UserModel
import com.example.newstoryapp.Pref.UserPreference
import com.example.newstoryapp.Response.ListStoryItem
import com.example.newstoryapp.Response.ResponseAddStory
import com.example.newstoryapp.Result.ResultState
import com.example.newstoryapp.Result.UserRemoteMediator
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private val userDatabase: UserDatabase,
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
    private val _listStories = MutableLiveData<List<ListStoryItem>>()

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun uploadAddStory(imageFile: File, description: String): LiveData<ResultState<ResponseAddStory>> = liveData{
        emit(ResultState.Loading)
        val token = userPreference.getToken().first()
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.addStory(token, multipartBody, requestBody)
            emit(ResultState.Success(successResponse))
        } catch (e: HttpException) {
            Log.d("UserPreference", "getAllStory: token=$token")
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ResponseAddStory::class.java)
            emit(ResultState.Error(errorResponse.message))
        }
    }

    suspend fun getStoriesWithLocation(token: String): List<ListStoryItem>? {
        try {
            val response = apiService.getStoriesWithLocation("Bearer $token")
            if (response.isSuccessful) {
                _listStories.postValue(response.body()?.listStory)
                return response.body()?.listStory
            } else {
                Log.e("UserPreference", "getStoriesWithLocation: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("UserPreference", "getStoriesWithLocation: Exception ${e.message}", e)
        }
        return null
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getPagingStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = UserRemoteMediator(userDatabase, apiService, token),
            pagingSourceFactory = { userDatabase.userDao().getAllStory() }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userDatabase: UserDatabase,
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userDatabase, userPreference, apiService)
            }.also { instance = it }
    }
}
