package com.anonymization.shared_streaming_library.poc;

import lombok.Data;

@Data
public class SuccessEvent {
    private String animalId;
    private int count;
}
