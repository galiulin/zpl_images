package org.mdream.zpl;

import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.image.BufferedImage;

@ApplicationScoped
public class NoneCompressor implements Compressor {
    private static final String ZPL_PATTERN = "^GFA,%d,%d,%s,%s^FS%n";

    @Inject
    ZplImageUtils zplImageUtils;

    @Override
    public CompressorType getCompressorType() {
        return CompressorType.ASCII_NONE;
    }

    @Override
    @SneakyThrows
    public String toZpl(BufferedImage image) {
        String asciiImage = zplImageUtils.imageToAscii(image);
        int rowBytes = zplImageUtils.getWidthBytes(image.getWidth());
        int bytes = rowBytes * image.getHeight();
        return String.format(ZPL_PATTERN, bytes, bytes, rowBytes, asciiImage);
    }
}
