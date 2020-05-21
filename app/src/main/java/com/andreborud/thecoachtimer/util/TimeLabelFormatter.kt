package com.andreborud.thecoachtimer.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter

class TimeLabelFormatter: ValueFormatter() {

    // override this for e.g. LineChart or ScatterChart
    override fun getPointLabel(entry: Entry?): String {
        return Format.timeAndShorten(entry?.y!!.toLong())
    }

    // override this for BarChart
    override fun getBarLabel(barEntry: BarEntry?): String {
        return Format.timeAndShorten(barEntry?.y!!.toLong())
    }

    // override this for custom formatting of XAxis or YAxis labels
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return Format.timeAndShorten(value.toLong())
    }
}