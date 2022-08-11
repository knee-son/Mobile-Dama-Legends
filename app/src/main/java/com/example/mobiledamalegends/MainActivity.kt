package com.example.mobiledamalegends

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import com.example.mobiledamalegends.databinding.ActivityMainBinding
import java.nio.IntBuffer

class MainActivity : AppCompatActivity() {
    var theme_index = 0
    val theme_list  = mutableListOf(
        mapOf(
            "name"  to "Cheese Cake",
            "board" to R.drawable.board_1,
            "white" to R.drawable.piece_light_1,
            "black" to R.drawable.piece_dark_1
        ),
        mapOf(
            "name"  to "Blueberry",
            "board" to R.drawable.board_2,
            "white" to R.drawable.piece_light_2,
            "black" to R.drawable.piece_dark_2
        )
    )

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mp = MediaPlayer.create(this, R.raw.embers)
        mp.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}