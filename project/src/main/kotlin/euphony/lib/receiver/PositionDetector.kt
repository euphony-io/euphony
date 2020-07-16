package euphony.lib.receiver

interface PositionDetector {
    fun detectSignal(signal: Int)
    fun detectFq(fq: Int)
}