package com.realtime.crossfire.jxclient.sound;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import org.jetbrains.annotations.NotNull;

/**
 * Executes non-blocking tasks for sound-related functions. All tasks are
 * executed as fast as possible and from a single thread.
 */
public class SoundTaskExecutor {

    /**
     * The pending tasks.
     */
    @NotNull
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    /**
     * The thread executing the {@link #tasks}.
     */
    @NotNull
    private final Thread thread = new Thread(this::executeTasks, "JXClient:SoundTaskExecutor");

    /**
     * Creates a new instance.
     */
    public SoundTaskExecutor() {
        thread.setDaemon(true);
    }

    /**
     * Activates this instance.
     */
    public void start() {
        thread.start();
    }

    /**
     * Executes the tasks from {@link #tasks}.
     */
    private void executeTasks() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                tasks.take().run();
            } catch (final InterruptedException ignored) {
                thread.interrupt();
                break;
            }
        }
    }

    /**
     * Schedules a task for execution. The task must not block as all tasks are
     * executed in the same thread.
     * @param task the task
     */
    public void execute(@NotNull final Runnable task) {
        //noinspection ObjectEquality
        if (Thread.currentThread() == thread) {
            throw new IllegalStateException("must not be called from a sound task");
        }
        tasks.offer(task);
    }

    /**
     * Schedules a task for execution. The task must not block as all tasks are
     * executed in the same thread.
     * @param task the task
     * @throws InterruptedException if the current thread was interrupted while
     * waiting for the shutdown
     */
    public void executeAndWait(@NotNull final Runnable task) throws InterruptedException {
        //noinspection ObjectEquality
        if (Thread.currentThread() == thread) {
            throw new IllegalStateException("must not be called from a sound task");
        }
        final Semaphore sem = new Semaphore(0);
        tasks.offer(() -> {
            try {
                task.run();
            } finally {
                sem.release();
            }
        });
        sem.acquire();
    }

}
