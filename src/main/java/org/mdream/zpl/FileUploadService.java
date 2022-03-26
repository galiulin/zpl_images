package org.mdream.zpl;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MultivaluedMap;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
public class FileUploadService {

    @Inject
    CompressorRegistry registry;

    @Inject
    ZplImageUtils imageUtils;

    public String uploadFile(MultipartFormDataInput input) {
        String result = "Files Successfully Uploaded";
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<String> fileNames = new ArrayList<>();
        List<InputPart> inputParts = uploadForm.get("file");
        String fileName = null;
        for (InputPart inputPart : inputParts) {
            try {
                MultivaluedMap<String, String> header =
                        inputPart.getHeaders();
                fileName = getFileName(header);
                fileNames.add(fileName);
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
//                writeFile(inputStream,fileName);
                BufferedImage image = ImageIO.read(inputStream);
                inputStream.close();
                image = imageUtils.resizeImg(image, 500, 300);
                image = imageUtils.toMonoAndInvertColour(image);
                result = registry.compress(CompressorType.Z64, image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.
                getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "";
    }
}
