package com.eap.plh24;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.SQLException;

@State(Scope.Thread)
public class BlockchainV3Benchmark {
    private BlockchainV3 blockchainV3;

    @Setup(Level.Trial)
    public void setUp() {
        blockchainV3 = new BlockchainV3();
        // Any additional setup code
    }

    @Benchmark
    public void addProductBenchmark() throws SQLException {
        // Simulate adding a single product
        blockchainV3.simulateAddProduct("Strawberry", "Fruit", "Red strawberry", 4.99);
    }

    @Benchmark
    public void addMultipleProductsBenchmark() {
        // Simulate adding multiple products concurrently
        blockchainV3.simulateAddMultipleProductsConcurrently(5); // Example, adding 5 products concurrently
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BlockchainV3Benchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
