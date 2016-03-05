package y1row.android.app.accelerometersample

import android.graphics.Color
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class AccelerometerData {

    val lineX = ArrayList<Entry>()
    val lineY = ArrayList<Entry>()
    val lineZ = ArrayList<Entry>()

    val dataSetX = LineDataSet(lineX, "X")
    val dataSetY = LineDataSet(lineY, "Y")
    val dataSetZ = LineDataSet(lineZ, "Z")

    var isFirst: Boolean = false
    var lastX: Float = 0.0f
    var lastY: Float = 0.0f
    var lastZ: Float = 0.0f

    init {
        dataSetX.fillColor = Color.RED
        dataSetX.color = Color.RED
        dataSetX.setDrawCircles(false)
        dataSetX.setDrawValues(false)

        dataSetY.fillColor = Color.BLUE
        dataSetY.color = Color.BLUE
        dataSetY.setDrawCircles(false)
        dataSetY.setDrawValues(false)

        dataSetZ.fillColor = Color.GREEN
        dataSetZ.color = Color.GREEN
        dataSetZ.setDrawCircles(false)
        dataSetZ.setDrawValues(false)
    }

    fun addValues(values: Array<Float>?, enableFilter: Boolean = true) {
        if (values == null) return
        if (values.count() < 3) return

        var x = values[0]
        var y = values[1]
        var z = values[2]

        if (isFirst.not() && enableFilter) {
            x = (0.9f * lastX + 0.1f * x)
            y = (0.9f * lastY + 0.1f * y)
            z = (0.9f * lastZ + 0.1f * z)
        }
        lastX = x
        lastY = y
        lastZ = z

        lineX.add(Entry(x, dataSetX.entryCount))
        lineY.add(Entry(y, dataSetY.entryCount))
        lineZ.add(Entry(z, dataSetZ.entryCount))
    }

}
