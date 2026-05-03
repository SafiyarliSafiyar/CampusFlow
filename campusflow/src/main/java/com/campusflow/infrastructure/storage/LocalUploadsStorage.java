package com.campusflow.infrastructure.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LocalUploadsStorage {
    private final Path uploadsRoot;

    public LocalUploadsStorage(@Value("${campusflow.uploads.dir:uploads}") String uploadsDir) {
        Path configured = Paths.get(uploadsDir);
        if (!configured.isAbsolute()) {
            configured = Paths.get(System.getProperty("user.dir")).resolve(configured);
        }
        this.uploadsRoot = configured.normalize();
    }

    public String storeProfilePhoto(MultipartFile file) throws IOException {
        Files.createDirectories(uploadsRoot);

        String safeExt = extractSafeExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + (safeExt.isEmpty() ? "" : ("." + safeExt));
        Path target = uploadsRoot.resolve(filename).normalize();

        file.transferTo(target);
        return "/uploads/" + filename;
    }

    private String extractSafeExtension(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            return "";
        }
        String ext = originalFilename.substring(dot + 1).toLowerCase(Locale.ROOT).trim();
        if (ext.length() > 10) {
            return "";
        }
        if (!ext.matches("[a-z0-9]+")) {
            return "";
        }
        return ext;
    }
}

