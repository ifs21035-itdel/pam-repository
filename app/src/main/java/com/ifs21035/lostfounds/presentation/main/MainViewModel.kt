package com.ifs21035.lostfounds.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ifs21035.lostfounds.data.pref.UserModel
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.data.remote.response.LostFoundResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundsResponse
import com.ifs21035.lostfounds.data.repository.AuthRepository
import com.ifs21035.lostfounds.data.repository.LostFoundRepository
import com.ifs21035.lostfounds.presentation.ViewModelFactory
import kotlinx.coroutines.launch

class MainViewModel(
    private val authRepository: AuthRepository,
    private val lostFoundRepository: LostFoundRepository
) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return authRepository.getSession().asLiveData()
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun getLostFounds(): LiveData<MyResult<LostFoundsResponse>> {
        return lostFoundRepository.getLostFounds(null, 1, null).asLiveData()
    }
    fun putLostFound(
        lostFoundId: Int,
        title: String,
        description: String,
        status: String,
        isCompleted: Boolean,
    ): LiveData<MyResult<LostFoundResponse>> {
        return lostFoundRepository.putLostFound(
            lostFoundId,
            title,
            description,
            status,
            isCompleted,
        ).asLiveData()
    }
    companion object {
        @Volatile
        private var INSTANCE: MainViewModel? = null
        fun getInstance(
            authRepository: AuthRepository,
            lostFoundRepository: LostFoundRepository
        ): MainViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = MainViewModel(
                    authRepository,
                    lostFoundRepository
                )
            }
            return INSTANCE as MainViewModel
        }
    }
}