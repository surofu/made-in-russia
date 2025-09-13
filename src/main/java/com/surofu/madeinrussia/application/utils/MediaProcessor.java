package com.surofu.madeinrussia.application.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MediaProcessor {

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

    public byte[] compressImage(MultipartFile file, String extension) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(imageMaxWidth, imageMaxHeight)
                .outputQuality(imageQuality)
                .outputFormat(extension)
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    public byte[] compressVideo(MultipartFile file, String extension) throws IOException, InterruptedException {
        File inputFile = File.createTempFile("input", ".tmp");
        File outputFile = File.createTempFile("output", "." + extension);

        try {
            file.transferTo(inputFile);

            // Преобразуем качество 0.0-1.0 в CRF 51-0 (обратная зависимость)
            int crf = (int) (51 * (1 - videoQuality));
            crf = Math.max(0, Math.min(51, crf)); // Ограничиваем диапазон 0-51

            List<String> command = Arrays.asList(
                    "ffmpeg", "-i", inputFile.getAbsolutePath(),
                    "-vf", "scale=" + videoMaxWidth + ":" + videoMaxHeight,
                    "-c:v", "libx264",           // Явно указываем видео кодек
                    "-c:a", "aac",               // Явно указываем аудио кодек
                    "-b:v", "1M",                // Битрейт
                    "-crf", String.valueOf(crf), // Основной параметр качества
                    "-preset", "medium",         // Баланс скорости и сжатия
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
                    e.printStackTrace();
                }
            });
            errorThread.start();

            // Также читаем stdout, если нужно
            Thread outputThread = new Thread(() -> {
                try (BufferedReader outputReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    while (outputReader.readLine() != null) {
                        // Просто потребляем вывод
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            });
            outputThread.start();

            // Ждем завершения с таймаутом
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);

            errorThread.join(1000);
            outputThread.join(1000);

            if (!finished) {
                process.destroyForcibly();
                throw new IOException("FFmpeg timeout. Error output: " + errorOutput);
            }

            if (process.exitValue() != 0) {
                throw new IOException("FFmpeg failed with exit code " +
                        process.exitValue() + ". Error: " + errorOutput);
            }

            return Files.readAllBytes(outputFile.toPath());

        } finally {
            inputFile.delete();
            outputFile.delete();
        }
    }
}
