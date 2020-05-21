package com.andreborud.thecoachtimer.ui.view

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.interfaces.ItemClickListener
import com.andreborud.thecoachtimer.data.model.Player
import com.andreborud.thecoachtimer.dpToPx
import com.andreborud.thecoachtimer.ui.adapter.RecyclerAdapter
import com.andreborud.thecoachtimer.ui.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var viewModel: MainViewModel
    private var adapter = RecyclerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup recyclerview with RecyclerAdapter
        recyclerView.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        // Setup viewModel and start observing live values
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.apply {
            loadPlayers()
            players.observe(this@MainActivity, Observer {
                adapter.submitList(it)
            })
        }
        lifecycle.addObserver(viewModel)

        // Listen to toolbar menu click
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.leaderboard -> {
                    val intent = Intent(this, LeaderboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.files -> {
                    val intent = Intent(this, FilesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.morePlayers -> {
                    viewModel.loadPlayersFromServer()
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Show dialog to let coach set the length of any lap before starting the SessionActivity
     */
    override fun onItemClick(item: Any) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
        alertDialog.apply {
            val input = EditText(this@MainActivity).apply {
                hint = "25"
                inputType = InputType.TYPE_CLASS_PHONE
                gravity = Gravity.CENTER
                val frameLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                frameLayoutParams.marginEnd = dpToPx(32)
                frameLayoutParams.marginStart = dpToPx(32)
                layoutParams = frameLayoutParams
            }
            setTitle("New Session")
            setMessage("Lap distance in meters:")
            setView(input)
            setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel") { dialog, _ -> dialog.dismiss() }
            setButton(AlertDialog.BUTTON_POSITIVE, "Start") { dialog, _ ->
                val intent = Intent(this@MainActivity, SessionActivity::class.java)

                // Send player and lap distance with the intent
                intent.putExtra("player", item as Player)
                intent.putExtra("distance", if (input.text.toString().isNotEmpty()) input.text.toString() else "25")
                startActivity(intent)
                dialog.dismiss()
            }
            show()
        }
    }
}
