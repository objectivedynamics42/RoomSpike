package com.objectivedynamics.roomspike

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//  This is the backend. The database. This used to be done by the OpenHelper.
//  The fact that this has very few comments emphasizes its coolness.

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Word::class], version = 1)
abstract class WordRoomDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao

    companion object {

        //  Effectively a singleton
        @Volatile
        private var INSTANCE: WordRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): WordRoomDatabase {
            //  getDatabase could conceivably be called at any point and potentially many times
            //  over the lifecycle of the app.
            //  The first time, the INSTANCE singleton will be null and so the synchronized block
            //  will be executed to build the database. On subsequent calls, the singleton will
            //  have been initialised in the first pass and so it's value will be returned
            //  without creation
            //  So, if the INSTANCE is not null, then return it, if it is, then create the database
            val wordRoomDatabase = INSTANCE ?: synchronized(this) {
                //  Create a database builder
                val databaseBuilder = Room.databaseBuilder(
                    context.applicationContext,
                    WordRoomDatabase::class.java,
                    "word_database"
                )
                val instance = databaseBuilder
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()

                //  Assign the newly built instance to the singleton
                INSTANCE = instance
                // return instance
                instance
            }
            return wordRoomDatabase
        }

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.wordDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(wordDao: WordDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            wordDao.deleteAll()

            var word = Word("Hello")
            wordDao.insert(word)
            word = Word("World!")
            wordDao.insert(word)
        }
    }
}

