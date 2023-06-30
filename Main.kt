package watermark

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

fun main() {

    try {
        val image: BufferedImage = getImage()
        val watermark: BufferedImage = getWatermark()
        if (image.width < watermark.width || image.height < watermark.height) throw RuntimeException("The watermark's dimensions are larger.")
        saveWatermarkedImage(getWatermarkedImage(image, watermark))
    } catch (e: RuntimeException) {
        println(e.message)
    }
}

private fun getWatermarkedImage(image: BufferedImage, watermark: BufferedImage): BufferedImage {
    val alpha = getAlpha(watermark)
    val transparencyColor = getTransparencyColor(watermark)
    val weight = getWeight()
    val watermarkPosition = getWatermarkPosition(image, watermark)
    return createWatermarkedImage(image, watermark, weight, alpha, transparencyColor, watermarkPosition)
}

private fun getWatermarkPosition(image: BufferedImage, watermark: BufferedImage): Position? {
    println("Choose the position method (single, grid):")
    return when (PositionMethods.getPositionMethod(readln())) {
        PositionMethods.SINGLE -> getSinglePosition(image, watermark)
        PositionMethods.GRID -> null
    }
}

private fun getSinglePosition(image: BufferedImage, watermark: BufferedImage): Position {
    val diffX = image.width - watermark.width
    val diffY = image.height - watermark.height
    println("Input the watermark position ([x 0-$diffX] [y 0-$diffY]):")
    val input = readln()
    if (!input.matches(Regex("-?\\d+ -?\\d+"))) throw RuntimeException("The position input is invalid.")
    val (x, y) = input.split(" ").map { it.toInt() }
    if (x !in 0..diffX || y !in 0..diffY) throw RuntimeException("The position input is out of range.")
    return Position(x, y)
}

private fun saveWatermarkedImage(watermarkedImage: BufferedImage) {
    println("Input the output image filename (jpg or png extension):")
    val (outputFileName, outputFileExtension) = readln().split(".")
    if (!outputFileExtension.matches(Regex("jpg|png"))) throw RuntimeException("The output file extension isn't \"jpg\" or \"png\".")

    ImageIO.write(watermarkedImage, outputFileExtension, File("$outputFileName.$outputFileExtension"))
    println("The watermarked image $outputFileName.$outputFileExtension has been created.")
}

private fun getWeight(): Int {
    println("Input the watermark transparency percentage (Integer 0-100):")
    val input = readln()
    if (!input.matches(Regex("\\d+"))) throw RuntimeException("The transparency percentage isn't an integer number.")

    val weight = input.toInt()
    if (weight !in 0..100) throw RuntimeException("The transparency percentage is out of range.")
    return weight
}

private fun getWatermark(): BufferedImage {
    println("Input the watermark image filename:")
    val watermark: BufferedImage = readImage(readln())
    if (isNotThreeColorComponents(watermark)) throw RuntimeException("The number of watermark color components isn't 3.")
    if (isNot24or32bit(watermark)) throw RuntimeException("The watermark isn't 24 or 32-bit.")
    return watermark
}

private fun getImage(): BufferedImage {
    println("Input the image filename:")
    val image: BufferedImage = readImage(readln())
    if (isNotThreeColorComponents(image)) throw RuntimeException("The number of image color components isn't 3.")
    if (isNot24or32bit(image)) throw RuntimeException("The image isn't 24 or 32-bit.")
    return image
}

private fun getTransparencyColor(watermark: BufferedImage) = if (watermark.transparency != 3) {
    println("Do you want to set a transparency color?")
    if (readln().lowercase() == "yes") {
        println("Input a transparency color ([Red] [Green] [Blue]):")
        getColor()
    } else null
} else null

private fun getAlpha(watermark: BufferedImage) = if (watermark.transparency == 3) {
    println("Do you want to use the watermark's Alpha channel?")
    readln().lowercase() == "yes"
} else false

private fun getColor(): Color {
    val input = readln()
    if (!input.matches(Regex("\\d+ \\d+ \\d+"))) throw RuntimeException("The transparency color input is invalid.")
    val (r, g, b) = input.split(" ").map { it.toInt() }
    if (r !in 0..255 || g !in 0..255 || b !in 0..255) throw RuntimeException("The transparency color input is invalid.")
    return Color(r, g, b)
}

private fun createWatermarkedImage(
    image: BufferedImage,
    watermark: BufferedImage,
    weight: Int,
    alpha: Boolean = false,
    transparencyColor: Color?,
    watermarkPosition: Position?
): BufferedImage {
    val watermarkedImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (x in 0 until watermarkedImage.width) {
        for (y in 0 until watermarkedImage.height) {
            val imageColor = Color(image.getRGB(x, y))
            val watermarkColor = getWatermarkColor(watermark, watermarkPosition, x, y, alpha)
            val currentColor = getCurrentColor(watermarkColor, transparencyColor, imageColor, weight, image, x, y)
            watermarkedImage.setRGB(x, y, currentColor.rgb)
        }
    }
    return watermarkedImage
}

private fun getCurrentColor(
    watermarkColor: Color,
    transparencyColor: Color?,
    imageColor: Color,
    weight: Int,
    image: BufferedImage,
    x: Int,
    y: Int
) = if (watermarkColor.alpha == 0 || watermarkColor == transparencyColor) imageColor else Color(
    (weight * watermarkColor.red + (100 - weight) * Color(image.getRGB(x, y)).red) / 100,
    (weight * watermarkColor.green + (100 - weight) * Color(image.getRGB(x, y)).green) / 100,
    (weight * watermarkColor.blue + (100 - weight) * Color(image.getRGB(x, y)).blue) / 100
)

private fun getWatermarkColor(watermark: BufferedImage, watermarkPosition: Position?, x: Int, y: Int, alpha: Boolean) =
    if (watermarkPosition == null) getWatermarkColorGrid(watermark, Position(x, y), alpha)
    else getWatermarkColorPosition(watermark, Position(x, y), watermarkPosition, alpha)

private fun getWatermarkColorGrid(watermark: BufferedImage, currentPixelPosition: Position, alpha: Boolean) =
    Color(watermark.getRGB(currentPixelPosition.x % watermark.width, currentPixelPosition.y % watermark.height), alpha)

private fun getWatermarkColorPosition(
    watermark: BufferedImage,
    currentPixelPosition: Position,
    watermarkPosition: Position,
    alpha: Boolean
): Color {
    if (currentPixelPosition.x !in watermarkPosition.x until watermarkPosition.x + watermark.width ||
        currentPixelPosition.y !in watermarkPosition.y until watermarkPosition.y + watermark.height
    ) return Color(0, 0, 0, 0)

    return Color(
        watermark.getRGB(
            currentPixelPosition.x - watermarkPosition.x,
            currentPixelPosition.y - watermarkPosition.y
        ), alpha
    )
}

private fun readImage(file: String): BufferedImage {
    return try {
        ImageIO.read(File(file))
    } catch (e: IOException) {
        throw RuntimeException("The file $file doesn't exist.")
    }
}

private fun isNotThreeColorComponents(bufferedImage: BufferedImage) = bufferedImage.colorModel.numColorComponents != 3

private fun isNot24or32bit(bufferedImage: BufferedImage) =
    bufferedImage.colorModel.pixelSize != 24 && bufferedImage.colorModel.pixelSize != 32

enum class PositionMethods(val string: String) {
    SINGLE("single"), GRID("grid");

    companion object {
        fun getPositionMethod(input: String): PositionMethods {
            for (positionMethod in PositionMethods.values()) {
                if (positionMethod.string == input.lowercase()) return positionMethod
            }
            throw RuntimeException("The position method input is invalid.")
        }
    }
}

data class Position(val x: Int, val y: Int)