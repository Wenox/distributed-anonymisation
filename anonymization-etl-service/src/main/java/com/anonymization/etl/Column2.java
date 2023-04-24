package com.anonymization.etl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Column2 {
    private List<String> pks;
    private List<String> values;
}
