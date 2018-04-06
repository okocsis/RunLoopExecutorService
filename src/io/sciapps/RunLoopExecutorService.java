package io.sciapps;

import com.sun.istack.internal.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

interface SetupFinishedCallback extends Runnable {
}

public class RunLoopExecutorService extends AbstractExecutorService {
    private final BlockingQueue<RunnableFuture> workQueue = new LinkedBlockingQueue<RunnableFuture>();

    private RunnableFuture inProgressTask = null;
    private volatile boolean isShuttingDown = false;
    private boolean isTerminated = false;

    public void startRunLoop(SetupFinishedCallback firstTask) {
        if (firstTask != null) {
            this.submit(firstTask);
        }
        this.startRunLoop();
    }

    public void startRunLoop() {
        while (true) {
            try {
                RunnableFuture task = this.workQueue.take();
                this.inProgressTask = task;
                task.run();
                this.inProgressTask = null;

                if (this.isShuttingDown) {
                    this.isTerminated = true;
                    break;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doLastVoidTask() {
        this.workQueue.add(new FutureTask(() -> {}, null));
    }

    private List<Runnable> terminate(boolean mayInterruptIfRunning) {
        this.isShuttingDown = true;
        if (this.workQueue.isEmpty()) {
            this.doLastVoidTask();
            return new LinkedList<>();
        }

        this.workQueue.forEach((task) -> task.cancel(mayInterruptIfRunning));
        this.workQueue.clear();
        this.inProgressTask.cancel(mayInterruptIfRunning);
        RunnableFuture task = this.inProgressTask;
        this.inProgressTask = null;

        List<Runnable> stillRunning = new LinkedList<>();
        if (!task.isDone()) {
            stillRunning.add(task);
        }

        return stillRunning;
    }

    @Override
    public void shutdown() {
        this.terminate(true);
    }

    @Override
    public List<Runnable> shutdownNow() {
        return this.terminate(true);
    }

    @Override
    public boolean isShutdown() {
        return this.isShuttingDown;
    }

    @Override
    public boolean isTerminated() {
        return this.isTerminated;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        //FIXME: need some better strategy here, it doesn't actually do the awaiting part
        this.terminate(false);
        return true;
    }

    @Override
    public void execute(Runnable command) {
        if (this.isShuttingDown) {
            return;
        }
        RunnableFuture future;
        if (command instanceof RunnableFuture) {
            future = (RunnableFuture) command;
        } else {
            future = new FutureTask(command, null);
        }
        this.workQueue.add(future);
    }
}
