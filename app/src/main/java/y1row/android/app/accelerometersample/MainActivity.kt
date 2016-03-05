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

    val MIN_THRESHOLD = 0.2

    val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val lineChart by lazy {
        findViewById(R.id.line_chart) as LineChart;
    }
    val incrementLineChart by lazy {
        findViewById(R.id.line_chart_2) as LineChart;
    }

    val data1 = AccelerometerData()
    val data2 = AccelerometerData() // 瞬間的な増分データ

    var coordinate: Coordinate? = null
    var calcTime: Long = 0L;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart.run {
            data = LineData()
            data.addDataSet(data1.dataSetX)
            data.addDataSet(data1.dataSetY)
            data.addDataSet(data1.dataSetZ)
        }
        incrementLineChart.run {
            data = LineData()
            data.addDataSet(data2.dataSetX)
            data.addDataSet(data2.dataSetY)
            data.addDataSet(data2.dataSetZ)
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
                data1.run {
                    addValues(p0.values.toTypedArray())

                    lineX.add(Entry(rawAX, dataSetX.entryCount))
                    lineY.add(Entry(rawAY, dataSetY.entryCount))
                    lineZ.add(Entry(rawAZ, dataSetZ.entryCount))
                }
                if (data1.rawAX > MIN_THRESHOLD
                        || data1.rawAY > MIN_THRESHOLD
                        || data1.rawAZ > MIN_THRESHOLD) {

                    val time: Long = System.currentTimeMillis()
                    val interval: Long = time - calcTime
                    calcTime = time;

                    if (time == interval) return // 初回なら抜ける

                    coordinate?.let {
                        it.calc(data2, interval)
                        Timber.d("coordinate : ${it.toString()}")
                    }
                }

                data2.run {
                    addValues(p0.values.toTypedArray())

                    if (coordinate != null) {
                        lineX.add(Entry(coordinate?.x!!.toFloat(), dataSetX.entryCount))
                        lineY.add(Entry(coordinate?.y!!.toFloat(), dataSetY.entryCount))
                        lineZ.add(Entry(coordinate?.z!!.toFloat(), dataSetZ.entryCount))
                    } else {
                        lineX.add(Entry(0f, dataSetX.entryCount))
                        lineY.add(Entry(0f, dataSetY.entryCount))
                        lineZ.add(Entry(0f, dataSetZ.entryCount))
                    }
                }

                invalidateChart(lineChart)
                invalidateChart(incrementLineChart)
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
