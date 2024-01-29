package com.wenox.anonymization.database_restoration_service.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Document
public class Timestamp {

    @Id
    private String id;

    private LocalDateTime timestamp;
}
