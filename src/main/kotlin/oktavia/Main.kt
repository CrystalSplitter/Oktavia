package oktavia

import kotlin.math.PI
import kotlin.math.sin
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

fun main(args: Array<String>) {
    readSoundFile(args[0])

    /*
    val freq = args[0].toDouble()
    val sampleRate = args[1].toDouble()
    val size = args[2].toInt()
    val input = DoubleSignal(size, sampleRate) {
        sin(2.0 * PI * freq * it/sampleRate)
    }
    File("my_input.csv").writeText(arrayToCSV(input.sampleList()))

    val freqDomain = simpleDFT(input.sampleList())
    val timeDomain = invSimpleDFT(freqDomain)
    val mags = freqDomain.map { it.magnitude() }
    val reals: List<Double> = timeDomain.map { it.re }
    File("my_output.csv").writeText(arrayToCSV(mags))
    File("my_output_2.csv").writeText(arrayToCSV(reals))
     */
}

fun readSoundFile(name: String) {
    println("Looking at file: $name")
    val stream: AudioInputStream = AudioSystem.getAudioInputStream(File(name))
    //val signalGroup = SignalGroup(256, stream.format.sampleRate, stream.format.channels)
    val filter = FIRFilter(Array(50) { 2.0f/50.0f }.toList())
    filter.connectInput(stream)
    val play = PlaySignal()
    play.connectInput(filter)
    play.start()
}

fun arrayToCSV(array: List<Any?>): String {
    var output = ""
    for (i in 0 until array.size) {
        output += "${array[i].toString()},\n"
    }
    return output
}