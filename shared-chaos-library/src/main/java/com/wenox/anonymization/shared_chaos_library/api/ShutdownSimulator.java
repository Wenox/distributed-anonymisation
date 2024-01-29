package com.wenox.anonymization.shared_chaos_library.api;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

@Slf4j
public class ShutdownSimulator {

    public static void crashJVM(String purpose) {
        try {
            log.error("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.error("Crashing JVM to simulate service failure...");
            log.error("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.error("Simulation purpose: {}", purpose);
            log.error("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            unsafe.putAddress(0, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
