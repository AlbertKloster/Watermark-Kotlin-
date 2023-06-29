package watermark

import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun main() {
    println("Input the image filename:")
    val file = readln()
    try {
        val image: BufferedImage = ImageIO.read(File(file))
        println("Image file: $file")
        println("Width: ${image.width}")
        println("Height: ${image.height}")
        println("Number of components: ${image.colorModel.numComponents}")
        println("Number of color components: ${image.colorModel.numColorComponents}")
        println("Bits per pixel: ${image.colorModel.pixelSize}")
        println("Transparency: ${image.transparency()}")
    } catch (e: IOException) {
        println("The file $file doesn't exist.")
    }

}

private fun BufferedImage.transparency(): String {
    return when (transparency) {
        Transparency.BITMASK -> "BITMASK"
        Transparency.OPAQUE -> "OPAQUE"
        Transparency.TRANSLUCENT -> "TRANSLUCENT"
        else -> "UNKNOWN"
    }
}