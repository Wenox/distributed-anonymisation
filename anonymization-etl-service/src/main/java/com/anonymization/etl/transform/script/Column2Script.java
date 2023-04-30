package com.anonymization.etl.transform.script;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Column2Script {
    private String alterColumnType;
    private List<String> updateQueries;

    public byte[] toByteArray() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String concatenated = Stream
                    .concat(Optional.ofNullable(alterColumnType).stream(), updateQueries.stream())
                    .collect(Collectors.joining());
            outputStream.write(concatenated.getBytes(StandardCharsets.UTF_8));

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error while concatenating queries", e);
        }
    }
}
