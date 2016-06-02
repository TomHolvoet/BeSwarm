package taskexecutor;

import behavior.Command;
import com.google.common.collect.ImmutableList;

/**
 * @author Hoang Tung Dinh
 */
public final class Task {
    private final ImmutableList<Command> behaviors;
    private final TaskType taskType;

    private Task(ImmutableList<Command> behaviors, TaskType taskType) {
        this.behaviors = behaviors;
        this.taskType = taskType;
    }

    public static Task create(ImmutableList<Command> behaviors, TaskType taskType) {
        return new Task(behaviors, taskType);
    }

    public ImmutableList<Command> getBehaviors() {
        return behaviors;
    }

    public boolean hasHigherPriority(Task otherTask) {
        return taskType.hasHigherPriority(otherTask.taskType);
    }
}
