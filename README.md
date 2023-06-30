# Stage 4/5: Transparency color
## Description
If a watermark image doesn't use the alpha channel, we can treat a specific color as a transparent one. For example, if a watermark image logo has a white background, the white color can be handled as transparent. If watermark logo pixels are of the same color as its background, treat them as transparent.

If your watermark image doesn't have the alpha channel, you will be prompted to choose a specific color as transparency.

## Objectives
- If a watermark image has the alpha channel, proceed as in the previous stage (Examples 2 and 3);
- Otherwise, before asking users for the watermark transparency percentage, output `Do you want to set a transparency color?`. If the user inputs anything else than `yes`, proceed without the use of a transparency color (Example 4);
- If the input is `yes`, ask for the transparency color with `Input a transparency color ([Red] [Green] [Blue]):`. After this, users should input 3 integer numbers, each within the `0` to `255` range, separated by space(Example 1). These are the values for the red, green, and blue colors, respectively. Any pixels in the watermark image with this color, should be treated as transparent. In case the input isn't correct, print `The transparency color input is invalid.` and terminate the program (Example 5).

If you need some image files to experiment with your code for stage 4, then you can <a href="https://stepik.org/media/attachments/lesson/623865/stage4.zip">download this zip file.</a>

## Examples
The greater-than symbol followed by a space (`> `) represents the user input. Note that it's not part of the input.

<b>Example 1:></b> <i>the watermark image doesn't employ the alpha channel</i>
```
Input the image filename:
> image.jpg
Input the watermark image filename:
> logo.jpg
Do you want to set a transparency color?
> yes
Input a transparency color ([Red] [Green] [Blue]):
> 255 255 255
Input the watermark transparency percentage (Integer 0-100):
> 20
Input the output image filename (jpg or png extension):
> out.jpg
The watermarked image out.jpg has been created.
```

<b>Example 2:</b> <i>the watermark image uses the alpha channel</i>
```
Input the image filename:
> image.jpg
Input the watermark image filename:
> logo_with_alpha.png
Do you want to use the watermark's Alpha channel?
> yes
Input the watermark transparency percentage (Integer 0-100):
> 20
Input the output image filename (jpg or png extension):
> out.png
The watermarked image out.png has been created.
```

<b>Example 3:</b> <i>the watermark image has the alpha channel, but the user doesn't want to use it</i>
```
Input the image filename:
> image.jpg
Input the watermark image filename:
> logo.png
Do you want to set a transparency color?
> no
Input the watermark transparency percentage (Integer 0-100):
> 20
Input the output image filename (jpg or png extension):
> out.jpg
The watermarked image out.jpg has been created.
```

<b>Example 4:</b> <i>the watermark image doesn't have the alpha channel; the user doesn't use a transparency color</i>
```
Input the image filename:
> image.jpg
Input the watermark image filename:
> logo.png
Do you want to set a transparency color?
> no
Input the watermark transparency percentage (Integer 0-100):
> 20
Input the output image filename (jpg or png extension):
> out.jpg
The watermarked image out.jpg has been created.
```

<b>Example 5:</b> <i>invalid transparency color</i>
```
Input the image filename:
> image.jpg
Input the watermark image filename:
> watermark.jpg
Do you want to set a transparency color?
> yes
Input a transparency color ([Red] [Green] [Blue]):
> 34 45
The transparency color input is invalid.
```
