package org.peakaboo.framework.scratch.list;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;
import org.peakaboo.framework.scratch.encoders.CompoundEncoder;
import org.peakaboo.framework.scratch.encoders.compressors.Compressors;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

/**
 * JUnit test for measuring current synchronization performance.
 * Run this before and after implementing ReadWriteLock to compare performance.
 */
public class SynchronizationPerformanceTest {

    private static final int SPECTRUM_SIZE = 2048;
    private static final int DATASET_SIZE = 1000; // Smaller for unit tests
    private static final int CONCURRENT_READERS = 4;
    private static final int READS_PER_THREAD = 500;

    /**
     * Generate realistic test data (float[2048] converted to bytes)
     */
    private byte[] generateSpectrumBytes() {
        float[] spectrum = new float[SPECTRUM_SIZE];
        
        // Generate realistic spectral data
        float background = 50f + ThreadLocalRandom.current().nextFloat() * 100f;
        for (int i = 0; i < SPECTRUM_SIZE; i++) {
            spectrum[i] = background + ThreadLocalRandom.current().nextFloat() * 20f;
        }
        
        // Add some peaks
        for (int p = 0; p < 5; p++) {
            int center = ThreadLocalRandom.current().nextInt(SPECTRUM_SIZE);
            float intensity = 200f + ThreadLocalRandom.current().nextFloat() * 800f;
            for (int i = Math.max(0, center - 10); i < Math.min(SPECTRUM_SIZE, center + 10); i++) {
                spectrum[i] += intensity * Math.exp(-Math.pow((i - center) / 3.0, 2));
            }
        }
        
        // Convert to bytes
        ByteBuffer buffer = ByteBuffer.allocate(spectrum.length * 4);
        for (float f : spectrum) {
            buffer.putFloat(f);
        }
        return buffer.array();
    }

    @Test
    public void testMemoryBackedConcurrentPerformance() throws Exception {
        System.out.println("\n=== Memory-backed ScratchList Performance Test ===");
        
        ScratchList<byte[]> list = ScratchLists.memoryBacked(
            new CompoundEncoder<>(Serializers.java(), Compressors.lz4fast())
        );
        
        try {
            runPerformanceTest("Memory-backed", list);
        } finally {
            if (list instanceof AutoCloseable) {
                ((AutoCloseable) list).close();
            }
        }
    }

    @Test
    public void testDiskBackedConcurrentPerformance() throws Exception {
        System.out.println("\n=== Disk-backed ScratchList Performance Test ===");
        
        ScratchList<byte[]> list = ScratchLists.diskBacked(
            new CompoundEncoder<>(Serializers.java(), Compressors.lz4fast())
        );
        
        try {
            runPerformanceTest("Disk-backed", list);
        } finally {
            if (list instanceof AutoCloseable) {
                ((AutoCloseable) list).close();
            }
        }
    }

    private void runPerformanceTest(String testName, ScratchList<byte[]> list) throws Exception {
        System.out.printf("Testing %s with %d spectra...\n", testName, DATASET_SIZE);
        
        // Phase 1: Populate the list (write test)
        long writeStart = System.nanoTime();
        for (int i = 0; i < DATASET_SIZE; i++) {
            list.add(generateSpectrumBytes());
        }
        long writeTime = System.nanoTime() - writeStart;
        
        System.out.printf("✓ Write phase: %.2f ms (%.2f spectra/sec)\n", 
            writeTime / 1_000_000.0, 
            DATASET_SIZE / (writeTime / 1_000_000_000.0));
        
        // Phase 2: Sequential read test
        long readStart = System.nanoTime();
        for (int i = 0; i < DATASET_SIZE; i++) {
            byte[] spectrum = list.get(i);
            assertNotNull("Spectrum should not be null", spectrum);
            assertTrue("Spectrum should not be empty", spectrum.length > 0);
        }
        long readTime = System.nanoTime() - readStart;
        
        System.out.printf("✓ Sequential read: %.2f ms (%.2f spectra/sec)\n", 
            readTime / 1_000_000.0, 
            DATASET_SIZE / (readTime / 1_000_000_000.0));
        
        // Phase 3: Concurrent read test (the critical test for synchronization)
        long concurrentReadTime = testConcurrentReads(list);
        
        System.out.printf("✓ Concurrent read: %.2f ms\n", concurrentReadTime / 1_000_000.0);
        
        // Summary
        double writeSpeedMBps = calculateThroughputMBps(DATASET_SIZE, writeTime);
        double readSpeedMBps = calculateThroughputMBps(DATASET_SIZE, readTime);
        
        System.out.printf("Summary: Write %.2f MB/s, Read %.2f MB/s\n", 
            writeSpeedMBps, readSpeedMBps);
    }

    private long testConcurrentReads(ScratchList<byte[]> list) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_READERS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(CONCURRENT_READERS);
        AtomicLong successfulReads = new AtomicLong(0);
        AtomicLong exceptions = new AtomicLong(0);
        
        // Create concurrent reader tasks
        for (int t = 0; t < CONCURRENT_READERS; t++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    for (int i = 0; i < READS_PER_THREAD; i++) {
                        try {
                            int index = random.nextInt(DATASET_SIZE);
                            byte[] spectrum = list.get(index);
                            if (spectrum != null && spectrum.length > 0) {
                                successfulReads.incrementAndGet();
                            }
                            
                            // Also test size() calls (common read operation)
                            int size = list.size();
                            assertTrue("Size should be >= dataset size", size >= DATASET_SIZE);
                            
                        } catch (Exception e) {
                            exceptions.incrementAndGet();
                            System.err.println("Read exception: " + e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    exceptions.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown();
                }
            });
        }
        
        // Start timing and release all threads
        long start = System.nanoTime();
        startLatch.countDown();
        
        // Wait for completion
        boolean completed = finishLatch.await(30, TimeUnit.SECONDS);
        long duration = System.nanoTime() - start;
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertTrue("Test should complete within timeout", completed);
        
        long totalOperations = CONCURRENT_READERS * READS_PER_THREAD;
        System.out.printf("  → %d threads, %d operations each, %d successful reads, %d exceptions\n", 
            CONCURRENT_READERS, READS_PER_THREAD, successfulReads.get(), exceptions.get());
        System.out.printf("  → %.0f operations/sec\n", 
            totalOperations / (duration / 1_000_000_000.0));
        
        // No exceptions should occur with proper synchronization
        assertTrue("No exceptions should occur during concurrent reads", exceptions.get() == 0);
        
        return duration;
    }

    private double calculateThroughputMBps(int numSpectra, long timeNs) {
        double dataSize = numSpectra * SPECTRUM_SIZE * 4; // float = 4 bytes
        double dataSizeMB = dataSize / (1024.0 * 1024.0);
        double timeSeconds = timeNs / 1_000_000_000.0;
        return dataSizeMB / timeSeconds;
    }

    /**
     * Simple performance comparison test that can be run standalone
     */
    @Test
    public void compareImplementations() throws IOException {
        System.out.println("\n=== Quick Performance Comparison ===");
        
        // Test both implementations with smaller dataset
        int testSize = 100;
        
        // Memory test
        ScratchList<byte[]> memList = ScratchLists.memoryBacked(
            new CompoundEncoder<>(Serializers.java(), Compressors.lz4fast())
        );
        long memTime = quickPerformanceTest(memList, testSize);
        
        // Disk test  
        ScratchList<byte[]> diskList = ScratchLists.diskBacked(
            new CompoundEncoder<>(Serializers.java(), Compressors.lz4fast())
        );
        long diskTime = quickPerformanceTest(diskList, testSize);
        
        System.out.printf("Memory: %.2f ms, Disk: %.2f ms (Memory is %.2fx faster)\n",
            memTime / 1_000_000.0, diskTime / 1_000_000.0, 
            (double) diskTime / memTime);
            
        // Cleanup
        try {
            if (memList instanceof AutoCloseable) ((AutoCloseable) memList).close();
            if (diskList instanceof AutoCloseable) ((AutoCloseable) diskList).close();
        } catch (Exception e) {
            // Ignore cleanup errors in test
        }
    }

    private long quickPerformanceTest(ScratchList<byte[]> list, int size) {
        long start = System.nanoTime();
        
        // Write
        for (int i = 0; i < size; i++) {
            list.add(generateSpectrumBytes());
        }
        
        // Read
        for (int i = 0; i < size; i++) {
            list.get(i);
        }
        
        return System.nanoTime() - start;
    }
}