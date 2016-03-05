package y1row.android.app.accelerometersample

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val accelerometerSensor by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    val lineChart by lazy {
        findViewById(R.id.line_chart) as LineChart;
    }

    val lineX = ArrayList<Entry>()
    val lineY = ArrayList<Entry>()
    val lineZ = ArrayList<Entry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dataSetX = LineDataSet(lineX, "X")
        dataSetX.axisDependency = YAxis.AxisDependency.LEFT
        dataSetX.fillColor = Color.RED
        dataSetX.color = Color.RED
        dataSetX.setDrawCircles(false)

        val dataSetY = LineDataSet(lineY, "Y")
        dataSetY.axisDependency = YAxis.AxisDependency.LEFT
        dataSetY.fillColor = Color.BLUE
        dataSetY.color = Color.BLUE
        dataSetY.setDrawCircles(false)

        val dataSetZ = LineDataSet(lineZ, "Z")
        dataSetZ.axisDependency = YAxis.AxisDependency.LEFT
        dataSetZ.fillColor = Color.GREEN
        dataSetZ.color = Color.GREEN
        dataSetZ.setDrawCircles(false)

        lineChart.data = LineData()
        lineChart.data.addDataSet(dataSetX)
        lineChart.data.addDataSet(dataSetY)
        lineChart.data.addDataSet(dataSetZ)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {

        if (p0 == null) return

        when (p0.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]

                //Timber.d("onSensorChanged : x[${x}] y[${y}] z[${z}]")
                lineChart.run {
                    data.addXValue(System.currentTimeMillis().toString())

                    val xCount = data.getDataSetByIndex(0).entryCount
                    data.addEntry(Entry(x, xCount), 0)

                    val yCount = data.getDataSetByIndex(1).entryCount
                    data.addEntry(Entry(y, yCount), 1)

                    val zCount = data.getDataSetByIndex(2).entryCount
                    data.addEntry(Entry(z, zCount), 2)

                    notifyDataSetChanged()
                    setVisibleXRangeMaximum(200f)
                    moveViewToX(data.xValCount - 201f)

                    invalidate()
                }

            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}
