package com.wenox.anonymization.blueprint_service.adapters.api;

import com.wenox.anonymization.blueprint_service.domain.model.DatabaseType;
import com.wenox.anonymization.shared_events_library.api.RestoreMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

record ImportBlueprintRequest(@NotNull MultipartFile dumpFile,
                                     @NotNull DatabaseType databaseType,
                                     @NotNull RestoreMode restoreMode,
                                     @NotEmpty String title,
                                     String description) {

}
