package taskexecutor;

import behavior.Command;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Hoang Tung Dinh
 */
public final class TaskExecutorService implements TaskExecutor {
    @Nullable
    private Task task;
    @Nullable
    private Future<?> future;

    private TaskExecutorService() {}

    public static TaskExecutorService create() {
        return new TaskExecutorService();
    }

    private void runTask() {
        checkNotNull(task, "Task must not be null when this method is called");
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Runnable runnable = RunBehaviors.create(task.getBehaviors());
        future = executorService.submit(runnable);
    }

    @Override
    public boolean submitTask(Task newTask) {
        if (task == null || newTask.hasHigherPriority(task)) {
            if (future != null) {
                future.cancel(true);
            }
            task = newTask;
            runTask();
            return true;
        }

        return false;
    }

    private static final class RunBehaviors implements Runnable {
        private final Iterable<Command> behaviors;

        private RunBehaviors(Iterable<Command> behaviors) {
            this.behaviors = behaviors;
        }

        public static RunBehaviors create(Iterable<Command> behaviors) {
            return new RunBehaviors(behaviors);
        }

        @Override
        public void run() {
            for (final Command behavior : behaviors) {
                behavior.execute();
            }
        }
    }
}
