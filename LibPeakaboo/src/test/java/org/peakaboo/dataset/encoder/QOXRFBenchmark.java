package org.peakaboo.dataset.encoder;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple benchmark for QOXRF compression performance using real data.
 * Loads CSV files with comma-separated float values.
 */
public class QOXRFBenchmark {
    
    private final QOXRF encoder = new QOXRF();
    
    public static void main(String[] args) {
        QOXRFBenchmark benchmark = new QOXRFBenchmark();
        
        if (args.length == 0) {
            System.out.println("Usage: java QOXRFBenchmark <csv-file> [csv-file2 ...]");
            System.out.println("CSV format: comma-separated float values, one spectrum per line");
            return;
        }
        
        for (String file : args) {
            try {
                benchmark.benchmarkFile(file);
            } catch (Exception e) {
                System.err.println("Error benchmarking " + file + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public void benchmarkFile(String filename) throws IOException, ScratchException {
        System.out.println("\n=== Benchmarking: " + filename + " ===");
        
        // Load all spectra from CSV
        List<float[]> spectra = loadSpectraFromCSV(filename);
        System.out.println("Loaded " + spectra.size() + " spectra");
        
        if (spectra.isEmpty()) {
            System.out.println("No valid spectra found");
            return;
        }
        
        // Benchmark each spectrum
        for (int i = 0; i < spectra.size(); i++) {
            float[] data = spectra.get(i);
            System.out.println("\nSpectrum " + (i + 1) + " (" + data.length + " points):");
            
            Spectrum spectrum = new ArraySpectrum(data, false);
            BenchmarkResult result = benchmark(spectrum, 5_000_000);
            printResults(result, data.length);
        }
    }
    
    private List<float[]> loadSpectraFromCSV(String filename) throws IOException {
        List<float[]> spectra = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNum = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNum++;
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                try {
                    String[] values = line.split(",");
                    float[] spectrum = new float[values.length];
                    
                    for (int i = 0; i < values.length; i++) {
                        spectrum[i] = Float.parseFloat(values[i].trim());
                    }
                    
                    spectra.add(spectrum);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid line " + lineNum + ": " + e.getMessage());
                }
            }
        }
        
        return spectra;
    }
    
    private BenchmarkResult benchmark(Spectrum spectrum, int iterations) throws ScratchException {
        // Debug: Check a few key values before encoding
        System.out.printf("DEBUG: Original values around index 447: [%d]=%.1f, [%d]=%.1f, [%d]=%.1f%n",
            446, spectrum.get(446), 447, spectrum.get(447), 448, spectrum.get(448));
        
        // Warmup
        for (int i = 0; i < 10; i++) {
            byte[] encoded = encoder.encode(spectrum);
            encoder.decode(encoded);
        }
        
        // Measure encoding
        long encodeStart = System.nanoTime();
        byte[] encoded = null;
        for (int i = 0; i < iterations; i++) {
            encoded = encoder.encode(spectrum);
        }
        long encodeTime = System.nanoTime() - encodeStart;
        
        // Measure decoding  
        long decodeStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            encoder.decode(encoded);
        }
        long decodeTime = System.nanoTime() - decodeStart;
        
        // Verify correctness
        Spectrum decoded = encoder.decode(encoded);
        
        // Debug: Check the same values after decoding
        System.out.printf("DEBUG: Decoded values around index 447: [%d]=%.1f, [%d]=%.1f, [%d]=%.1f%n",
            446, decoded.get(446), 447, decoded.get(447), 448, decoded.get(448));
        
        boolean correct = verifyCorrectness(spectrum, decoded);
        
        return new BenchmarkResult(
            encodeTime / iterations,
            decodeTime / iterations, 
            encoded.length,
            spectrum.size() * 4, // Original size in bytes
            correct
        );
    }
    
    private boolean verifyCorrectness(Spectrum original, Spectrum decoded) {
        if (original.size() != decoded.size()) {
            System.err.println("Size mismatch: original=" + original.size() + ", decoded=" + decoded.size());
            return false;
        }
        
        int errors = 0;
        for (int i = 0; i < original.size(); i++) {
            float diff = Math.abs(original.get(i) - decoded.get(i));
            if (diff > 1e-4f) {
                if (errors < 10) { // Only print first 10 errors
                    System.err.printf("Mismatch at index %d: original=%.6f, decoded=%.6f, diff=%.6f%n", 
                        i, original.get(i), decoded.get(i), diff);
                }
                errors++;
            }
        }
        
        if (errors > 0) {
            System.err.println("Total errors: " + errors + " out of " + original.size());
        }
        
        return errors == 0;
    }
    
    private void printResults(BenchmarkResult result, int dataPoints) {
        System.out.printf("  Original size: %d bytes%n", result.originalSize);
        System.out.printf("  Compressed size: %d bytes%n", result.compressedSize);
        System.out.printf("  Compression ratio: %.2f:1%n", (double) result.originalSize / result.compressedSize);
        System.out.printf("  Encode time: %.2f µs%n", result.encodeTimeNs / 1000.0);
        System.out.printf("  Decode time: %.2f µs%n", result.decodeTimeNs / 1000.0);
        System.out.printf("  Encode throughput: %.1f MB/s%n", 
            result.originalSize * 1000.0 / result.encodeTimeNs);
        System.out.printf("  Decode throughput: %.1f MB/s%n", 
            result.originalSize * 1000.0 / result.decodeTimeNs);
        System.out.printf("  Correctness: %s%n", result.correct ? "PASS" : "FAIL");
    }
    
    private static class BenchmarkResult {
        final long encodeTimeNs;
        final long decodeTimeNs;
        final int compressedSize;
        final int originalSize;
        final boolean correct;
        
        BenchmarkResult(long encodeTimeNs, long decodeTimeNs, int compressedSize, 
                       int originalSize, boolean correct) {
            this.encodeTimeNs = encodeTimeNs;
            this.decodeTimeNs = decodeTimeNs;
            this.compressedSize = compressedSize;
            this.originalSize = originalSize;
            this.correct = correct;
        }
    }
}