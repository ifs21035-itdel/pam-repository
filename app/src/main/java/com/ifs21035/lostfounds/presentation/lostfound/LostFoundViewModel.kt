package com.ifs21035.lostfounds.presentation.lostfound

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.data.remote.response.DataAddLostFoundResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundDetailResponse
import com.ifs21035.lostfounds.data.repository.LostFoundRepository
import com.ifs21035.lostfounds.presentation.ViewModelFactory

class LostFoundViewModel(
    private val lostFoundRepository: LostFoundRepository
) : ViewModel() {
    fun getLostFound(lostFoundId: Int): LiveData<MyResult<LostFoundDetailResponse>>{
        return lostFoundRepository.getLostFound(lostFoundId).asLiveData()
    }
    fun postLostFound(
        title: String,
        description: String,
        status: String
    ): LiveData<MyResult<DataAddLostFoundResponse>>{
        return lostFoundRepository.postLostFound(
            title,
            description,
            status
        ).asLiveData()
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
            isCompleted
        ).asLiveData()
    }
    fun deleteLostFound(lostFoundId: Int): LiveData<MyResult<LostFoundResponse>> {
        return lostFoundRepository.deleteLostFound(lostFoundId).asLiveData()
    }
    companion object {
        @Volatile
        private var INSTANCE: LostFoundViewModel? = null
        fun getInstance(
            lostFoundRepository: LostFoundRepository
        ): LostFoundViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LostFoundViewModel(
                    lostFoundRepository
                )
            }
            return INSTANCE as LostFoundViewModel
        }
    }
}
