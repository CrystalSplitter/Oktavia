package oktavia

import kotlin.math.PI
import kotlin.math.sin
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

fun main(args: Array<String>) {
    if (args.size > 0) {
        readSoundFile(args[0])
    } else {
        readSoundFile("/home/crystal/Documents/wip_one_channel.wav")
    }

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
    val pipeline = Pipeline().apply {
        val source = stage(AudioSource(stream))
        val filter = stage(
                FIRFilter(Array(50) { 1.0f/50.0f }.toList(), inputConnection = source.output)
        )
        stage(SignalPlayer(inputConnection = filter.output))
        stage(SignalPlayer(inputConnection = source.output))
    }
    pipeline.run()
}

fun arrayToCSV(array: List<Any?>): String {
    var output = ""
    for (i in 0 until array.size) {
        output += "${array[i].toString()},\n"
    }
    return output
}