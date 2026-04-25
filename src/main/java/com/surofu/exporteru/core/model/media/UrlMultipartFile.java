package com.surofu.exporteru.core.model.media;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jspecify.annotations.NonNull;
import org.springframework.web.multipart.MultipartFile;

public record UrlMultipartFile(
    byte[] content,
    String filename,
    String contentType
) implements MultipartFile {

  @Override
  public @NonNull String getName() {
    return filename;
  }

  @Override
  public String getOriginalFilename() {
    return filename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return content.length == 0;
  }

  @Override
  public long getSize() {
    return content.length;
  }

  @Override
  public byte @NonNull [] getBytes() {
    return content;
  }

  @Override
  public @NonNull InputStream getInputStream() {
    return new ByteArrayInputStream(content);
  }

  @Override
  public void transferTo(@NonNull File dest) throws IOException {
    try (FileOutputStream os = new FileOutputStream(dest)) {
      os.write(content);
    }
  }
}
