package org.mdream.zpl;

import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;

/**
 * thanks and original code https://gist.github.com/trevarj/1255e5cbc08fb3f79c3f255e25989a18
 */
@ApplicationScoped
public class Z64Compressor implements Compressor {
    private static final String ZPL_PATTERN = "^GFA,%d,%d,%s,:Z64:%s:%s^FS%n";

    @Inject
    ZplImageUtils zplImageUtils;

    @Override
    public CompressorType getCompressorType() {
        return CompressorType.Z64;
    }

    @Override
    @SneakyThrows
    public String toZpl(BufferedImage image) {
        String result;
        BufferedImage resizedImage = zplImageUtils.toMonoAndInvertColour(image);
        DataBufferByte dataBufferByte = (DataBufferByte) resizedImage.getRaster().getDataBuffer();
        try (ByteArrayOutputStream compressedImage = new ByteArrayOutputStream()) {
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(compressedImage);
            deflaterOutputStream.write(dataBufferByte.getData(), 0, dataBufferByte.getData().length);
            deflaterOutputStream.finish();
            byte[] encodedBytesImage = Base64.getMimeEncoder().encode(compressedImage.toByteArray());

            String crcString = getCRCHexString(encodedBytesImage);
            int rowBytes = zplImageUtils.getWidthBytes(resizedImage.getWidth());
            int bytes = rowBytes * resizedImage.getHeight();

            result = String.format(ZPL_PATTERN, bytes, bytes, rowBytes,
                    new String(encodedBytesImage, StandardCharsets.US_ASCII), crcString);
        }
        return result;
    }

    /**
     *  Reads in a sequence of bytes and prints out its 16 bit
     *  Cylcic Redundancy Check (CRC-CCIIT 0xFFFF).
     *
     *  1 + x + x^5 + x^12 + x^16 is irreducible polynomial.
     *
     */
    private static String getCRCHexString(byte[] bytes) {
        int crc = 0x0000;           // initial value
        int polynomial = 0x1021;    // 0001 0000 0010 0001  (0, 5, 12)
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;

                if (c15 ^ bit) {
                    crc ^= polynomial;
                }
            }
        }

        crc &= 0xffff;
        return Integer.toHexString(crc);
    }
}
