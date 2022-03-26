package org.mdream.zpl;

import java.awt.image.BufferedImage;

public interface Compressor {
    String toZpl(BufferedImage image);

    CompressorType getCompressorType();
}
