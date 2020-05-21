package com.andreborud.thecoachtimer.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.ui.adapter.RecyclerAdapter
import com.andreborud.thecoachtimer.ui.viewmodel.FilesViewModel
import kotlinx.android.synthetic.main.activity_files.*

class FilesActivity : AppCompatActivity() {

    private lateinit var viewModel: FilesViewModel
    private var adapter = RecyclerAdapter(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        // Setup recyclerview with RecyclerAdapter
        recyclerView.apply {
            adapter = this@FilesActivity.adapter
            layoutManager = LinearLayoutManager(this@FilesActivity)
            setHasFixedSize(true)
        }

        toolbar.title = "Exported CSV Files"

        // Setup viewModel and start observing live values
        viewModel = ViewModelProvider(this).get(FilesViewModel::class.java)
        viewModel.apply {
            loadFiles()
            files.observe(this@FilesActivity, Observer {
                adapter.submitList(it)
            })
        }
        lifecycle.addObserver(viewModel)
    }
}
