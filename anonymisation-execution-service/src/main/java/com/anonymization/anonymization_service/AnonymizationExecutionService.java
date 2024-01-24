package com.anonymization.anonymization_service;

import com.wenox.anonymization.s3.S3Constants;
import com.wenox.anonymization.s3.api.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping
    public ResponseEntity<?> executeAnonymizationScript(@Valid @RequestBody TriggerRequest dto) {
        log.info("=====> Received dto: {}", dto);
        try {
            return ResponseEntity.ok(restoreScriptDump(dto.getMirrorId(), dto.getFilePath()));
        } catch (Exception ex) {
            log.error("Error occurred during processing of dto: {}", dto, ex);
            ex.printStackTrace();
            return ResponseEntity.badRequest().body("Error executing script: " + ex);
        }
    }

    public Response restoreScriptDump(String db, String file) throws IOException, TimeoutException, InterruptedException {
        log.info("Executing script {} against database {}", file, db);
        try (InputStream inputStream = storageService.downloadFile(S3Constants.BUCKET_SCRIPTS, file)) {
            restoreDumpFromInputStream(inputStream, commandFactory.generateExecuteScriptCommand(db));
        }
        return new Response("Executed script successfully");
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
            return ResponseEntity.ok(dumpAndSaveInS3(dto));
        } catch (Exception ex) {
            log.error("Error occurred during processing of dto: {}", dto, ex);
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(ex);
        }
    }

    public Response dumpAndSaveInS3(DumpRequest dumpRequest) throws IOException, TimeoutException, InterruptedException {
        // todo
        String command;
        if (dumpRequest.getRestoreMode() == RestoreMode.SCRIPT) {
            command = String.format("pg_dump -h postgres -p 5432 -U postgres -Fp %s --verbose | aws s3 cp - s3://%s/%s", dumpRequest.getMirrorId(), S3Constants.BUCKET_DUMPS, dumpRequest.getDumpPath() + ".sql");
        } else {
            command = String.format("pg_dump -h postgres -p 5432 -U postgres -Fc %s --verbose | aws s3 cp - s3://%s/%s", dumpRequest.getMirrorId(), S3Constants.BUCKET_DUMPS, dumpRequest.getDumpPath() + ".dump");
        }
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
        return new Response("Generated dump successfully");
    }

}
