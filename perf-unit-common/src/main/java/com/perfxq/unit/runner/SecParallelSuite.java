package com.perfxq.unit.runner;

import com.perfxq.unit.annotation.Parallel;
import com.perfxq.unit.suite.ClasspathFinderFactory;
import com.perfxq.unit.suite.ClasspathSuite;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class SecParallelSuite extends ClasspathSuite {

    public SecParallelSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super( klass, builder, new ClasspathFinderFactory());

        if (this.getChildren().size() == 1) {
            Runner child = this.getChildren().get(0);
            ((ParentRunner) child).setScheduler(new ParallelForkedScheduler());
        } else {
            setScheduler(new ParallelForkedScheduler());
        }
    }
}
