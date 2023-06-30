package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun main() {

    try {
        println("Input the image filename:")
        val image: BufferedImage = readImage(readln())
        if (isNotThreeColorComponents(image)) throw RuntimeException("The number of image color components isn't 3.")
        if (isNot24or32bit(image)) throw RuntimeException("The image isn't 24 or 32-bit.")

        println("Input the watermark image filename:")
        val watermark: BufferedImage = readImage(readln())
        if (isNotThreeColorComponents(watermark)) throw RuntimeException("The number of watermark color components isn't 3.")
        if (isNot24or32bit(watermark)) throw RuntimeException("The watermark isn't 24 or 32-bit.")

        if (image.width != watermark.width || image.height != watermark.height) throw RuntimeException("The image and watermark dimensions are different.")

        val alpha: Boolean = if (watermark.transparency == 3) {
            println("Do you want to use the watermark's Alpha channel?")
            readln().lowercase() == "yes"
        } else false

        val transparencyColor: Color? = if (watermark.transparency != 3) {
            println("Do you want to set a transparency color?")
            if (readln().lowercase() == "yes") {
                println("Input a transparency color ([Red] [Green] [Blue]):")
                getColor()
            } else null
        } else null

        println("Input the watermark transparency percentage (Integer 0-100):")
        val input = readln()
        if (!input.matches(Regex("\\d+"))) throw RuntimeException("The transparency percentage isn't an integer number.")

        val weight = input.toInt()
        if (weight !in 0..100) throw RuntimeException("The transparency percentage is out of range.")

        println("Input the output image filename (jpg or png extension):")
        val (outputFileName, outputFileExtension) = readln().split(".")
        if (!outputFileExtension.matches(Regex("jpg|png"))) throw RuntimeException("The output file extension isn't \"jpg\" or \"png\".")

        val outputImage: BufferedImage = getWatermarkedImage(image, watermark, weight, alpha, transparencyColor)

        ImageIO.write(outputImage, outputFileExtension, File("$outputFileName.$outputFileExtension"))
        println("The watermarked image $outputFileName.$outputFileExtension has been created.")

    } catch (e: RuntimeException) {
        println(e.message)
    }
}

private fun getColor(): Color {
    val input = readln()
    if (!input.matches(Regex("\\d+ \\d+ \\d+"))) throw RuntimeException("The transparency color input is invalid.")
    val (rString, gString, bString) = input.split(" ")
    val r = rString.toInt()
    val g = gString.toInt()
    val b = bString.toInt()
    if (r !in 0..255 || g !in 0..255 || b !in 0..255) throw RuntimeException("The transparency color input is invalid.")
    return Color(r, g, b)
}

private fun getWatermarkedImage(image: BufferedImage, watermark: BufferedImage, weight: Int, alpha: Boolean = false, transparencyColor: Color?): BufferedImage {
    val watermarkedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (x in 0 until watermarkedImage.width) {
        for (y in 0 until watermarkedImage.height) {
            val watermarkColor = Color(watermark.getRGB(x, y), alpha)
            val imageColor = Color(image.getRGB(x, y))
            val color = if (watermarkColor.alpha == 0 || watermarkColor == transparencyColor) imageColor else Color(
                (weight * watermarkColor.red + (100 - weight) * Color(image.getRGB(x, y)).red) / 100,
                (weight * watermarkColor.green + (100 - weight) * Color(image.getRGB(x, y)).green) / 100,
                (weight * watermarkColor.blue + (100 - weight) * Color(image.getRGB(x, y)).blue) / 100
            )
            watermarkedImage.setRGB(x, y, color.rgb)
        }
    }
    return watermarkedImage
}

private fun readImage(file: String): BufferedImage {
    return try {
        ImageIO.read(File(file))
    } catch (e: IOException) {
        throw RuntimeException("The file $file doesn't exist.")
    }
}

private fun isNotThreeColorComponents(bufferedImage: BufferedImage) = bufferedImage.colorModel.numColorComponents != 3

private fun isNot24or32bit(bufferedImage: BufferedImage) = bufferedImage.colorModel.pixelSize != 24 && bufferedImage.colorModel.pixelSize != 32
