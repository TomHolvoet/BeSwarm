package taskexecutor;

import com.google.common.collect.ImmutableList;
import commands.Command;

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

    public static Task create(TaskType taskType, Command... commands) {
        ImmutableList<Command> commandList = ImmutableList.<Command>copyOf(commands);
        return new Task(commandList, taskType);
    }

    public ImmutableList<Command> getCommands() {
        return commands;
    }

    public boolean hasHigherPriority(Task otherTask) {
        return taskType.hasHigherPriority(otherTask.taskType);
    }
}
