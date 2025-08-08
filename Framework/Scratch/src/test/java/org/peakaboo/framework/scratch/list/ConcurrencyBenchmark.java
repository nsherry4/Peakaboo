package org.peakaboo.framework.scratch.list;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.peakaboo.framework.scratch.encoders.CompoundEncoder;
import org.peakaboo.framework.scratch.encoders.compressors.Compressors;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

/**
 * Comprehensive benchmark for ScratchList implementations focusing on concurrent
 * read/write performance with realistic float[2048] data.
 */
public class ConcurrencyBenchmark {

    private static final int SPECTRUM_SIZE = 2048;
    private static final int DATASET_SIZE = 10000;
    private static final int WARMUP_ITERATIONS = 3;
    private static final int BENCHMARK_ITERATIONS = 5;
    
    // Realistic test data generator
    private static class SpectrumGenerator {
        private final Random random = new Random(42); // Deterministic for consistency
        
        public float[] generateSpectrum() {
            float[] spectrum = new float[SPECTRUM_SIZE];
            
            // Generate realistic spectral data with peaks and background
            // Background level
            float background = 50f + random.nextFloat() * 100f;
            
            for (int i = 0; i < SPECTRUM_SIZE; i++) {
                spectrum[i] = background + random.nextFloat() * 20f; // Background noise
            }
            
            // Add some peaks (typical XRF spectrum has 5-20 peaks)
            int numPeaks = 8 + random.nextInt(12);
            for (int p = 0; p < numPeaks; p++) {
                int center = random.nextInt(SPECTRUM_SIZE);
                float intensity = 200f + random.nextFloat() * 1000f;
                float width = 2f + random.nextFloat() * 8f;
                
                // Gaussian peak
                for (int i = Math.max(0, center - 50); i < Math.min(SPECTRUM_SIZE, center + 50); i++) {
                    float distance = (i - center) / width;
                    spectrum[i] += intensity * Math.exp(-distance * distance);
                }
            }
            
            return spectrum;
        }
        
        public byte[] generateSpectrumBytes() {
            float[] spectrum = generateSpectrum();
            ByteBuffer buffer = ByteBuffer.allocate(spectrum.length * 4);
            for (float f : spectrum) {
                buffer.putFloat(f);
            }
            return buffer.array();
        }
    }
    
    // Performance measurement results
    public static class BenchmarkResults {
        public final String testName;
        public final long writeTimeNs;
        public final long readTimeNs;
        public final long concurrentReadTimeNs;
        public final double writeThroughputMBps;
        public final double readThroughputMBps;
        public final double concurrentReadThroughputMBps;
        public final long memoryUsageBytes;
        
        public BenchmarkResults(String testName, long writeTimeNs, long readTimeNs, 
                               long concurrentReadTimeNs, double writeThroughputMBps,
                               double readThroughputMBps, double concurrentReadThroughputMBps,
                               long memoryUsageBytes) {
            this.testName = testName;
            this.writeTimeNs = writeTimeNs;
            this.readTimeNs = readTimeNs;
            this.concurrentReadTimeNs = concurrentReadTimeNs;
            this.writeThroughputMBps = writeThroughputMBps;
            this.readThroughputMBps = readThroughputMBps;
            this.concurrentReadThroughputMBps = concurrentReadThroughputMBps;
            this.memoryUsageBytes = memoryUsageBytes;
        }
        
        @Override
        public String toString() {
            return String.format(
                "%s:\n" +
                "  Write: %.2f ms (%.2f MB/s)\n" +
                "  Read:  %.2f ms (%.2f MB/s)\n" +
                "  Concurrent Read: %.2f ms (%.2f MB/s)\n" +
                "  Memory: %.2f MB\n",
                testName,
                writeTimeNs / 1_000_000.0, writeThroughputMBps,
                readTimeNs / 1_000_000.0, readThroughputMBps,
                concurrentReadTimeNs / 1_000_000.0, concurrentReadThroughputMBps,
                memoryUsageBytes / (1024.0 * 1024.0)
            );
        }
    }
    
    private final SpectrumGenerator generator = new SpectrumGenerator();
    
    /**
     * Benchmark a ScratchList implementation with realistic workloads
     */
    public BenchmarkResults benchmarkList(String testName, ScratchList<byte[]> list) throws Exception {
        System.out.println("Benchmarking: " + testName);
        
        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runSingleBenchmark(list, false);
            if (list instanceof AutoCloseable) {
                ((AutoCloseable) list).close();
            }
            list = createFreshList(list);
        }
        
        // Actual benchmark runs
        long totalWriteTime = 0;
        long totalReadTime = 0;
        long totalConcurrentReadTime = 0;
        
        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            BenchmarkResults result = runSingleBenchmark(list, true);
            totalWriteTime += result.writeTimeNs;
            totalReadTime += result.readTimeNs;
            totalConcurrentReadTime += result.concurrentReadTimeNs;
            
            if (list instanceof AutoCloseable) {
                ((AutoCloseable) list).close();
            }
            list = createFreshList(list);
        }
        
        // Calculate averages
        long avgWriteTime = totalWriteTime / BENCHMARK_ITERATIONS;
        long avgReadTime = totalReadTime / BENCHMARK_ITERATIONS;
        long avgConcurrentReadTime = totalConcurrentReadTime / BENCHMARK_ITERATIONS;
        
        // Calculate throughput (approximate - based on float[2048] = 8KB per spectrum)
        double spectrumSizeKB = SPECTRUM_SIZE * 4 / 1024.0;
        double datasetSizeMB = DATASET_SIZE * spectrumSizeKB / 1024.0;
        
        double writeThroughput = datasetSizeMB / (avgWriteTime / 1_000_000_000.0);
        double readThroughput = datasetSizeMB / (avgReadTime / 1_000_000_000.0);
        double concurrentReadThroughput = datasetSizeMB / (avgConcurrentReadTime / 1_000_000_000.0);
        
        // Memory usage estimation
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        Thread.sleep(100); // Let GC settle
        long memoryUsage = runtime.totalMemory() - runtime.freeMemory();
        
        return new BenchmarkResults(
            testName, avgWriteTime, avgReadTime, avgConcurrentReadTime,
            writeThroughput, readThroughput, concurrentReadThroughput, memoryUsage
        );
    }
    
    @SuppressWarnings("unchecked")
    private ScratchList<byte[]> createFreshList(ScratchList<byte[]> template) throws IOException {
        if (template instanceof org.peakaboo.framework.scratch.list.array.ScratchArrayList) {
            return ScratchLists.memoryBacked(template.getEncoder());
        } else {
            return ScratchLists.diskBacked(template.getEncoder());
        }
    }
    
    private BenchmarkResults runSingleBenchmark(ScratchList<byte[]> list, boolean measureMemory) throws Exception {
        // Phase 1: Write test (sequential writes)
        long writeStart = System.nanoTime();
        for (int i = 0; i < DATASET_SIZE; i++) {
            byte[] spectrum = generator.generateSpectrumBytes();
            list.add(spectrum);
        }
        long writeTime = System.nanoTime() - writeStart;
        
        // Phase 2: Sequential read test
        long readStart = System.nanoTime();
        for (int i = 0; i < DATASET_SIZE; i++) {
            byte[] spectrum = list.get(i);
            if (spectrum == null || spectrum.length == 0) {
                throw new RuntimeException("Invalid spectrum at index " + i);
            }
        }
        long readTime = System.nanoTime() - readStart;
        
        // Phase 3: Concurrent read test (simulates real usage)
        int numReaderThreads = Runtime.getRuntime().availableProcessors();
        int readsPerThread = DATASET_SIZE * 2; // Each thread reads more than the dataset size
        
        long concurrentReadTime = benchmarkConcurrentReads(list, numReaderThreads, readsPerThread);
        
        return new BenchmarkResults("Single Run", writeTime, readTime, concurrentReadTime, 0, 0, 0, 0);
    }
    
    private long benchmarkConcurrentReads(ScratchList<byte[]> list, int numThreads, int readsPerThread) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numThreads);
        AtomicLong totalOperations = new AtomicLong(0);
        
        // Create reader tasks
        for (int t = 0; t < numThreads; t++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for all threads to be ready
                    
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    for (int i = 0; i < readsPerThread; i++) {
                        int index = random.nextInt(DATASET_SIZE);
                        byte[] spectrum = list.get(index);
                        if (spectrum != null) {
                            totalOperations.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
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
        finishLatch.await();
        long duration = System.nanoTime() - start;
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.printf("  Concurrent reads: %d operations in %.2f ms (%.0f ops/sec)\n", 
            totalOperations.get(), duration / 1_000_000.0, 
            totalOperations.get() / (duration / 1_000_000_000.0));
        
        return duration;
    }
    
    /**
     * Run comprehensive benchmarks comparing memory and disk implementations
     */
    public static void main(String[] args) throws Exception {
        ConcurrencyBenchmark benchmark = new ConcurrencyBenchmark();
        List<BenchmarkResults> results = new ArrayList<>();
        
        System.out.println("=== ScratchList Concurrent Performance Benchmark ===");
        System.out.printf("Dataset: %d spectra of %d floats each (%.2f MB total)\n", 
            DATASET_SIZE, SPECTRUM_SIZE, 
            DATASET_SIZE * SPECTRUM_SIZE * 4 / (1024.0 * 1024.0));
        System.out.println();
        
        // Test memory-backed list
        try {
            ScratchList<byte[]> memoryList = ScratchLists.memoryBacked(
                new CompoundEncoder<>(Serializers.java(), Compressors.lz4fast())
            );
            results.add(benchmark.benchmarkList("Memory-backed (LZ4)", memoryList));
            if (memoryList instanceof AutoCloseable) {
                ((AutoCloseable) memoryList).close();
            }
        } catch (Exception e) {
            System.err.println("Memory-backed test failed: " + e.getMessage());
        }
        
        // Test disk-backed list
        try {
            ScratchList<byte[]> diskList = ScratchLists.diskBacked(
                new CompoundEncoder<>(Serializers.java(), Compressors.lz4fast())
            );
            results.add(benchmark.benchmarkList("Disk-backed (LZ4)", diskList));
            if (diskList instanceof AutoCloseable) {
                ((AutoCloseable) diskList).close();
            }
        } catch (Exception e) {
            System.err.println("Disk-backed test failed: " + e.getMessage());
        }
        
        // Print results summary
        System.out.println("\n=== BENCHMARK RESULTS ===");
        for (BenchmarkResults result : results) {
            System.out.println(result);
        }
        
        // Performance comparison
        if (results.size() >= 2) {
            BenchmarkResults memory = results.get(0);
            BenchmarkResults disk = results.get(1);
            
            System.out.println("=== PERFORMANCE COMPARISON ===");
            System.out.printf("Write Performance: Memory is %.2fx faster\n", 
                (double) disk.writeTimeNs / memory.writeTimeNs);
            System.out.printf("Read Performance: Memory is %.2fx faster\n", 
                (double) disk.readTimeNs / memory.readTimeNs);
            System.out.printf("Concurrent Read Performance: Memory is %.2fx faster\n", 
                (double) disk.concurrentReadTimeNs / memory.concurrentReadTimeNs);
        }
    }
}