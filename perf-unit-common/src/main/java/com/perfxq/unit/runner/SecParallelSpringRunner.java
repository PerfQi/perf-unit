package com.perfxq.unit.runner;

import com.perfxq.unit.annotation.Parallel;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Annotation;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class SecParallelSpringRunner extends SpringJUnit4ClassRunner{
    public SecParallelSpringRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        setScheduler(new ParallelScheduler(createExecutor(clazz)));
    }

    private static ExecutorService createExecutor(Class<?> type) {
        Parallel parallel = SecParallelSpringRunner.annotationOfClass(type, Parallel.class);
        if (parallel != null) {
            return newFixedThreadPool(parallel.count(),
                    new ConcurrentTestRunnerThreadFactory());
        }
        return newCachedThreadPool(new ConcurrentTestRunnerThreadFactory());
    }

    private static class ConcurrentTestRunnerThreadFactory implements
            ThreadFactory {
        private AtomicLong count = new AtomicLong();

        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, SecParallelSpringRunner.class.getSimpleName()
                    + "-Thread-" + count.getAndIncrement());
        }
    }

    public static <T extends Annotation> T annotationOfClass(Class<?> type,
                                                             Class<T> annotationClass) {
        T classAnnotation = null;
        while (classAnnotation == null && type.getSuperclass() != null) {
            classAnnotation = type.getAnnotation(annotationClass);
            type = type.getSuperclass();
        }
        return classAnnotation;
    }
}
