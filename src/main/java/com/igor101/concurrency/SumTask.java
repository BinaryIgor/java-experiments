package com.igor101.concurrency;

import java.util.concurrent.RecursiveTask;

public class SumTask extends RecursiveTask<Long> {

    @Override
    protected Long compute() {
        // TODO :split
        return 0L;
    }
}
