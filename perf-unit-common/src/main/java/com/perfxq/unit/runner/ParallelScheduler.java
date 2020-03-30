package com.perfxq.unit.runner;

import com.perfxq.unit.datasource.dynamic.SecDataSourceContextHolder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runners.model.RunnerScheduler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class ParallelScheduler implements RunnerScheduler {
    private static final Log log = LogFactory.getLog(ParallelScheduler.class);

    private final ExecutorService executor;

    private final CompletionService<String> completionService;

    private Queue<Future<String>> tasks = new LinkedList<Future<String>>();

    public ParallelScheduler(ExecutorService executor) {
        this.executor = executor;
        this.completionService = new ExecutorCompletionService<String>(executor);
    }

    public void schedule(final Runnable childStatement) {
        Future<String> future = completionService
            .submit(new Callable<String>() {
                public String call() {
                    childStatement.run();
                    return toString();
                }

                @Override
                public String toString() {
                    return childStatement.toString() + "_" + SecDataSourceContextHolder.getDataSourceKey();
                }
            });
        tasks.add(future);
    }

    public void finished() {
        try {
            while (!tasks.isEmpty()) {
                Future<String> task = completionService.take();
                try {
                    log.info("the task completed,the task info：" + task.get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                tasks.remove(task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            while (!tasks.isEmpty()) {
                tasks.poll().cancel(true);
            }
            executor.shutdownNow();
        }
    }

}

