package com.anonymization.anonymization_service;

import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import com.wenox.anonymization.shared_events_library.api.KafkaConstants;
import com.wenox.anonymization.shared_events_library.api.KafkaTemplateWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/execute-anonymization")
public class AnonymizationExecutionService {

    private final KafkaTemplateWrapper<String, Object> kafkaTemplateWrapper;
    private final StorageService storageService;
    private final CommandFactory commandFactory;

    @KafkaListener(topics = KafkaConstants.TOPIC_ANONYMIZATION_EXECUTION, groupId = "database-restoration-service-group")
    @PostMapping
    public void onAnonymizationScriptReady(@Valid @RequestBody TriggerRequest dto) {
        log.info("=====> Received dto: {}", dto);
        try {
            restoreScriptDump(dto.getDatabaseName(), dto.getFilePath());
        } catch (Exception ex) {
            log.error("Error occurred during processing of dto: {}", dto, ex);
            ex.printStackTrace();
        }
    }

    public void restoreScriptDump(String dbName, String file) throws IOException, TimeoutException, InterruptedException {
        log.info("Executing script {} against database {}", file, dbName);
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_SCRIPTS, file)) {
            restoreDumpFromInputStream(inputStream, commandFactory.generateExecuteScriptCommand(dbName));
        }
    }

    private void restoreDumpFromInputStream(InputStream inputStream, List<String> command) throws IOException, InterruptedException, TimeoutException {
        int exitCode = new ProcessExecutor()
                .command(command)
                .redirectInput(inputStream)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException(String.format("Script execution using command %s failed with exit code: %s", command, exitCode));
        }

        log.info("Successfully executed script using command {}", command);
    }
}
