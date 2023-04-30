package com.anonymization.etl.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ColumnTuple implements Serializable {
    List<String> pks;
    List<String> values;

    public ColumnTuple copyWithValues(List<String> newValues) {
        return new ColumnTuple(this.pks, newValues);
    }
}
