package com.anonymization.shared_streaming_library.poc;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class AnimalTask {
    private String animalId;
    private String animalName;
    private Integer age;
}