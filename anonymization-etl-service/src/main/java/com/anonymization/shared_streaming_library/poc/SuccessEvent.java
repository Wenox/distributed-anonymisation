package com.anonymization.shared_streaming_library.poc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessEvent {
    private String taskId;
    private int count;
}
