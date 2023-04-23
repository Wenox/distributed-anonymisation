package com.anonymization.shared_streaming_library;

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
