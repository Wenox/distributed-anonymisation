package com.wenox.anonymization.database_restoration_service.adapters.api;

import com.wenox.anonymization.database_restoration_service.domain.service.mirror.MirrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MirrorFacade {

    private final MirrorService mirrorService;

    CreateMirrorResponse createMirror(CreateMirrorRequest dto) {
        String mirrorDatabaseName = mirrorService.createMirror(dto.getWorksheetId());
        return new CreateMirrorResponse(mirrorDatabaseName);
    }
}
