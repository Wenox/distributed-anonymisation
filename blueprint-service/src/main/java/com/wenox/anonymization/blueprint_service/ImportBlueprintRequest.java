package com.wenox.anonymization.blueprint_service;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Value
@EqualsAndHashCode(callSuper = true)
public record ImportBlueprintRequest(@NotNull MultipartFile file,
                                     @NotNull FileType type,
                                     @NotNull RestoreMode restoreMode,
                                     @NotEmpty String title,
                                     String description) {

}
