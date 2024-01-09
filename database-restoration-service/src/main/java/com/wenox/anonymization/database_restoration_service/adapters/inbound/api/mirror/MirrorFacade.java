package com.wenox.anonymization.database_restoration_service.adapters.inbound.api.mirror;

import com.wenox.anonymization.database_restoration_service.domain.service.mirror.MirrorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class MirrorFacade {

    private final MirrorService mirrorService;

    CreateMirrorResponse createMirror(CreateMirrorRequest dto) {
        String mirrorDb = mirrorService.createMirror(dto.getWorksheetId());
        return new CreateMirrorResponse(mirrorDb);
    }
}
