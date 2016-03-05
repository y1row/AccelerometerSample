package y1row.android.app.accelerometersample

import android.hardware.SensorManager

/**
 * 座標系
 */
class Coordinate(gravity: FloatArray) {

    var azimuth: Double = 0.0
    var pitch: Double = 0.0
    var roll: Double = 0.0

    var nPitchRad: Double = 0.0
    var sinNPitch: Double = 0.0
    var cosNPitch: Double = 0.0

    var nRollRad: Double = 0.0
    var sinNRoll: Double = 0.0
    var cosNRoll: Double = 0.0

    var nAzimuthRad: Double = 0.0
    var sinNAzimuth: Double = 0.0
    var cosNAzimuth: Double = 0.0

    var oldTime: Long = 0L

    var ax: Double = 0.0
    var ay: Double = 0.0
    var az: Double = 0.0
    var vx: Double = 0.0
    var vy: Double = 0.0
    var vz: Double = 0.0
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0

    init {
        val rotationMatrix: FloatArray = FloatArray(9)
        val geomagnetic: FloatArray = FloatArray(3)
        val attitude: FloatArray = FloatArray(3)

        SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)
        SensorManager.getOrientation(rotationMatrix, attitude)

        azimuth = Math.toDegrees(attitude[0].toDouble())
        pitch = Math.toDegrees(attitude[1].toDouble())
        roll = Math.toDegrees(attitude[2].toDouble())

        nPitchRad = Math.toRadians(-pitch)
        sinNPitch = Math.sin(nPitchRad)
        cosNPitch = Math.cos(nPitchRad)

        nRollRad = Math.toRadians(-roll)
        sinNRoll = Math.sin(nRollRad)
        cosNRoll = Math.cos(nRollRad)

        nAzimuthRad = Math.toRadians(-azimuth);
        sinNAzimuth = Math.sin(nAzimuthRad);
        cosNAzimuth = Math.cos(nAzimuthRad);
    }

    fun calc(accData: AccelerometerData, interval: Long) {
        val bx: Double = accData.rawAX * cosNRoll + accData.rawAZ * sinNRoll
        val by: Double = accData.rawAX * sinNPitch * sinNRoll + accData.rawAY * cosNPitch - accData.rawAZ * sinNPitch * cosNRoll
        az = -accData.rawAX * cosNPitch * sinNRoll + accData.rawAY * sinNPitch * cosNRoll + accData.rawAZ * cosNPitch * cosNRoll

        ax = bx * cosNAzimuth - by * sinNAzimuth;
        ay = bx * sinNAzimuth + by * cosNAzimuth;

        vx += ax * interval / 10; // [cm/s] にする
        vy += ay * interval / 10;
        vz += az * interval / 10;
        x += vx * interval / 1000; // [cm] にする
        y += vy * interval / 1000;
        z += vz * interval / 1000;
    }

    override fun toString(): String {
        return "vx[${vx}], vy[${vy}], vz[${vz}], x[${x}], y[${y}], z[${z}]"
    }

}