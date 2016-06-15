package taskexecutor;

/**
 * @author Hoang Tung Dinh
 */
public interface EmergencyNotifier {
    void registerTaskExecutor(TaskExecutor taskExecutor);
    void removeTaskExecutor(TaskExecutor taskExecutor);
}
