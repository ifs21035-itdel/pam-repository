package com.ifs21035.lostfounds.presentation.lostfound

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21035.lostfounds.data.local.entity.LostFoundEntity
import com.ifs21035.lostfounds.data.remote.MyResult
import com.ifs21035.lostfounds.data.remote.response.DataAddLostFoundResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundResponse
import com.ifs21035.lostfounds.data.remote.response.LostFoundDetailResponse
import com.ifs21035.lostfounds.data.repository.LocalLostFoundRepository
import com.ifs21035.lostfounds.data.repository.LostFoundRepository
import com.ifs21035.lostfounds.presentation.ViewModelFactory

class LostFoundViewModel(
    private val lostFoundRepository: LostFoundRepository,
    private val localLostFoundRepository: LocalLostFoundRepository
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
    fun getLocalLostFounds(): LiveData<List<LostFoundEntity>?> {
        return localLostFoundRepository.getAllLostFounds()
    }
    fun getLocalLostFound(lostFoundId: Int): LiveData<LostFoundEntity?> {
        return localLostFoundRepository.get(lostFoundId)
    }
    fun insertLocalTodo(lostfound: LostFoundEntity) {
        localLostFoundRepository.insert(lostfound)
    }
    fun deleteLocalTodo(lostfound: LostFoundEntity) {
        localLostFoundRepository.delete(lostfound)
    }
    companion object {
        @Volatile
        private var INSTANCE: LostFoundViewModel? = null
        fun getInstance(
            lostFoundRepository: LostFoundRepository,
            lostFoundLocalRepository: LocalLostFoundRepository,
        ): LostFoundViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LostFoundViewModel(
                    lostFoundRepository,
                    lostFoundLocalRepository,
                )
            }
            return INSTANCE as LostFoundViewModel
        }
    }
}
