package com.kneeson.mobiledamalegends

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import com.kneeson.mobiledamalegends.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var theme_index = 0
    val theme_list  = mutableListOf(
        mapOf(
            "name"  to "Cheese Cake",
            "board" to R.drawable.board_1,
            "white" to R.drawable.piece_light_1,
            "black" to R.drawable.piece_dark_1,
            "hiLit" to R.drawable.highlight,
        ),
        mapOf(
            "name"  to "Blueberry",
            "board" to R.drawable.board_2,
            "white" to R.drawable.piece_light_2,
            "black" to R.drawable.piece_dark_2,
            "hiLit" to R.drawable.highlight
        )
    )

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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