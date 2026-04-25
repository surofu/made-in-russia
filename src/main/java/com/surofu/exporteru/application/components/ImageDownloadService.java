package com.surofu.exporteru.application.components;

import com.surofu.exporteru.core.model.media.UrlMultipartFile;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class ImageDownloadService {

  private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(10))
      .followRedirects(HttpClient.Redirect.NORMAL)
      .build();

  public Optional<MultipartFile> download(String url) {
    try {
      var request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .timeout(Duration.ofSeconds(10))
          .GET()
          .build();

      var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());

      if (response.statusCode() != 200 || response.body().length == 0) {
        log.warn("Failed to download image from {}: status {}", url, response.statusCode());
        return Optional.empty();
      }

      String contentType = response.headers()
          .firstValue(HttpHeaders.CONTENT_TYPE)
          .orElse("image/jpeg");

      String filename = extractFilename(url);
      return Optional.of(new UrlMultipartFile(response.body(), filename, contentType));

    } catch (Exception e) {
      log.warn("Error downloading image from {}: {}", url, e.getMessage());
      return Optional.empty();
    }
  }

  private String extractFilename(String url) {
    try {
      String path = URI.create(url).getPath();
      String name = path.substring(path.lastIndexOf('/') + 1);
      return name.isBlank() ? "image.jpg" : name;
    } catch (Exception e) {
      return "image.jpg";
    }
  }
}
