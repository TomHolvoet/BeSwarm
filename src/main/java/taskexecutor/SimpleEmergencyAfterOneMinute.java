package taskexecutor;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Hoang Tung Dinh
 */
public final class SimpleEmergencyAfterOneMinute {
    private final Task task;
    private final TaskExecutor taskExecutor;

    private SimpleEmergencyAfterOneMinute(Task task, TaskExecutor taskExecutor) {
        this.task = task;
        this.taskExecutor = taskExecutor;
    }

    public static SimpleEmergencyAfterOneMinute create(Task task, TaskExecutor taskExecutor) {
        return new SimpleEmergencyAfterOneMinute(task, taskExecutor);
    }

    public void run() {
        try {
            SECONDS.sleep(60);
            taskExecutor.submitTask(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
