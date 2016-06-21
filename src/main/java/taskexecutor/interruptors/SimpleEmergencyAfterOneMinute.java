package taskexecutor.interruptors;

import java.util.ArrayList;
import java.util.Collection;

import taskexecutor.EmergencyNotifier;
import taskexecutor.Task;
import taskexecutor.TaskExecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Hoang Tung Dinh
 */
public final class SimpleEmergencyAfterOneMinute implements EmergencyNotifier {
    private final Task task;
    private final Collection<TaskExecutor> taskExecutors = new ArrayList<>();

    private SimpleEmergencyAfterOneMinute(Task task) {
        this.task = task;
    }

    public static SimpleEmergencyAfterOneMinute create(Task task) {
        return new SimpleEmergencyAfterOneMinute(task);
    }

    public void run() {
        try {
            SECONDS.sleep(60);
            for (final TaskExecutor taskExecutor : taskExecutors) {
                taskExecutor.submitTask(task);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerTaskExecutor(TaskExecutor taskExecutor) {
        taskExecutors.add(taskExecutor);
    }

    @Override
    public void removeTaskExecutor(TaskExecutor taskExecutor) {
        taskExecutors.remove(taskExecutor);
    }
}
