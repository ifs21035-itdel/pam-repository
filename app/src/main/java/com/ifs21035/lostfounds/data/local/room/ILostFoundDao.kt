package com.ifs21035.lostfounds.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ifs21035.lostfounds.data.local.entity.LostFoundEntity

@Dao
interface ILostFoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(lostFound: LostFoundEntity)

    @Delete
    fun delete(lostFound: LostFoundEntity)

    @Query("SELECT * FROM lost_founds WHERE id = :id LIMIT 1")
    fun get(id: Int): LiveData<LostFoundEntity?>

    @Query("SELECT * FROM lost_founds ORDER BY created_at DESC")
    fun getAllLostFounds(): LiveData<List<LostFoundEntity>?>
}
