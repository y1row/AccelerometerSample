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

    var isFirst: Boolean = true
    val MOD_FILTER = 0.1f
    var lowPassX: Float = 0.0f
    var lowPassY: Float = 0.0f
    var lowPassZ: Float = 0.0f

    // 瞬間的な加速度の増分
    var rawAX: Float = 0.0f
    var rawAY: Float = 0.0f
    var rawAZ: Float = 0.0f

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

        // Low Pass Filter
        if (isFirst.not() && enableFilter) {
            lowPassX += (x - lowPassX) * MOD_FILTER
            lowPassY += (y - lowPassY) * MOD_FILTER
            lowPassZ += (z - lowPassZ) * MOD_FILTER
        } else {
            lowPassX = x
            lowPassY = y
            lowPassZ = z
        }
        isFirst = false

        rawAX = x - lowPassX
        rawAY = y - lowPassY
        rawAZ = z - lowPassZ
    }

}
