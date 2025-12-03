package com.example.gitly.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gitly.data.local.dao.FavoriteRepoDao
import com.example.gitly.data.local.dao.FavoriteUserDao
import com.example.gitly.data.local.entity.FavoriteRepo
import com.example.gitly.data.local.entity.FavoriteUser

@Database(
    entities = [FavoriteRepo::class, FavoriteUser::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun favoriteRepoDao(): FavoriteRepoDao
    abstract fun favoriteUserDao(): FavoriteUserDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gitly_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
