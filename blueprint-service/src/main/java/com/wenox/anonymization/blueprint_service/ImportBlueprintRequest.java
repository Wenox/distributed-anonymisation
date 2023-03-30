package com.wenox.anonymization.blueprint_service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ImportBlueprintRequest(@NotNull MultipartFile dumpFile,
                                     @NotNull FileType type,
                                     @NotNull RestoreMode restoreMode,
                                     @NotEmpty String title,
                                     String description) {

}
