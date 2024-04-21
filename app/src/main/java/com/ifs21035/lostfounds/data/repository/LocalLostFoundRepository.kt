package com.ifs21035.lostfounds.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.ifs21035.lostfounds.data.local.entity.LostFoundEntity
import com.ifs21035.lostfounds.data.local.room.ILostFoundDao
import com.ifs21035.lostfounds.data.local.room.LostFoundDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
class LocalLostFoundRepository(context: Context) {
    private val mLostFoundDao: ILostFoundDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()
    init {
        val db = LostFoundDatabase.getInstance(context)
        mLostFoundDao = db.lostFoundDao()
    }
    fun getAllLostFounds(): LiveData<List<LostFoundEntity>?> = mLostFoundDao.getAllLostFounds()
    fun get(lostFoundId: Int): LiveData<LostFoundEntity?> = mLostFoundDao.get(lostFoundId)
    fun insert(lostfound: LostFoundEntity) {
        executorService.execute { mLostFoundDao.insert(lostfound) }
    }
    fun delete(lostfound: LostFoundEntity) {
        executorService.execute { mLostFoundDao.delete(lostfound) }
    }
    companion object {
        @Volatile
        private var INSTANCE: LocalLostFoundRepository? = null
        fun getInstance(
            context: Context
        ): LocalLostFoundRepository {
            synchronized(LocalLostFoundRepository::class.java) {
                INSTANCE = LocalLostFoundRepository(
                    context
                )
            }
            return INSTANCE as LocalLostFoundRepository
        }
    }
}