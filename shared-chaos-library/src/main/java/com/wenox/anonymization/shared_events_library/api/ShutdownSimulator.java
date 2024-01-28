package com.wenox.anonymization.shared_events_library.api;

import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;
import java.lang.reflect.Field;

@Slf4j
public class ShutdownSimulator {

    public static void crashJVM() {
        try {
            log.error("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            log.error("Crashing JVM to simulate service failure...");
            log.error("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            unsafe.putAddress(0, 0); // this will crash JVM
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
