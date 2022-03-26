package org.mdream.zpl;

import io.quarkus.runtime.StartupEvent;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.NotSupportedException;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CompressorRegistry {
    private final Map<CompressorType, Compressor> compressorMap = new HashMap<>();

    @Inject
    Instance<Compressor> compressors;

    void onStart(@Observes StartupEvent ev) {
        compressors.forEach(compressor -> register(compressor.getCompressorType(), compressor));
    }

    public void register(CompressorType compressorType, Compressor compressor) {
        compressorMap.put(compressorType, compressor);
    }

    public String compress(CompressorType compressorType, BufferedImage image) {
        Compressor compressor = compressorMap.get(compressorType);
        if (compressor == null) {
            throw new NotSupportedException(String.format("compressorType=%s isn't exists", compressorType));
        }
        return StringUtils.deleteWhitespace(compressor.toZpl(image));
    }
}
