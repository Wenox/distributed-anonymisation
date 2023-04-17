package com.wenox.anonymization.suppression_service;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("person")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Person {
    @PrimaryKey
    private UUID id;
    private String firstName;
    private String lastName;

    // Constructors, getters, and setters
}