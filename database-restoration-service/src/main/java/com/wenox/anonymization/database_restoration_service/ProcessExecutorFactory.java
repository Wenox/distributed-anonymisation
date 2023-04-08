package com.wenox.anonymization.database_restoration_service;

import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

public class ProcessExecutorFactory {

    public static ProcessExecutor newProcess(List<String> command) {
        return new ProcessExecutor()
                .command(command)
                .exitValue(0)
                .readOutput(false)
                .redirectOutput(Slf4jStream.of(LoggerFactory.getLogger(ProcessExecutorFactory.class)).asInfo())
                .destroyOnExit()
                .timeout(60, TimeUnit.SECONDS);
    }
}
