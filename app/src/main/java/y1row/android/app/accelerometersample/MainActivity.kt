package y1row.android.app.accelerometersample

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData

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
    val filterdLineChart by lazy {
        findViewById(R.id.line_chart_2) as LineChart;
    }

    val originData = AccelerometerData()
    val filterdData = AccelerometerData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart.run {
            data = LineData()
            data.addDataSet(originData.dataSetX)
            data.addDataSet(originData.dataSetY)
            data.addDataSet(originData.dataSetZ)
        }
        filterdLineChart.run {
            data = LineData()
            data.addDataSet(filterdData.dataSetX)
            data.addDataSet(filterdData.dataSetY)
            data.addDataSet(filterdData.dataSetZ)
        }
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
                //Timber.d("onSensorChanged : x[${x}] y[${y}] z[${z}]")

                originData.addValues(p0.values.toTypedArray(), false)
                filterdData.addValues(p0.values.toTypedArray())

                invalidateChart(lineChart)
                invalidateChart(filterdLineChart)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    fun invalidateChart(chart: LineChart) {
        chart.run {
            data.addXValue(System.currentTimeMillis().toString())

            notifyDataSetChanged()
            setVisibleXRangeMaximum(200f)
            moveViewToX(data.xValCount - 201f)

            invalidate()
        }
    }

}
