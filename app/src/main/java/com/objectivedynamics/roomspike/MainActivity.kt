package com.objectivedynamics.roomspike

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.objectivedynamics.roomspike.ui.theme.RoomSpikeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: WordRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomSpikeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        val wordRoomDatabase = WordRoomDatabase.getDatabase(applicationContext, lifecycleScope)
        val wordDao = wordRoomDatabase.wordDao()
        repository = WordRepository(wordDao)

        lifecycleScope.launch {
            val words = repository.allWords.collect {
                wordsList ->
                val message = "Words from database: $wordsList"
                Log.d("RoomDatabase", message)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RoomSpikeTheme {
        Greeting("Android")
    }
}