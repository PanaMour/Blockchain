package com.eap.plh24;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2)
@Warmup(iterations = 1)
@Measurement(iterations = 4)
public class BlockchainV1Benchmark {
    private BlockchainV1 blockchainV1;

    @Setup(Level.Trial)
    public void setUp() {
        blockchainV1 = new BlockchainV1();
    }

    @Benchmark
    public void addProductBenchmark() throws SQLException {
        blockchainV1.simulateAddProduct("Apple", "Fruit", "Delicious red apple", 1.99);
    }

    @Benchmark
    public void addMultipleProductsBenchmark() throws SQLException {
        blockchainV1.simulateAddMultipleProducts(5);
    }


    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BlockchainV1Benchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
