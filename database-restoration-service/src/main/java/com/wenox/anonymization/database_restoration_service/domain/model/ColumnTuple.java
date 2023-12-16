package com.wenox.anonymization.database_restoration_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ColumnTuple {
    List<String> pks;
    List<String> values;
}
