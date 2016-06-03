package taskexecutor;

/**
 * @author Hoang Tung Dinh
 */
public interface TaskExecutor {
    Status submitTask(Task newTask);

    enum Status {
        ACCEPTED, REJECTED
    }
}
