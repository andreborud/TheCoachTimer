package com.andreborud.thecoachtimer.ui.view

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.dpToPx
import com.andreborud.thecoachtimer.ui.adapter.RecyclerAdapter
import com.andreborud.thecoachtimer.ui.viewmodel.SessionViewModel
import com.andreborud.thecoachtimer.util.TimeLabelFormatter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_session.*

class SessionActivity : AppCompatActivity() {

    private lateinit var viewModel: SessionViewModel
    private var adapter = RecyclerAdapter(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        // Setup recyclerview with RecyclerAdapter
        recyclerView.apply {
            adapter = this@SessionActivity.adapter
            layoutManager = LinearLayoutManager(this@SessionActivity)
            setHasFixedSize(true)
        }

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Setup viewModel and start observing live values
        viewModel = ViewModelProvider(this).get(SessionViewModel::class.java)
        viewModel.apply {
            setup(intent.getParcelableExtra("player")!!, intent.getStringExtra("distance")!!.toInt())

            toolbar.title = player.name.fullName
            Glide.with(App.context)
                .load(player.picture.thumbnail)
                .apply(RequestOptions().circleCrop())
                .into(profileImage)

            time.observe(this@SessionActivity, Observer {
                timeView.text = it
            })

            lapTimes.observe(this@SessionActivity, Observer {
                adapter.submitList(it)

                // add a slight delay for scrolling to the top as if its called straight away the list isn't ready
                recyclerView.postDelayed( {
                    recyclerView.layoutManager?.smoothScrollToPosition(recyclerView, null, 0)
                }, 200)
            })

            averageSpeed.observe(this@SessionActivity, Observer {
                setHtmlInTextView(getString(R.string.averageSpeed, it), averageSpeedView)
            })

            averageTime.observe(this@SessionActivity, Observer {
                setHtmlInTextView(getString(R.string.averageTime, it), averageTimeView)
            })

            averageTimeLong.observe(this@SessionActivity, Observer {
                setAverageTime(it)
            })

            timeVariation.observe(this@SessionActivity, Observer {
                setHtmlInTextView(getString(R.string.timeVariance, it), timeVarianceView)
            })

            cadence.observe(this@SessionActivity, Observer {
                setHtmlInTextView(getString(R.string.cadence, it), cadanceView)
            })

            chartLapTimes.observe(this@SessionActivity, Observer {
                setChartData(it)
                chart.invalidate()
            })
        }
        lifecycle.addObserver(viewModel)

        setupChart()
    }

    override fun onBackPressed() {
        viewModel.saveSession()
        super.onBackPressed()
    }

    // Handle start and stop clicks
    fun startStop(view: View) {
        val fab = view as FloatingActionButton
        if (fab.tag != null && fab.tag as String == "Started") {
            viewModel.stop()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_black_24dp,App.context.theme))
            } else {
                @Suppress("DEPRECATION")
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_black_24dp))
            }
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green))
            lapButton.visibility = View.GONE
            fab.tag = ""
        } else {
            viewModel.start()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_stop_black_24dp, App.context.theme))
            } else {
                @Suppress("DEPRECATION")
                fab.setImageDrawable(resources.getDrawable(R.drawable.ic_stop_black_24dp))
            }
            fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red))
            lapButton.visibility = View.VISIBLE
            fab.tag = "Started"
        }
    }

    /**
     * add new lap
     */
    @Suppress("UNUSED_PARAMETER")
    fun lap(view: View) {
        viewModel.lap()
    }

    /**
     * style text using html, for styling see strings.xml
     */
    @Suppress("DEPRECATION")
    private fun setHtmlInTextView(htmlString: String, textView: TextView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT)
        } else {
            textView.text = Html.fromHtml(htmlString)
        }
    }

    /**
     * Setup look and feel of chart
     */
    private fun setupChart() {
        chart.setViewPortOffsets(dpToPx(14).toFloat(), 0f,  dpToPx(14).toFloat(), dpToPx(24).toFloat())
        chart.setBackgroundColor(Color.TRANSPARENT)
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(true)
        val x = chart.xAxis
        x.textColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        x.position = XAxis.XAxisPosition.BOTTOM
        x.setDrawGridLines(true)
        x.axisLineColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val y = chart.axisLeft
        y.setLabelCount(3, false)
        y.textColor = ContextCompat.getColor(this, R.color.colorPrimary)
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        y.setDrawGridLines(false)
        y.axisLineColor = ContextCompat.getColor(this, R.color.colorPrimary)
        y.valueFormatter = TimeLabelFormatter()
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false
    }

    /**
     * set data of chart
     */
    private fun setChartData(values: List<Entry>) {
        val x = chart.xAxis
        x.setLabelCount(values.size - 1, false)
        val set1: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "DataSet 1")
            set1.mode = LineDataSet.Mode.CUBIC_BEZIER
            set1.cubicIntensity = 0.15f
            set1.setDrawCircles(true)
            set1.lineWidth = 1.8f
            set1.circleRadius = 3f
            set1.setCircleColor(ContextCompat.getColor(this, R.color.colorPrimary))
            set1.color = ContextCompat.getColor(this, R.color.colorAccent)
            val data = LineData(set1)
            data.setValueTextSize(9f)
            data.setDrawValues(false)
            chart.data = data
        }
    }

    /**
     * set the average lap time line in the chart
     */
    private fun setAverageTime(time: Long) {
        val ll1 = LimitLine(time.toFloat(), "Average Lap Time")
        ll1.lineWidth = 1f
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f

        val yAxis = chart.axisLeft
        yAxis.removeAllLimitLines()
        yAxis.addLimitLine(ll1)
    }
}
