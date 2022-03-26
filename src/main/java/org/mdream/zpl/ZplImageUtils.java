package org.mdream.zpl;

import javax.inject.Singleton;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

@Singleton
public class ZplImageUtils {

    public BufferedImage resizeImg(BufferedImage image, int width, int height) {
        BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g = resizedImg.createGraphics();
        AffineTransform at = AffineTransform
                .getScaleInstance(width / (double) image.getWidth(), height / (double) image.getHeight());
        g.drawRenderedImage(image, at);
        return resizedImg;
    }

    public String imageToAscii(BufferedImage image) {
        StringBuilder sb = new StringBuilder();
        Graphics2D graphics = image.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        int height = image.getHeight();
        int width = image.getWidth();
        int rgb, red, green, blue, index = 0;
        char auxBinaryChar[] = {'0', '0', '0', '0', '0', '0', '0', '0'};
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                rgb = image.getRGB(w, h);
                red = (rgb >> 16) & 0x000000FF;
                green = (rgb >> 8) & 0x000000FF;
                blue = (rgb) & 0x000000FF;
                char auxChar = '1';
                int totalColor = red + green + blue;
                if (totalColor > 768 * 0.5) {
                    auxChar = '0';
                }
                auxBinaryChar[index] = auxChar;
                index++;
                if (index == 8 || w == (width - 1)) {
                    sb.append(fourByteBinary(new String(auxBinaryChar)));
                    auxBinaryChar = new char[]{'0', '0', '0', '0', '0', '0', '0', '0'};
                    index = 0;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * @param width ширина картинки в px
     * @return размер пикселей в байтах
     */
    public int getWidthBytes(int width) {
        return width % 8 > 0 ? width / 8 + 1 : width / 8;
    }

    /**
     * Дополняет нулем если длина 15
     *
     * @param binaryStr строка
     * @return дополненная строка
     */
    private String fourByteBinary(String binaryStr) {
        int decimal = Integer.parseInt(binaryStr, 2);
        if (decimal > 15) {
            return Integer.toString(decimal, 16).toUpperCase();
        } else {
            return "0" + Integer.toString(decimal, 16).toUpperCase();
        }
    }

    /**
     * Поворот изображения на указанный угол.
     * Ничего не делает если угол = 0;
     */
    public BufferedImage rotateImage(final BufferedImage bufferedimage,
                                            final int angle) {
        int width = bufferedimage.getWidth();
        int height = bufferedimage.getHeight();

        BufferedImage dstImage;
        AffineTransform affineTransform = new AffineTransform();

        if (angle == 0) {
            return bufferedimage;
        } else if (angle == 90) {
            affineTransform.translate(height, 0);
            dstImage = new BufferedImage(height, width, bufferedimage.getType());
        } else if (angle == 180) {
            affineTransform.translate(width, height);
            dstImage = new BufferedImage(width, height, bufferedimage.getType());
        } else if (angle == 270) {
            affineTransform.translate(0, width);
            dstImage = new BufferedImage(height, width, bufferedimage.getType());
        } else {
            throw new IllegalArgumentException("недопустимое значение: " + angle);
        }

        affineTransform.rotate(Math.toRadians(angle));
        AffineTransformOp affineTransformOp = new AffineTransformOp(
                affineTransform,
                AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return affineTransformOp.filter(bufferedimage, dstImage);
    }

    /**
     * преобразование изображения в монохромное с инвертированием цвета.
     */
    public BufferedImage toMonoAndInvertColour(BufferedImage image) {
        BufferedImage imageOut = image;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgba = image.getRGB(i, j);
                imageOut.setRGB(i, j, toMonoAndInvertPixel(rgba));
            }
        }
        return imageOut;
    }

    private int toMonoAndInvertPixel(int rgba) {
        Color col = new Color(rgba, true);
        int MONO_THRESHOLD = 368;
        if (col.getRed() + col.getGreen() + col.getBlue() > MONO_THRESHOLD)
            col = Color.BLACK;
        else
            col = Color.WHITE;
        return col.getRGB();
    }

}

