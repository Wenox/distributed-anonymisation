package com.anonymization.anonymization_service;

import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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

    private final StorageService storageService;
    private final CommandFactory commandFactory;

    @Value("${command.s3-upload}")
    private String s3UploadCommand;

    @PostMapping
    public ResponseEntity<?> executeAnonymizationScript(@Valid @RequestBody TriggerRequest dto) {
        log.info("=====> Received dto: {}", dto);
        try {
            return ResponseEntity.ok(restoreScriptDump(dto.getDbName(), dto.getFilePath()));
        } catch (Exception ex) {
            log.error("Error occurred during processing of dto: {}", dto, ex);
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Error executing script: " + ex);
        }
    }

    public ExecuteScriptResponse restoreScriptDump(String dbName, String file) throws IOException, TimeoutException, InterruptedException {
        log.info("Executing script {} against database {}", file, dbName);
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_SCRIPTS, file)) {
            restoreDumpFromInputStream(inputStream, commandFactory.generateExecuteScriptCommand(dbName));
        }
        return new ExecuteScriptResponse("Executed script successfully");
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

    @PostMapping("/generate-dump")
    public ResponseEntity<?> generateDump(@Valid @RequestBody DumpRequest dto) {
        log.info("=====> Received dto: {}", dto);
        try {
            return ResponseEntity.ok(dumpAndSaveInS3(dto.getDbName(), "resulting_dump_script.sql"));
        } catch (Exception ex) {
            log.error("Error occurred during processing of dto: {}", dto, ex);
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(ex);
        }
    }

    public GenerateDumpResponse dumpAndSaveInS3(String dbName, String file) throws IOException, TimeoutException, InterruptedException {
        String command = String.format("pg_dump -h localhost -p 5432 -U postgres -Fp %s --verbose | aws s3 cp - s3://%s/%s", dbName, S3Constants.BUCKET_DUMPS, file);
        log.info("Executing command: {}", command);

        int exitCode = new ProcessExecutor()
                .command("bash", "-c", command)
                .redirectOutput(Slf4jStream.of(getClass()).asInfo())
                .timeout(60, TimeUnit.SECONDS)
                .execute()
                .getExitValue();

        if (exitCode != 0) {
            throw new RuntimeException(String.format("Script execution using command %s failed with exit code: %s", command, exitCode));
        }

        log.info("Successfully executed script using command {}", command);
        return GenerateDumpResponse.builder().file(file).build();
    }

}
