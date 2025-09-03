package com.surofu.madeinrussia.application.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class MediaProcessor {

    @Value("${app.compress.image.max-width}")
    private int imageMaxWidth;

    @Value("${app.compress.image.max-height}")
    private int imageMaxHeight;

    @Value("${app.compress.image.quality}")
    private int imageQuality;

    @Value("${app.compress.video.max-width}")
    private int videoMaxWidth;

    @Value("${app.compress.video.max-height}")
    private int videoMaxHeight;

    @Value("${app.compress.video.quality}")
    private int videoQuality;

    public byte[] processImage(byte[] data) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));

//        int originalWidth = image.getWidth();
//        int originalHeight = image.getHeight();
//
//        // Resize
//        if (image.getWidth() > imageMaxWidth) {
//            int newHeight = (int) ((double) imageMaxWidth / originalWidth * originalHeight);
//            image = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, imageMaxWidth, newHeight);
//        }
//
//        // Crop
//        image = cropImage(image, imageMaxWidth, imageMaxHeight);
//
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "webp", output);

        return output.toByteArray();
    }

    public byte[] processVideo(byte[] data) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
//
//        int originalWidth = image.getWidth();
//        int originalHeight = image.getHeight();
//
//        // Resize
//        if (image.getWidth() > videoMaxWidth) {
//            int newHeight = (int) ((double) videoMaxWidth / originalWidth * originalHeight);
//            image = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.AUTOMATIC, videoMaxWidth, newHeight);
//        }
//
//        // Crop
//        image = cropImage(image, videoMaxWidth, videoMaxHeight);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "webm", output);

        return output.toByteArray();
    }

    private BufferedImage cropImage(BufferedImage image, int width, int height) {
        int x = 0;
        int y = 0;
        width = Math.min(width, image.getWidth() - x);
        height = Math.min(height, image.getHeight() - y);
        return image.getSubimage(x, y, width, height);
    }
}
