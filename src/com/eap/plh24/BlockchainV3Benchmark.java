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
public class BlockchainV3Benchmark {
    private BlockchainV3 blockchainV3;

    @Setup(Level.Trial)
    public void setUp() {
        blockchainV3 = new BlockchainV3();
    }

    @Benchmark
    @Threads(4)
    public void simulateAddProduct() throws SQLException {
        blockchainV3.simulateAddProduct("Strawberry", "Fruit", "Red strawberry", 4.99);
    }

    @Benchmark
    @Threads(5)
    public void simulateAddMultipleProductsConcurrently() {
        blockchainV3.simulateAddMultipleProductsConcurrently(5);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BlockchainV3Benchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
