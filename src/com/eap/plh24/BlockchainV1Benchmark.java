package com.eap.plh24;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.sql.SQLException;

@State(Scope.Thread)
public class BlockchainV1Benchmark {
    private BlockchainV1 blockchainV1;

    @Setup(Level.Trial)
    public void setUp() {
        blockchainV1 = new BlockchainV1();
    }

    @Benchmark
    public void addProductBenchmark() throws SQLException {
        blockchainV1.simulateAddProduct();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BlockchainV1Benchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
