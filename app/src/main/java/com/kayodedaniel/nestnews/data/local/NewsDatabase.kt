package com.kayodedaniel.nestnews.data.local

//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(entities = [ArticleEntity::class], version = 1)
//abstract class NewsDatabase : RoomDatabase() {
//    abstract fun articleDao(): ArticleDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: NewsDatabase? = null
//
//        fun getInstance(context: Context): NewsDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    NewsDatabase::class.java,
//                    "offlinenewsdatabase"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}