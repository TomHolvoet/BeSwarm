package taskexecutor;

/**
 * @author Hoang Tung Dinh
 */
public interface TaskExecutor {
    /**
     * Submits a new task to the task executor.
     *
     * @param newTask the new task to be submitted
     * @return a notification whether the task is accepted or rejected
     */
    Status submitTask(Task newTask);

    enum Status {
        ACCEPTED, REJECTED
    }
}
