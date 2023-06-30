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

        println("Input the watermark transparency percentage (Integer 0-100):")
        val input = readln()
        if (!input.matches(Regex("\\d+"))) throw RuntimeException("The transparency percentage isn't an integer number.")

        val weight = input.toInt()
        if (weight !in 0..100) throw RuntimeException("The transparency percentage is out of range.")

        println("Input the output image filename (jpg or png extension):")
        val (outputFileName, outputFileExtension) = readln().split(".")
        if (!outputFileExtension.matches(Regex("jpg|png"))) throw RuntimeException("The output file extension isn't \"jpg\" or \"png\".")

        val outputImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
        for (i in 0 until outputImage.width) {
            for (j in 0 until outputImage.height) {
                val color = Color(
                    (weight * Color(watermark.getRGB(i, j)).red + (100 - weight) * Color(image.getRGB(i, j)).red) / 100,
                    (weight * Color(watermark.getRGB(i, j)).green + (100 - weight) * Color(image.getRGB(i, j)).green) / 100,
                    (weight * Color(watermark.getRGB(i, j)).blue + (100 - weight) * Color(image.getRGB(i, j)).blue) / 100
                )
                outputImage.setRGB(i, j, color.rgb)
            }
        }

        ImageIO.write(outputImage, outputFileExtension, File("$outputFileName.$outputFileExtension"))
        println("The watermarked image $outputFileName.$outputFileExtension has been created.")

    } catch (e: RuntimeException) {
        println(e.message)
    }
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

