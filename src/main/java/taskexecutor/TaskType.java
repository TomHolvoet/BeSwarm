package taskexecutor;

/**
 * @author Hoang Tung Dinh
 */
public enum TaskType {
    FIRST_ORDER_EMERGENCY(0),
    SECOND_ORDER_EMERGENCY(1),
    NORMAL_TASK(2);

    private final int taskPriority;

    TaskType(int taskPriority) {
        this.taskPriority = taskPriority;
    }

    public boolean hasHigherPriority(TaskType taskType) {
        return taskPriority < taskType.taskPriority;
    }
}
