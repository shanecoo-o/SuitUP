package com.suitup.backend.upload;

import org.springframework.core.io.Resource;

public record StoredFileResource(UploadedFileEntity metadata, Resource resource) {
}
