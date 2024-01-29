package com.wenox.anonymization.blueprint_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString(exclude = "dump")
public class BlueprintInstantiatedEvent {
    Blueprint blueprint;
    byte[] dump;
}
