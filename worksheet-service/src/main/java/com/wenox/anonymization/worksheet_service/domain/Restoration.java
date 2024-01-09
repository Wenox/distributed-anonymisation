package com.wenox.anonymization.worksheet_service.domain;

import lombok.Data;

@Data
public class Restoration {

    private String restorationId;

    private String blueprintId;

    private boolean isActive;

    private String runnerIp;
}
