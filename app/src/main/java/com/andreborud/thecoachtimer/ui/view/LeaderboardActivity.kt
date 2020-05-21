package com.andreborud.thecoachtimer.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.ui.adapter.RecyclerAdapter
import com.andreborud.thecoachtimer.ui.viewmodel.LeaderboardViewModel
import kotlinx.android.synthetic.main.activity_leaderboard.*
import kotlinx.android.synthetic.main.activity_main.recyclerView

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var viewModel: LeaderboardViewModel
    private var adapter = RecyclerAdapter(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        toolbar.title = "Leaderboard"
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Setup recyclerview with RecyclerAdapter
        recyclerView.apply {
            adapter = this@LeaderboardActivity.adapter
            layoutManager = LinearLayoutManager(this@LeaderboardActivity)
            setHasFixedSize(true)
        }

        // Setup viewModel and start observing live values
        viewModel = ViewModelProvider(this).get(LeaderboardViewModel::class.java)
        viewModel.apply {
            loadLeaderboard()
            leaderboardItems.observe(this@LeaderboardActivity, Observer {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })
        }
        lifecycle.addObserver(viewModel)

        // Listen to toolbar menu click
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.exportCsv -> {
                    viewModel.exportToCSV()
                    true
                }
                R.id.explosiveness -> {
                    adapter.sortedBy = LeaderboardViewModel.Performance.Explosiveness
                    viewModel.sortListBy(LeaderboardViewModel.Performance.Explosiveness)
                    true
                }
                R.id.endurance -> {
                    adapter.sortedBy = LeaderboardViewModel.Performance.Endurance
                    viewModel.sortListBy(LeaderboardViewModel.Performance.Endurance)
                    true
                }
                else -> false
            }
        }
    }
}
