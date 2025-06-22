package com.surofu.madeinrussia.application.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.Picture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@Component
public class FileCompressor {

    @Value("${app.compress.image.max-width}")
    private int imageMaxWidth;

    @Value("${app.compress.image.max-height}")
    private int imageMaxHeight;

    @Value("${app.compress.image.quality}")
    private float imageQuality;

    @Value("${app.compress.video.max-width}")
    private int videoMaxWidth;

    @Value("${app.compress.video.max-height}")
    private int videoMaxHeight;

    @Value("${app.compress.video.quality}")
    private float videoQuality;

    static {
        // Проверяем доступность WebP поддержки
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) {
            throw new IllegalStateException("WebP support not available. Add webp-imageio dependency.");
        }
    }

    public byte[] compressImageAndConvertToWebP(MultipartFile file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(imageMaxWidth, imageMaxHeight)
                .outputQuality(imageQuality) // Качество (0.1 - 1.0)
                .toOutputStream(outputStream);

        ByteArrayOutputStream convertedOutputStream = convertToWebP(outputStream);

        return convertedOutputStream.toByteArray();
    }

    public byte[] getFirstFrame(MultipartFile file) throws IOException, JCodecException {
        Picture picture = FrameGrab.getFrameFromFile(file.getResource().getFile(), 1);
        return picture.getPlaneData(0);
    }

    private ByteArrayOutputStream convertToWebP(ByteArrayOutputStream originalStream) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(originalStream.toByteArray()));

        if (image == null) {
            throw new IOException("Could not read image from stream");
        }

        ByteArrayOutputStream webPOutputStream = new ByteArrayOutputStream();

        if (!ImageIO.write(image, "webp", webPOutputStream)) {
            throw new IOException("WebP conversion failed");
        }

        return webPOutputStream;
    }
}
