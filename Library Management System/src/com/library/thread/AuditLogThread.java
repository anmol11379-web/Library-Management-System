package com.library.thread;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Multithreaded Background Auditor
 * Demonstrates:
 * - Thread creation by extending Thread class (Unit 3 & Syllabus Experiment 13)
 * - Thread Life Cycle & Life Cycle Methods (start, sleep, join, interrupt) (Unit 3)
 * - Java Synchronization methods (synchronized method and synchronized block) (Unit 3)
 * - Collections Framework (List, ArrayList) (Unit 4)
 */
public class AuditLogThread extends Thread {
    private final List<String> logQueue = new ArrayList<>();
    private final List<String> processedLogs = new ArrayList<>();
    private volatile boolean running = true;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AuditLogThread() {
        super("AuditLogger-WorkerThread");
        setDaemon(true); // Allows JVM to exit cleanly if main terminates
    }

    /**
     * Synchronized method: Adds an event entry safely from any calling thread.
     * Demonstrates: Java Synchronization (Unit 3)
     */
    public synchronized void recordEvent(String event) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String entry = "[" + timestamp + "] [AUDIT] " + event;
        logQueue.add(entry);
        notify(); // Wake up worker if waiting
    }

    /**
     * Thread Life Cycle: run() method executed when thread starts.
     */
    @Override
    public void run() {
        while (running) {
            String logToProcess = null;
            synchronized (this) {
                while (logQueue.isEmpty() && running) {
                    try {
                        wait(2000); // Wait for new events or timeout
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (!logQueue.isEmpty()) {
                    logToProcess = logQueue.remove(0);
                }
            }

            if (logToProcess != null) {
                // Simulate asynchronous audit processing
                synchronized (processedLogs) {
                    processedLogs.add(logToProcess);
                }
            }

            try {
                Thread.sleep(100); // Simulate processing latency
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Thread-safe retrieval of recent audit entries.
     */
    public synchronized List<String> getRecentAudits() {
        synchronized (processedLogs) {
            return new ArrayList<>(processedLogs);
        }
    }

    /**
     * Graceful stop showing thread join (Unit 3).
     */
    public void stopAuditor() {
        this.running = false;
        synchronized (this) {
            notifyAll();
        }
        try {
            this.join(1000); // Wait up to 1 second for thread to terminate cleanly
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
