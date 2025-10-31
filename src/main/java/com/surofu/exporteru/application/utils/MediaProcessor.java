package com.surofu.exporteru.application.utils;

import com.surofu.exporteru.application.exception.InvalidMediaFile;
import com.surofu.exporteru.application.exception.UnsupportedFormat;
import com.surofu.exporteru.infrastructure.persistence.s3.UploadOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MediaProcessor {

    @Value("${app.compress.image.max-width:1920}")
    private int imageMaxWidth;

    @Value("${app.compress.image.max-height:1080}")
    private int imageMaxHeight;

    @Value("${app.compress.image.quality:0.8}")
    private float imageQuality;

    @Value("${app.compress.video.max-width:1280}")
    private int videoMaxWidth;

    @Value("${app.compress.video.max-height:720}")
    private int videoMaxHeight;

    @Value("${app.compress.video.quality:0.8}")
    private float videoQuality;

    private static final Set<String> IGNORABLE_IMAGE_FORMATS =
            Set.of("svg");

    private static final Set<String> SUPPORTED_IMAGE_FORMATS =
            Set.of("jpg", "jpeg", "png", "gif", "webp");

    private static final Set<String> SUPPORTED_VIDEO_FORMATS =
            Set.of("mp4", "avi", "mov", "mkv", "webm");

    // Основной метод для конвертации изображений в WebP
    public byte[] compressImageToWebP(MultipartFile file) throws IOException {
        return compressImageToWebP(file, UploadOptions.builder().build());
    }

    public byte[] compressImageToWebP(MultipartFile file, UploadOptions options) throws IOException {
        int width = Objects.requireNonNullElse(options.getWidth(), imageMaxWidth);
        int height = Objects.requireNonNullElse(options.getHeight(), imageMaxHeight);
        float quality = Objects.requireNonNullElse(options.getQuality(), imageQuality);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename).toLowerCase();

        if (IGNORABLE_IMAGE_FORMATS.contains(fileExtension)) {
            return file.getBytes();
        }

        if (!SUPPORTED_IMAGE_FORMATS.contains(fileExtension)) {
            throw new UnsupportedFormat("Unsupported image format: " + fileExtension);
        }

        try (InputStream inputStream = file.getInputStream()) {
            BufferedImage originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                throw new InvalidMediaFile("Could not read image file");
            }

            // Resize image
            BufferedImage resizedImage = resizeImage(originalImage, width, height);

            // Всегда конвертируем в WebP
            return convertToWebP(resizedImage, quality);
        }
    }

    // Основной метод для конвертации видео в WebM
    public byte[] compressVideoToWebM(MultipartFile file) throws IOException, InterruptedException {
        return compressVideoToWebM(file, UploadOptions.builder().build());
    }

    public byte[] compressVideoToWebM(MultipartFile file, UploadOptions options) throws IOException, InterruptedException {
        int width = Objects.requireNonNullElse(options.getWidth(), videoMaxWidth);
        int height = Objects.requireNonNullElse(options.getHeight(), videoMaxHeight);
        float quality = Objects.requireNonNullElse(options.getQuality(), videoQuality);

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename).toLowerCase();

        if (!SUPPORTED_VIDEO_FORMATS.contains(fileExtension)) {
            throw new UnsupportedFormat("Unsupported video format: " + fileExtension);
        }

        File inputFile = File.createTempFile("input", ".tmp");
        File outputFile = File.createTempFile("output", ".webm");

        try {
            file.transferTo(inputFile);

            // Преобразуем качество 0.0-1.0 в CRF 63-0 для VP9
            int crf = (int) (63 * (1 - quality));
            crf = Math.max(0, Math.min(63, crf));

            List<String> command = Arrays.asList(
                    "ffmpeg", "-i", inputFile.getAbsolutePath(),
                    "-vf", "scale=" + width + ":" + height + ":force_original_aspect_ratio=decrease",
                    "-c:v", "libvpx-vp9",        // Кодек VP9 для WebM
                    "-c:a", "libopus",           // Аудио кодек Opus для WebM
                    "-crf", String.valueOf(crf),
                    "-b:v", "0",                 // Отключаем битрейт, используем только CRF
                    "-deadline", "good",         // Качество кодирования
                    "-cpu-used", "0",           // Максимальное качество
                    "-row-mt", "1",             // Многопоточность
                    "-auto-alt-ref", "1",       // Улучшение качества
                    "-lag-in-frames", "25",
                    "-movflags", "+faststart",
                    "-y",
                    outputFile.getAbsolutePath()
            );

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Читаем stderr в отдельном потоке
            StringBuilder errorOutput = new StringBuilder();
            Thread errorThread = new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                        log.debug("FFmpeg: {}", line);
                    }
                } catch (IOException e) {
                    log.error("Error reading FFmpeg error stream: {}", e.getMessage());
                }
            });
            errorThread.start();

            // Читаем stdout
            Thread outputThread = new Thread(() -> {
                try (BufferedReader outputReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    while (outputReader.readLine() != null) {
                        // Потребляем вывод
                    }
                } catch (IOException e) {
                    log.error("Error reading FFmpeg output stream: {}", e.getMessage());
                }
            });
            outputThread.start();

            // Ждем завершения с таймаутом (увеличиваем для VP9)
            boolean finished = process.waitFor(60, TimeUnit.SECONDS);

            errorThread.join(5000);
            outputThread.join(5000);

            if (!finished) {
                process.destroyForcibly();
                throw new IOException("FFmpeg timeout. Error output: " + errorOutput);
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new IOException("FFmpeg failed with exit code " +
                        exitCode + ". Error: " + errorOutput);
            }

            if (!outputFile.exists() || outputFile.length() == 0) {
                throw new IOException("FFmpeg produced empty output file");
            }

            return Files.readAllBytes(outputFile.toPath());

        } finally {
            // Удаляем временные файлы
            silentlyDelete(inputFile);
            silentlyDelete(outputFile);
        }
    }

    // Улучшенный метод конвертации в WebP
    private byte[] convertToWebP(BufferedImage image, float quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Сначала пытаемся использовать ImageIO с установленным WebP writer
            if (!compressWebPWithImageIO(image, outputStream, quality)) {
                // Если не получилось, пробуем через внешнюю утилиту cwebp
                log.warn("WebP ImageIO writer not available, trying cwebp");
                compressWebPWithCwebp(image, outputStream, quality);
            }
            return outputStream.toByteArray();
        } finally {
            outputStream.close();
        }
    }

    private boolean compressWebPWithImageIO(BufferedImage image, OutputStream outputStream, float quality) {
        try {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/webp");

            if (!writers.hasNext()) {
                return false;
            }

            ImageWriter writer = writers.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
                writer.setOutput(ios);

                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    writeParam.setCompressionQuality(quality);
                }

                IIOImage iioImage = new IIOImage(image, null, null);
                writer.write(null, iioImage, writeParam);
            } finally {
                writer.dispose();
            }
            return true;
        } catch (Exception e) {
            log.error("WebP compression with ImageIO failed: {}", e.getMessage());
            return false;
        }
    }

    private void compressWebPWithCwebp(BufferedImage image, OutputStream outputStream, float quality) throws IOException {
        File tempInput = File.createTempFile("webp_input", ".png");
        File tempOutput = File.createTempFile("webp_output", ".webp");

        try {
            // Сохраняем изображение во временный PNG файл
            ImageIO.write(image, "png", tempInput);

            // Конвертируем в WebP с помощью cwebp
            int webpQuality = (int) (quality * 100);
            List<String> command = Arrays.asList(
                    "cwebp",
                    "-q", String.valueOf(webpQuality),
                    tempInput.getAbsolutePath(),
                    "-o", tempOutput.getAbsolutePath()
            );

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            // Читаем ошибки
            StringBuilder errorOutput = new StringBuilder();
            Thread errorThread = new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorOutput.append(line).append("\n");
                    }
                } catch (IOException e) {
                    log.error("Error reading cwebp error stream: {}", e.getMessage());
                }
            });
            errorThread.start();

            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            errorThread.join(5000);

            if (!finished) {
                process.destroyForcibly();
                throw new IOException("cwebp timeout. Error: " + errorOutput);
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new IOException("cwebp failed with exit code " + exitCode + ". Error: " + errorOutput);
            }

            // Читаем результат
            byte[] result = Files.readAllBytes(tempOutput.toPath());
            outputStream.write(result);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("cwebp process interrupted", e);
        } finally {
            silentlyDelete(tempInput);
            silentlyDelete(tempOutput);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
            return originalImage;
        }

        double widthRatio = (double) maxWidth / originalWidth;
        double heightRatio = (double) maxHeight / originalHeight;
        double ratio = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * ratio);
        int newHeight = (int) (originalHeight * ratio);

        newWidth = Math.max(1, newWidth);
        newHeight = Math.max(1, newHeight);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight,
                getOptimalImageType(originalImage));
        Graphics2D g = resizedImage.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        if (originalImage.getColorModel().hasAlpha()) {
            g.setComposite(AlphaComposite.Src);
        }

        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }

    private int getOptimalImageType(BufferedImage image) {
        if (image.getColorModel().hasAlpha()) {
            return BufferedImage.TYPE_INT_ARGB;
        }
        return BufferedImage.TYPE_INT_RGB;
    }

    private byte[] compressImage(BufferedImage image, String format, float quality) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            switch (format.toLowerCase()) {
                case "webp":
                    return convertToWebP(image, quality);
                case "gif":
                    compressGif(image, outputStream, quality);
                    break;
                case "avif":
                    log.warn("AVIF format not fully supported, converting to PNG");
                    compressStandardFormat(image, "png", outputStream, quality);
                    break;
                default:
                    compressStandardFormat(image, format, outputStream, quality);
                    break;
            }
            return outputStream.toByteArray();
        } finally {
            outputStream.close();
        }
    }

    private void compressGif(BufferedImage image, OutputStream outputStream, float quality) throws IOException {
        boolean hasTransparency = image.getColorModel().hasAlpha();

        if (hasTransparency) {
            compressStandardFormat(image, "png", outputStream, quality);
        } else {
            compressStandardFormat(image, "jpg", outputStream, quality);
        }
    }

    private void compressStandardFormat(BufferedImage image, String format,
                                        OutputStream outputStream, float quality) throws IOException {

        String outputFormat = format.toLowerCase();
        if ("jpg".equals(outputFormat)) {
            outputFormat = "jpeg";
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(outputFormat);
        if (!writers.hasNext()) {
            throw new IOException("No image writer found for format: " + format);
        }

        ImageWriter writer = writers.next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(ios);

            if (writeParam.canWriteCompressed() && "jpeg".equals(outputFormat)) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(quality);

                if (writeParam instanceof javax.imageio.plugins.jpeg.JPEGImageWriteParam) {
                    javax.imageio.plugins.jpeg.JPEGImageWriteParam jpegParams =
                            (javax.imageio.plugins.jpeg.JPEGImageWriteParam) writeParam;
                    jpegParams.setOptimizeHuffmanTables(true);
                }
            }

            IIOImage iioImage = new IIOImage(image, null, null);
            writer.write(null, iioImage, writeParam);
        } finally {
            writer.dispose();
        }
    }

    private String getFileExtension(@Nullable String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }


    private void silentlyDelete(File file) {
        if (file != null && file.exists()) {
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                log.warn("Could not delete temporary file: {}", file.getAbsolutePath());
            }
        }
    }
}