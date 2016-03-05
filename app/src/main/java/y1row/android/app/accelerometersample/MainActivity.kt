package y1row.android.app.accelerometersample

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import timber.log.Timber

class MainActivity : AppCompatActivity(), SensorEventListener {

    val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val lineChart by lazy {
        findViewById(R.id.line_chart) as LineChart;
    }
    val incrementLineChart by lazy {
        findViewById(R.id.line_chart_2) as LineChart;
    }

    val originData = AccelerometerData()
    val incrementAcceleration = AccelerometerData() // 瞬間的な増分データ

    var coordinate: Coordinate? = null
    var calcTime: Long = 0L;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart.run {
            data = LineData()
            data.addDataSet(originData.dataSetX)
            data.addDataSet(originData.dataSetY)
            data.addDataSet(originData.dataSetZ)
        }
        incrementLineChart.run {
            data = LineData()
            data.addDataSet(incrementAcceleration.dataSetX)
            data.addDataSet(incrementAcceleration.dataSetY)
            data.addDataSet(incrementAcceleration.dataSetZ)
        }
    }

    override fun onResume() {
        super.onResume()
        // 加速度センサー
        sensorManager.registerListener(this
                , sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                , SensorManager.SENSOR_DELAY_NORMAL)
        // 地磁気センサー
        sensorManager.registerListener(this
                , sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
                , SensorManager.SENSOR_DELAY_NORMAL)
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
                originData.run {
                    addValues(p0.values.toTypedArray())

                    lineX.add(Entry(lowPassX, dataSetX.entryCount))
                    lineY.add(Entry(lowPassY, dataSetY.entryCount))
                    lineZ.add(Entry(lowPassZ, dataSetZ.entryCount))
                }
                incrementAcceleration.run {
                    addValues(p0.values.toTypedArray())

                    lineX.add(Entry(rawAX, dataSetX.entryCount))
                    lineY.add(Entry(rawAY, dataSetY.entryCount))
                    lineZ.add(Entry(rawAZ, dataSetZ.entryCount))
                }
                invalidateChart(lineChart)
                invalidateChart(incrementLineChart)

                val time: Long = System.currentTimeMillis()
                val interval: Long = time - calcTime
                calcTime = time;

                if (time == interval) return // 初回なら抜ける

                coordinate?.let {
                    it.calc(incrementAcceleration, interval)
                    Timber.d("coordinate : ${it.toString()}")
                }
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                coordinate = Coordinate(p0.values)
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
