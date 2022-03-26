package org.mdream.zpl;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Singleton
public class FileUtils {

    @ConfigProperty(name = "upload.directory")
    String UPLOAD_DIR;

    private void writeFile(InputStream inputStream, String fileName)
            throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        File customDir = new File(UPLOAD_DIR);
        if (!customDir.exists()){
            customDir.mkdirs();
        }
        fileName = customDir.getAbsolutePath() +
                File.separator + fileName;
        Path path = Paths.get(fileName);
        File file = new File(fileName);
        file.delete();
        Files.write(path, bytes,
                StandardOpenOption.CREATE_NEW);
    }
}
