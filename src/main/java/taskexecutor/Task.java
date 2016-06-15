package taskexecutor;

import commands.Command;
import com.google.common.collect.ImmutableList;

/**
 * @author Hoang Tung Dinh
 */
public final class Task {
    private final ImmutableList<Command> commands;
    private final TaskType taskType;

    private Task(ImmutableList<Command> commands, TaskType taskType) {
        this.commands = commands;
        this.taskType = taskType;
    }

    public static Task create(ImmutableList<Command> commands, TaskType taskType) {
        return new Task(commands, taskType);
    }

    public ImmutableList<Command> getCommands() {
        return commands;
    }

    public boolean hasHigherPriority(Task otherTask) {
        return taskType.hasHigherPriority(otherTask.taskType);
    }
}
